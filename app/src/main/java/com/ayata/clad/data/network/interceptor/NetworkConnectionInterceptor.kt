package com.ayata.clad.data.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ProtocolException
import java.net.Socket

class NetworkConnectionInterceptor(private val mContext: Context) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if (!isInternetAvailable()) {
            throw NoInternetException()
        } else {
//            var response: Response
//            response = try {
//                chain.proceed(chain.request())
//
//            } catch (p: ProtocolException) {
//                Response.Builder()
//                    .request(chain.request())
//                    .code(200)
//                            .protocol(Protocol.HTTP_1_1)
//                    .build()
//            }
//            response
                            chain.proceed(chain.request())

        }
    }

    val isConnected: Boolean
        get() = if (mContext != null) {
            val connectivityManager = mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } else false

    private fun isConnectionOn(): Boolean {
        mContext?.let {
            val connectivityManager = mContext
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connectivityManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    val network = connectivityManager.activeNetwork
                    val connection = connectivityManager.getNetworkCapabilities(network)
                    return connection != null && (
                            connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                } else {
                    val activeNetwork = connectivityManager.activeNetworkInfo
                    if (activeNetwork != null) {
                        return (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                                activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                    }
                    return false
                }
            }


        }
        return false

    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress("8.8.8.8", 53)

            sock.connect(sockaddr, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }

    }

    class NoConnectivityException : IOException() {
        override val message: String
            get() = "No network available, please check your WiFi or Data connection"
    }

    class NoInternetException : IOException() {
        override val message: String
            get() = "No internet available, please check your connected WIFi or Data"
    }

}