package com.cheeke.surfy.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Single
import kotlin.reflect.KClass

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltWorkerFactoryEntryPoint {
    fun hiltWorkerFactory(): HiltWorkerFactory
}

private const val WORKER_CLASS_NAME = "MovieInitWorker"
const val IS_FORCE = "IS_FORCE"

internal fun KClass<out RxWorker>.delegatedData(isForce: Boolean = false) =
    Data.Builder()
        .putString(WORKER_CLASS_NAME, qualifiedName)
        .putBoolean(IS_FORCE, isForce)
        .build()

class DelegatingWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : RxWorker(appContext, workerParams) {
    private val workerClassName =
        workerParams.inputData.getString(WORKER_CLASS_NAME).orEmpty()

    private val delegateWorker =
        EntryPointAccessors.fromApplication<HiltWorkerFactoryEntryPoint>(appContext)
            .hiltWorkerFactory()
            .createWorker(appContext, workerClassName, workerParams)
            as? RxWorker ?: throw IllegalArgumentException("Unable to find appropriate worker")

    override fun getForegroundInfo(): Single<ForegroundInfo> =
        delegateWorker.getForegroundInfo()

    override fun createWork(): Single<Result> =
        delegateWorker.createWork()
}