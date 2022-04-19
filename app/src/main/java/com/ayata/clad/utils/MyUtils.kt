package com.ayata.clad.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ayata.clad.R
import com.ayata.clad.brand.response.BrandDetailResponse
import com.ayata.clad.databinding.DialogShoppingSizeBinding
import com.ayata.clad.home.response.ProductDetail
import com.ayata.clad.shopping_bag.adapter.AdapterCircleText
import com.ayata.clad.shopping_bag.model.ModelCheckout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

fun Context.copyToClipboard(text: CharSequence){
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label",text)
    clipboard?.setPrimaryClip(clip)
}
fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
inline fun View.snack(message:String, left:Int = 10, top:Int = 10, right:Int = 10, bottom:Int = 10, duration:Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(this, message, duration).apply {

        val params = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT )
        params.setMargins(left, top, right, bottom)
        params.gravity = Gravity.BOTTOM
        params.anchorGravity = Gravity.BOTTOM
            setActionTextColor(Color.WHITE)
        view.layoutParams = params
        show()
    }
}
 fun Context.showToast(message: String, isSuccess: Boolean,yOffset:Int=0,xOffset:Int=0) {
    val toast = Toast(this)
    val view: View = LayoutInflater.from(this)
        .inflate(R.layout.custom_toast, null)
    val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
    val ivImage = view.findViewById<ImageView>(R.id.ivImage)
    val cardView: CardView = view.findViewById(R.id.cardBackground)
    tvMessage.text = message
    if (isSuccess) {
        cardView.setCardBackgroundColor(this.resources.getColor(R.color.colorGreenDark))
        ivImage.setImageResource(R.drawable.ic_success)
    } else {
        cardView.setCardBackgroundColor(this.resources.getColor(R.color.colorRedDark))
        ivImage.setImageResource(R.drawable.ic_info)
    }
    toast.setView(view)
    toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, xOffset, yOffset)
    toast.show()
}
fun String.removeDoubleQuote() = this?.let { this.replace("\"", "") }
fun String.removeBracket()= this?.let { this.replace("[", "").replace("]", "")}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}
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




//val jsonProducts = jsonObject.get("products").asJsonArray
//val type: Type =
//    object : TypeToken<ArrayList<ProductDetail?>?>() {}.type
//val list: ArrayList<ProductDetail> = Gson().fromJson(
//    jsonProducts,
//    type
//)

//val brandDetail= Gson().fromJson(jsonObject.get("brand"), BrandDetailResponse::class.java)



