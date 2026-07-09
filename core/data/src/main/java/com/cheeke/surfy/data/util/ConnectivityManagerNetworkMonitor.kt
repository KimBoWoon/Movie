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