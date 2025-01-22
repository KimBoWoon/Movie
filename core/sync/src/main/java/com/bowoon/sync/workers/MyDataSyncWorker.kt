package com.bowoon.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.data.repository.MyDataRepository
import com.bowoon.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class MyDataSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val myDataRepository: MyDataRepository
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORKER_NAME = "MyDataSyncWorker"
        lateinit var workerId: UUID

        fun startUpSyncWork() =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .addTag(WORKER_NAME)
//                .setBackoffCriteria(
//                    BackoffPolicy.LINEAR,
//                    backoffDelay = 1,
//                    TimeUnit.SECONDS
//                )
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .setConstraints(SyncConstraints)
                .setInputData(MyDataSyncWorker::class.delegatedData())
                .build().also {
                    workerId = it.id
                }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        awaitAll(
            async { myDataRepository.syncWith() }
        ).all { it }
            .let {
                when (it) {
                    true -> Result.success()
                    false -> Result.failure()
                }
            }
    }
}