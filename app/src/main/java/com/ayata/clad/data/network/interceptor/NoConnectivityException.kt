package com.ayata.clad.data.network.interceptor

import android.os.Message
import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String?
        get() = "No Internet Connection"
}
class EmptyException(val mymessage: String) : IOException() {
    override val message: String?
        get() = mymessage
}
