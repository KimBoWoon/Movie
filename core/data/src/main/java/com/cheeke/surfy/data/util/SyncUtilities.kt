package com.cheeke.surfy.data.util

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.Movie
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface Synchronizer {
    fun getVersion(): Single<String>
    fun Syncable.sync(): Single<Boolean> = this@sync.syncWith(this@Synchronizer)
    fun getSyncInputData(): List<Pair<String, Any?>>
    fun afterSync(): Completable = Completable.complete()
}

interface Syncable {
    fun syncWith(synchronizer: Synchronizer): Single<Boolean>
}

fun Synchronizer.updateMovieSync(
    updateChecker: Synchronizer.() -> Single<Boolean>,
    getList: () -> Single<List<Movie>>,
    modelDeleter: () -> Completable,
    modelUpdater: (movies: List<Movie>) -> Completable
): Single<Boolean> = updateChecker()
    .flatMap { shouldUpdate: Boolean ->
        if (shouldUpdate) {
            getList().flatMap { updateList: List<Movie> ->
                Log.d("changeListSync -> $updateList")

                modelDeleter()
                    .andThen(modelUpdater(updateList))
                    .andThen(
                        Single.fromCallable {
                            getSyncInputData().firstOrNull { it.first == "IS_FORCE" }?.second as? Boolean ?: false
                        }
                    )
                    .flatMapCompletable { forceUpdate: Boolean ->
                        if (!forceUpdate) afterSync() else Completable.complete()
                    }.toSingleDefault(true)
            }
        } else {
            Log.d("changeListSync -> update not necessary")
            Single.just(true)
        }
    }
    .onErrorReturn { throwable: Throwable ->
        Log.printStackTrace(tr = throwable)
        false
    }

fun Synchronizer.changeListSync(
    getList: () -> Single<List<Movie>>,
    modelDeleter: () -> Completable,
    modelUpdater: (movies: List<Movie>) -> Completable
): Single<Boolean> = getList()
    .flatMap { updateList: List<Movie> ->
        modelDeleter()
            .andThen(modelUpdater(updateList))
            .andThen(afterSync())
            .toSingleDefault(true)
    }
    .onErrorReturn { throwable: Throwable ->
        Log.printStackTrace(tr = throwable)
        false
    }