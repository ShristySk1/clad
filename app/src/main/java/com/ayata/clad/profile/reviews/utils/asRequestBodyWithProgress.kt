package com.ayata.clad.profile.reviews.utils

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

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
fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}