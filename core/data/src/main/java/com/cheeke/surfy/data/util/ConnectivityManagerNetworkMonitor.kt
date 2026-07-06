package com.cheeke.surfy.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
) : NetworkMonitor {
    override val isOnline: Flowable<Boolean> =
        Flowable.create({ emitter ->
            val connectivityManager = context.getSystemService<ConnectivityManager>()

            if (connectivityManager == null) {
                emitter.onNext(false)
                emitter.onComplete()
                return@create
            }

            val callback = object : ConnectivityManager.NetworkCallback() {
                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks += network
                    emitter.onNext(true)
                }

                override fun onLost(network: Network) {
                    networks -= network
                    emitter.onNext(networks.isNotEmpty())
                }
            }

            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(
                request,
                callback
            )

            emitter.onNext(connectivityManager.isCurrentlyConnected())
            emitter.setCancellable {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }, BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean =
        activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
}

//internal class ConnectivityManagerNetworkMonitor @Inject constructor(
//    @param:ApplicationContext private val context: Context,
//    @param:Dispatcher(Dispatchers.IO) private val ioDispatcher: CoroutineDispatcher
//) : NetworkMonitor {
//    override val isOnline: Flow<Boolean> = callbackFlow {
//        val connectivityManager = context.getSystemService<ConnectivityManager>()
//        if (connectivityManager == null) {
//            channel.trySend(false)
//            channel.close()
//            return@callbackFlow
//        }
//
//        /**
//         * The callback's methods are invoked on changes to *any* network matching the [NetworkRequest],
//         * not just the active network. So we can simply track the presence (or absence) of such [Network].
//         * 모든 네트워크 연결에 관해 호출되는 콜백이므로 네트워크 연결 여부를 쉽게 파악 가능
//         */
//        val callback = object : NetworkCallback() {
//            private val networks = mutableSetOf<Network>()
//
//            override fun onAvailable(network: Network) {
//                networks += network
//                channel.trySend(true)
//            }
//
//            override fun onLost(network: Network) {
//                networks -= network
//                channel.trySend(networks.isNotEmpty())
//            }
//        }
//
//        val request = Builder()
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .build()
//        connectivityManager.registerNetworkCallback(request, callback)
//
//        /**
//         * Sends the latest connectivity status to the underlying channel.
//         * 현재 어떤 네트워크에 연결되어 있는지 전달
//         */
//        channel.trySend(connectivityManager.isCurrentlyConnected())
//
//        awaitClose {
//            connectivityManager.unregisterNetworkCallback(callback)
//        }
//    }
//        .flowOn(ioDispatcher)
//        .conflate()
//
//    private fun ConnectivityManager.isCurrentlyConnected() = activeNetwork
//        ?.let(::getNetworkCapabilities)
//        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
//}