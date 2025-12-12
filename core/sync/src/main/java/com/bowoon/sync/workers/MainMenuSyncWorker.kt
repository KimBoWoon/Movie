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
import com.bowoon.notifications.SystemTrayNotifier
import com.bowoon.sync.initializers.SyncConstraints
import com.bowoon.sync.initializers.syncForegroundInfo
import com.bowoon.sync.utils.calculateInitialDelay
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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
    private val notifier: SystemTrayNotifier
) : CoroutineWorker(appContext, workerParams), Synchronizer {
    companion object {
        const val WORKER_NAME = "MainMenuSyncWorker"
        const val EXPEDITED_SYNC_WORK_NAME = "EXPEDITED_SYNC_WORK_NAME"

        fun startUpSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setInitialDelay(duration = calculateInitialDelay(), timeUnit = TimeUnit.MILLISECONDS)
                .addTag(tag = WORKER_NAME)
                .setConstraints(SyncConstraints)
                .setInputData(inputData = MainMenuSyncWorker::class.delegatedData(isForce))
                .build()

        fun startUpExpeditedSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = EXPEDITED_SYNC_WORK_NAME)
                .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints = SyncConstraints)
                .setInputData(inputData = MainMenuSyncWorker::class.delegatedData(isForce))
                .build()

        fun test(isForce: Boolean = false): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<DelegatingWorker>(repeatInterval = 20, repeatIntervalTimeUnit = TimeUnit.MINUTES)
                .setInputData(inputData = MainMenuSyncWorker::class.delegatedData(isForce))
                .build()
    }

    override suspend fun getChangeListVersions(): String =
        userDateRepository.internalData.map { it.updateDate }.firstOrNull() ?: ""

    override suspend fun updateChangeListVersions(update: () -> String) {
        userDateRepository.updateUserData(
            userData = userDateRepository.internalData.first().copy(updateDate = update()),
            isSync = false
        )
    }

    override fun getIsForce(): Boolean = inputData.getBoolean(key = "IS_FORCE", defaultValue = false)

    private val isForce = inputData.getBoolean(key = "IS_FORCE", defaultValue = false)

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(context = ioDispatcher) {
        async {
            mainMenuRepository.sync()
        }.await()
            .let { isSuccess ->
                when (isSuccess) {
                    true -> {
                        databaseRepository
                            .getNextWeekReleaseMoviesFlow()
                            .map { it.takeIf { it.isNotEmpty() } }
                            .let { favoriteMovies ->
                                notifier.postMovieNotifications(movies = favoriteMovies.firstOrNull() ?: emptyList())
                            }
                        Result.success()
                    }
                    false -> if (runAttemptCount > 5) Result.failure() else Result.retry()
                }
            }
    }
}