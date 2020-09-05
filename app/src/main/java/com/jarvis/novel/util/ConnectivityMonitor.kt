package com.jarvis.novel.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.*


class ConnectivityMonitor(context: Context, lifecycleOwner: LifecycleOwner, private val callback: (Boolean) -> Unit) : LifecycleObserver {
    private var connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        .build()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onResume() {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        networkCallback.lastInternetConnectionCheck()
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onPause() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun toggleConnectionState(isConnected: Boolean) = callback.invoke(isConnected)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
//            lastInternetConnectionCheck()
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            lastInternetConnectionCheck()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            lastInternetConnectionCheck()
        }


        fun lastInternetConnectionCheck() {
            if (connectivityManager.allNetworks.isEmpty()) {
                toggleConnectionState(false)
                return
            }

            connectivityManager.allNetworks.forEach { network ->
                network?.let {
                    connectivityManager.getNetworkCapabilities(it)
                        ?.let { networkCapabilities ->
                            val netInternet =
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            val transportCellular =
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            val transportWifi =
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            val transportEthernet =
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            val transportVpn =
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

                            val isConnected = netInternet ||
                                    transportWifi || transportCellular ||
                                    transportEthernet || transportVpn

                            toggleConnectionState(isConnected)
                        }
                }
            }
        }
    }

}