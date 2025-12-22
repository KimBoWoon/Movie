package com.bowoon.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.data.util.Synchronizer
import com.bowoon.notifications.Notifier
import com.bowoon.sync.initializers.SyncConstraints
import com.bowoon.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class MainMenuSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @param:Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val userDateRepository: UserDataRepository,
    private val mainMenuRepository: MainMenuRepository,
    private val databaseRepository: DatabaseRepository,
    private val notifier: Notifier
) : CoroutineWorker(appContext, workerParams), Synchronizer {
    companion object {
        const val WORKER_NAME = "MainMenuSyncWorker"
        const val EXPEDITED_SYNC_WORK_NAME = "EXPEDITED_SYNC_WORK_NAME"
        const val IS_FORCE = "IS_FORCE"

        fun startUpExpeditedSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = EXPEDITED_SYNC_WORK_NAME)
                .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints = SyncConstraints)
                .setInputData(inputData = MainMenuSyncWorker::class.delegatedData(isForce))
                .build()

//        fun startUpSyncWork(): OneTimeWorkRequest =
//            OneTimeWorkRequestBuilder<DelegatingWorker>()
//                .addTag(tag = WORKER_NAME)
//                .setInitialDelay(duration = calculateInitialDelay(), timeUnit = TimeUnit.MILLISECONDS)
//                .setConstraints(constraints = SyncConstraints)
//                .build()

        fun startUpPeriodicSyncWork(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<DelegatingWorker>(repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS)
                .addTag(tag = WORKER_NAME)
                .setConstraints(constraints = SyncConstraints)
                .build()
    }

    override suspend fun getVersion(): String =
        userDateRepository.getMainDate()

    override suspend fun updateVersion(update: () -> String) {
        val date = update()

        if (date.isNotEmpty()) {
            userDateRepository.updateMainDate(value = date)
        }
    }

    override fun getSyncInputData(): List<Pair<String, Any?>> = mutableListOf(
        Pair(
            first = IS_FORCE,
            second = inputData.getBoolean(key = IS_FORCE, defaultValue = false)
        )
    )

    override suspend fun afterSync() {
        databaseRepository
            .getNextWeekReleaseMovies()
            .map { it.takeIf { it.isNotEmpty() } }
            .let { favoriteMovies ->
                notifier.postMovieNotifications(movies = favoriteMovies.firstOrNull() ?: emptyList())
            }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(context = ioDispatcher) {
        async {
            mainMenuRepository.sync()
        }.await()
            .let { isSuccess ->
                when (isSuccess) {
                    true -> Result.success()
                    false -> if (runAttemptCount > 5) Result.failure() else Result.retry()
                }
            }
    }
}