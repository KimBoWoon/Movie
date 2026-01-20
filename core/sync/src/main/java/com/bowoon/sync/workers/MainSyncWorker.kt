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
import com.bowoon.sync.utils.calculateInitialDelay
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class MainSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @param:Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val userDateRepository: UserDataRepository,
    private val mainMenuRepository: MainMenuRepository,
    private val databaseRepository: DatabaseRepository,
    private val notifier: Notifier
) : CoroutineWorker(appContext, workerParams), Synchronizer {
    companion object {
        const val WORKER_NAME = "MainSyncWorker"
        const val WORKER_TAG = "MAIN_MOVIE_SYNC_WORKER"
        const val PERIODIC_WORKER_TAG = "PERIODIC_WORKER_TAG"
        const val EXPEDITED_SYNC_WORK_NAME = "EXPEDITED_SYNC_WORK_NAME"
        const val IS_FORCE = "IS_FORCE"

        fun startUpExpeditedSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = EXPEDITED_SYNC_WORK_NAME)
                .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints = SyncConstraints)
                .setInputData(inputData = MainSyncWorker::class.delegatedData(isForce))
                .build()

        fun startUpSyncWork(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = WORKER_TAG)
                .setInitialDelay(duration = calculateInitialDelay(), timeUnit = TimeUnit.MILLISECONDS)
                .setConstraints(SyncConstraints)
                .setInputData(MainSyncWorker::class.delegatedData(isForce = false))
                .build()

        fun startPeriodicSyncWork(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<DelegatingWorker>(repeatInterval = 1, repeatIntervalTimeUnit = TimeUnit.DAYS)
                .addTag(tag = PERIODIC_WORKER_TAG)
                .setNextScheduleTimeOverride(
                    nextScheduleTimeOverrideMillis = LocalDateTime.now()
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                        .plusDays(1)
                        .toInstant(ZoneOffset.from(ZonedDateTime.now()))
                        .toEpochMilli()
                )
                .setInputData(inputData = MainSyncWorker::class.delegatedData(isForce = false))
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
            .firstOrNull()?.also { movies ->
                notifier.postMovieNotifications(movies = movies)
            }
        userDateRepository.updateWorkScheduleTime(value = Instant.now().toEpochMilli())
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