package com.cheeke.surfy.ui.components

import android.widget.FrameLayout
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.cheeke.surfy.analytics.api.LocalAnalyticsHelper
import com.cheeke.surfy.analytics.api.logPlayTrailer
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.VIDEO_RATIO
import com.cheeke.surfy.model.VideoInfo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun VideosComponent(
    scope: CoroutineScope,
    vodList: List<VideoInfo>,
    autoPlayTrailer: Boolean?
) {
    val pagerState = rememberPagerState { vodList.size }
    val analyticsHelper = LocalAnalyticsHelper.current

    HorizontalPager(
        state = pagerState
    ) { index ->
        val listener = remember(key1 = index, key2 = autoPlayTrailer, key3 = vodList) {
            object : YouTubePlayerListener {
                override fun onApiChange(youTubePlayer: YouTubePlayer) {
                    Log.d("onApiChange")
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    Log.d("onCurrentSecond > $second")
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    when (error) {
                        PlayerConstants.PlayerError.UNKNOWN -> Log.e("UNKNOWN")
                        PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> Log.e("INVALID_PARAMETER_IN_REQUEST")
                        PlayerConstants.PlayerError.HTML_5_PLAYER -> Log.e("HTML_5_PLAYER")
                        PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> {
                            Log.e("VIDEO_NOT_FOUND")
                            scope.launch {
                                pagerState.scrollToPage(index + 1)
                            }
                        }
                        PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> {
                            Log.e("VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER")
                            scope.launch {
                                pagerState.scrollToPage(index + 1)
                            }
                        }
                        PlayerConstants.PlayerError.REQUEST_MISSING_HTTP_REFERER -> Log.e("REQUEST_MISSING_HTTP_REFERER")
                    }
                }

                override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer,
                    playbackQuality: PlayerConstants.PlaybackQuality
                ) {
                    Log.d("onPlaybackQualityChange > $playbackQuality")
                }

                override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
                ) {
                    Log.d("onPlaybackRateChange > ${playbackRate.name}")
                }

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    vodList[index].key?.let { key ->
                        when (autoPlayTrailer) {
                            true -> youTubePlayer.loadVideo(videoId = key, startSeconds = 0f)
                            false -> youTubePlayer.cueVideo(videoId = key, startSeconds = 0f)
                            else -> youTubePlayer.cueVideo(videoId = key, startSeconds = 0f)
                        }
                    }
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    when (state) {
                        PlayerConstants.PlayerState.UNKNOWN -> Log.d("UNKNOWN")
                        PlayerConstants.PlayerState.UNSTARTED -> Log.d("UNSTARTED")
                        PlayerConstants.PlayerState.ENDED -> {
                            Log.d("ENDED")
                            scope.launch {
                                pagerState.scrollToPage(page = index + 1)
                            }
                        }
                        PlayerConstants.PlayerState.PLAYING -> {
                            Log.d("PLAYING")
                            analyticsHelper.logPlayTrailer(contentType = "YouTube", videoId = vodList[index].key.orEmpty())
                        }
                        PlayerConstants.PlayerState.PAUSED -> Log.d("PAUSED")
                        PlayerConstants.PlayerState.BUFFERING -> Log.d("BUFFERING")
                        PlayerConstants.PlayerState.VIDEO_CUED -> Log.d("VIDEO_CUED")
                    }
                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                    Log.d("onVideoDuration > $duration")
                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
                    Log.d("onVideoId > $videoId")
                }

                override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
                ) {
                    Log.d("onVideoLoadedFraction > $loadedFraction")
                }
            }
        }

        AndroidView(
            factory = { context ->
                YouTubePlayerView(context = context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        (context.resources.displayMetrics.widthPixels / VIDEO_RATIO).toInt()
                    )
                    addYouTubePlayerListener(youTubePlayerListener = listener)
                }
            },
            onRelease = { youTubePlayerView ->
                youTubePlayerView.removeYouTubePlayerListener(youTubePlayerListener = listener)
                youTubePlayerView.release()
            }
        )
    }
}