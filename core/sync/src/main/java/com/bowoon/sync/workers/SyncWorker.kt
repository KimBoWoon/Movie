package com.bowoon.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers
import com.bowoon.data.repository.SyncRepository
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.sync.initializers.SyncConstraints
import com.bowoon.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val syncRepository: SyncRepository,
    private val tmdbRepository: TMDBRepository
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORKER_NAME = "MovieInitWorker"

        fun startUpSyncWork(isForce: Boolean = false) =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(SyncConstraints)
                .setInputData(SyncWorker::class.delegatedData(isForce))
                .build()
    }

    private val isForce = inputData.getBoolean("IS_FORCE", false)

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        awaitAll(
            async { syncRepository.syncWith(isForce) },
            async { tmdbRepository.syncWith() }
        ).all { it }.let {
            when (it) {
                true -> Result.Success()
                false -> Result.Retry()
            }
        }
    }
}