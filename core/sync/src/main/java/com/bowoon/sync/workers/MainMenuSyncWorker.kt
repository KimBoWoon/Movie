package com.bowoon.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.notifications.SystemTrayNotifier
import com.bowoon.sync.initializers.SyncConstraints
import com.bowoon.sync.initializers.syncForegroundInfo
import com.bowoon.sync.utils.calculateInitialDelay
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class MainMenuSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @param:Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val mainMenuRepository: MainMenuRepository,
    private val databaseRepository: DatabaseRepository,
    private val notifier: SystemTrayNotifier
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORKER_NAME = "MainMenuSyncWorker"

        fun startUpSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setInitialDelay(duration = calculateInitialDelay(), timeUnit = TimeUnit.MILLISECONDS)
                .addTag(tag = WORKER_NAME)
                .setConstraints(SyncConstraints)
                .setInputData(MainMenuSyncWorker::class.delegatedData(isForce))
                .build()
    }

    private val isForce = inputData.getBoolean(key = "IS_FORCE", defaultValue = false)

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(context = ioDispatcher) {
        async {
            mainMenuRepository.syncWith(
                isForce = isForce,
                notification = {
                    databaseRepository
                        .getNextWeekReleaseMovies()
                        .takeIf { it.isNotEmpty() }
                        ?.let { favoriteMovies ->
                            notifier.postMovieNotifications(movies = favoriteMovies)
                        }
                }
            )
        }.await()
            .let { isSuccess ->
                when (isSuccess) {
                    true -> Result.success()
                    false -> if (runAttemptCount > 5) Result.failure() else Result.retry()
                }
            }
    }
}