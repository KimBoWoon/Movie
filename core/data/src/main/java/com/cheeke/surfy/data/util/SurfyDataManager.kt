package com.cheeke.surfy.data.util

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.model.Configuration
import com.cheeke.surfy.model.Genre
import com.cheeke.surfy.model.Language
import com.cheeke.surfy.model.LocaleOption
import com.cheeke.surfy.model.PosterSize
import com.cheeke.surfy.model.Regions
import com.cheeke.surfy.model.SurfyAppData
import com.cheeke.surfy.network.SettingRemoteDataSource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import jakarta.inject.Inject

class SurfyDataManager @Inject constructor(
    private val apis: SettingRemoteDataSource,
    private val userDataRepository: UserDataRepository,
    datastore: InternalDataSource,
    networkMonitor: NetworkMonitor
) : DataManager {
    private val disposables = CompositeDisposable()

    private sealed interface ResourceResult<out T> {
        data class Success<T>(val value: T) : ResourceResult<T>
        data class Failure(val throwable: Throwable) : ResourceResult<Nothing>
    }

    private fun <T : Any> Single<T>.toSafeFlowable(): Flowable<ResourceResult<T>> =
        map<ResourceResult<T>> { value: T -> ResourceResult.Success(value = value) }
            .toFlowable()
            .onErrorReturn { throwable: Throwable -> ResourceResult.Failure(throwable = throwable) }

    private val userData = datastore.userData
        .onErrorResumeNext { throwable: Throwable ->
            /* 로깅 처리 */
            Flowable.empty()
        }
        .replay(1)
        .refCount()

    override val localeFlow: Flowable<Locale> =
        userData
            .map { userDataEntry -> Locale(language = userDataEntry.language, region = userDataEntry.region) }
            .distinctUntilChanged()

    private val _surfyAppData =
        BehaviorProcessor.createDefault<SurfyAppDataState>(SurfyAppDataState.Loading)
    override val surfyAppData = _surfyAppData.hide()
    private val _tmdbConfiguration = BehaviorProcessor.create<Triple<Configuration, List<Language>, Regions>>()
    val tmdbConfiguration = _tmdbConfiguration.hide()
    private val _tmdbGenres = BehaviorProcessor.create<GenreData>()
    val tmdbGenres = _tmdbGenres.hide()

    init {
        disposables += localeFlow
            .subscribeBy(
                onNext = { loadGenres(language = it.language, region = it.region) },
                onError = { throwable: Throwable -> /* 로깅 처리 */ }
            )

        disposables += networkMonitor.isOnline
            .distinctUntilChanged()
            .filter { isOnline: Boolean -> isOnline }
            .subscribeBy(
                onNext = { loadTmdbConfiguration() },
                onError = { throwable: Throwable -> /* 로깅 처리 */ }
            )

        disposables += userData.take(1)
            .flatMap { internalData ->
                Flowable.combineLatest(
                    tmdbConfiguration,
                    tmdbGenres
                ) { (configurationResult, languageResult, regionResult), genresResult ->
                    val results = listOf(internalData, configurationResult, languageResult, regionResult, genresResult)
                    val firstFailure: ResourceResult.Failure? = results.filterIsInstance<ResourceResult.Failure>().firstOrNull()

                    val appDataState: SurfyAppDataState = if (firstFailure != null) {
                        SurfyAppDataState.Error(throwable = firstFailure.throwable)
                    } else {
                        SurfyAppDataState.Success(
                            data = SurfyAppData(
                                isAdult = internalData.isAdult,
                                autoPlayTrailer = internalData.isAutoPlayTrailer,
                                isDarkMode = internalData.isDarkMode,
                                updateDate = internalData.updateDate,
                                imageQuality = internalData.imageQuality,
                                secureBaseUrl = configurationResult.images?.secureBaseUrl.orEmpty(),
                                movieGenres = genresResult.movie,
                                tvGenres = genresResult.tv,
                                region = regionResult.results.orEmpty().map { regionItem ->
                                    LocaleOption(
                                        code = regionItem.iso31661.orEmpty(),
                                        label = regionItem.nativeName.orEmpty(),
                                        isSelected = internalData.region == regionItem.iso31661
                                    )
                                },
                                language = languageResult.map { languageItem ->
                                    LocaleOption(
                                        code = languageItem.iso6391.orEmpty(),
                                        label = languageItem.englishName.orEmpty(),
                                        isSelected = internalData.language == languageItem.iso6391
                                    )
                                },
                                posterSize = configurationResult.images?.posterSizes.orEmpty().map { size ->
                                    PosterSize(size = size, isSelected = internalData.imageQuality == size)
                                }
                            )
                        )
                    }
                    appDataState
                }
            }.startWithItem(SurfyAppDataState.Loading)
            .onErrorReturn { throwable: Throwable -> SurfyAppDataState.Error(throwable = throwable) }
            .subscribe(
                {
                    _surfyAppData.onNext(it)
                },
                {
                    Log.printStackTrace(it)
                    _surfyAppData.onNext(SurfyAppDataState.Error(throwable = it))
                }
            )
    }

    private fun loadTmdbConfiguration() {
        disposables += Flowable.combineLatest(
            apis.getConfiguration().toSafeFlowable(),
            apis.getAvailableLanguage().toSafeFlowable(),
            apis.getAvailableRegion().toSafeFlowable(),
        ) { configurationResult, languageResult, regionResult ->
            val configuration = (configurationResult as ResourceResult.Success).value
            val language = (languageResult as ResourceResult.Success).value
            val region = (regionResult as ResourceResult.Success).value
            Triple(configuration, language, region)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _tmdbConfiguration.onNext(it) })
    }

    private fun loadGenres(
        language: String,
        region: String
    ) {
        disposables += Flowable.combineLatest(
            apis.getMovieGenres(language = "$language-$region").toSafeFlowable(),
            apis.getTvGenres(language = "$language-$region").toSafeFlowable()
        ) { movieResult, tvResult ->
            val movie = (movieResult as ResourceResult.Success).value
            val tv = (tvResult as ResourceResult.Success).value
            GenreData(movie = movie.genres.orEmpty(), tv = tv.genres.orEmpty())
        }.distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _tmdbGenres.onNext(it) })
    }
}

data class Locale(val language: String, val region: String)
data class GenreData(val movie: List<Genre>, val tv: List<Genre>)