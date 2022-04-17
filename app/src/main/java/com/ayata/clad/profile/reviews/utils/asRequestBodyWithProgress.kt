package com.ayata.clad.profile.reviews.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean

/** Returns a new request body that transmits the content of this. */
fun File.asRequestBodyWithProgress(
    contentType: MediaType? = null,
    progressCallback: ((progress: Float) -> Unit)?
): RequestBody {
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun contentLength() = length()

        override fun writeTo(sink: BufferedSink) {
            val fileLength = contentLength()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val inSt = FileInputStream(this@asRequestBodyWithProgress)
            var uploaded = 0L
            inSt.use {
                var read: Int = inSt.read(buffer)
                val handler = Handler(Looper.getMainLooper())
                while (read != -1) {
                    progressCallback?.let {
                        uploaded += read
                        val progress = (uploaded.toDouble() / fileLength.toDouble()).toFloat()
                        handler.post { it(progress) }

                        sink.write(buffer, 0, read)
                    }
                    read = inSt.read(buffer)
                }
            }
        }
    }
}
//fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
//    observeForever(object: Observer<T> {
//        override fun onChanged(value: T) {
//            removeObserver(this)
//            observer(value)
//        }
//    })
//}
//fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
//    observe(lifecycleOwner, object : Observer<T> {
//        override fun onChanged(t: T?) {
//            observer.onChanged(t)
//            removeObserver(this)
//        }
//    })
//}
//fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
//    observe(owner, object: Observer<T> {
//        override fun onChanged(value: T) {
//            removeObserver(this)
//            observer(value)
//        }
//    })
//}
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, object : Observer<T> {
            override fun onChanged(t: T?) {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    override fun postValue(value: T) {
        mPending.set(true)
        super.postValue(value)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        setValue(null)
    }

    companion object {

        private val TAG = "SingleLiveEvent"
    }
}
/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}