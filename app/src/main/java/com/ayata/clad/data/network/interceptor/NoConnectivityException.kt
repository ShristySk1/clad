package com.ayata.clad.data.network.interceptor

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String?
        get() = "No Internet Connection"
}
