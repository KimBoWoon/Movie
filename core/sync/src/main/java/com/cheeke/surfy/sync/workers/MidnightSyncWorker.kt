package com.cheeke.surfy.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.cheeke.surfy.data.repository.SyncRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.util.Synchronizer
import com.cheeke.surfy.sync.initializers.SyncConstraints
import com.cheeke.surfy.sync.initializers.syncForegroundInfo
import com.cheeke.surfy.sync.utils.millisUntilNextMidnight
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class MidnightSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val userDateRepository: UserDataRepository,
    private val syncRepository: SyncRepository
) : RxWorker(appContext, workerParams), Synchronizer {
    companion object {
        const val WORKER_TAG = "MID_NIGHT_SYNC_WORKER"
        const val PERIODIC_WORKER_TAG = "PERIODIC_WORKER_TAG"
        const val EXPEDITED_SYNC_WORK_NAME = "EXPEDITED_SYNC_WORK_NAME"

        fun startUpExpeditedSyncWork(isForce: Boolean = false): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = EXPEDITED_SYNC_WORK_NAME)
                .setExpedited(policy = OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints = SyncConstraints)
                .setInputData(inputData = MidnightSyncWorker::class.delegatedData(isForce))
                .build()

        fun startUpSyncWork(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(tag = WORKER_TAG)
                .setInitialDelay(duration = millisUntilNextMidnight(), timeUnit = TimeUnit.MILLISECONDS)
                .setConstraints(SyncConstraints)
                .setInputData(MidnightSyncWorker::class.delegatedData(isForce = false))
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
                .setInputData(inputData = MidnightSyncWorker::class.delegatedData(isForce = false))
                .setConstraints(constraints = SyncConstraints)
                .build()
    }

    override fun getVersion(): Single<String> =
        userDateRepository.getMainDate().firstOrError()

    override fun getSyncInputData(): List<Pair<String, Any?>> = mutableListOf(
        Pair(
            first = IS_FORCE,
            second = inputData.getBoolean(key = IS_FORCE, defaultValue = false)
        )
    )

    override fun getForegroundInfo(): Single<ForegroundInfo> =
        Single.just(appContext.syncForegroundInfo())

    override fun createWork(): Single<Result> {
        return syncRepository.sync()
            .map { isSuccess ->
                when (isSuccess) {
                    true -> Result.success()
                    false -> if (runAttemptCount > 5) Result.failure() else Result.retry()
                }
            }.onErrorReturnItem(Result.failure())
    }
}