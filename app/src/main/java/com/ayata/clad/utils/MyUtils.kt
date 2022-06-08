package com.ayata.clad.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ayata.clad.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
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

inline fun View.snack(
    message: String,
    left: Int = 10,
    top: Int = 10,
    right: Int = 10,
    bottom: Int = 10,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).apply {

        val params = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(left, top, right, bottom)
        params.gravity = Gravity.BOTTOM
        params.anchorGravity = Gravity.BOTTOM
        setActionTextColor(Color.WHITE)
        view.layoutParams = params
        show()
    }
}

fun Context.showToast(message: String, isSuccess: Boolean, yOffset: Int = 0, xOffset: Int = 0) {
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
fun String.removeBracket() = this?.let { this.replace("[", "").replace("]", "") }

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
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}

fun <T> LiveData<T>.observeOnceAfterInit(owner: LifecycleOwner, observer: (T) -> Unit) {
    var firstObservation = true

    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            if (firstObservation) {
                firstObservation = false
            } else {
                removeObserver(this)
                observer(value)
            }
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

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

fun validateTextField(textField: TextInputLayout): Boolean {
    val data = textField.editText!!.text.toString().trim()
    return if (data.isEmpty() or (data == " ")) {
        textField.error = "This field can't be empty"
        false
    } else {
        textField.error = null
        true
    }
}

fun EditText.validateEditText(): Boolean {
    if (this.getText().toString().trim().equals("")) {
        this.setError("This field can not be blank");
        return false
    }
    return true
}

fun TextView.setDifferentColor(
    context: Context,
    firstLetter: String,
    secondLetter: String,
    firstColor: Int = R.color.black,
    secondColor: Int = R.color.colorRed
) {
    this.text = SpannableStringBuilder()
        .color(ContextCompat.getColor(context, firstColor)) { append(firstLetter) }
        .color(ContextCompat.getColor(context, secondColor)) { append(secondLetter) }
}

fun TextView.changeColor(colorLight: Int, colorDark: Int, context: Context) {
    this.setTextColor(ContextCompat.getColor(context, colorDark));
    this.background.setTint(ContextCompat.getColor(context, colorLight));
}

fun TextView.setStockStatus(stock: String, context: Context) {
    var textToDisplay = stock
    if (stock.contains("Out of Stock", ignoreCase = true)) {
        this.changeColor(R.color.colorRedDark, R.color.white, context)
    } else if (stock.contains("In stock", ignoreCase = true)) {
//        this.changeColor( R.color.colorGreenDark, R.color.white, context)
        this.visibility = View.GONE
    } else {
        this.changeColor(R.color.colorYellowDark, R.color.white, context)
    }
    this.setText(textToDisplay)
}
fun <T> removeSlice(list: MutableList<T>, from: Int, end: Int) {
    list.removeAll(list.slice(from..end))
}

//val subClasses = DataModel::class.sealedSubclasses.filter { clazz -> clazz == DataModel.Image::class }
//val imageFromModel = listImage.filterIsInstance<DataModel.Image>()


//val jsonProducts = jsonObject.get("products").asJsonArray
//val type: Type =
//    object : TypeToken<ArrayList<ProductDetail?>?>() {}.type
//val list: ArrayList<ProductDetail> = Gson().fromJson(
//    jsonProducts,
//    type
//)

//val brandDetail= Gson().fromJson(jsonObject.get("brand"), BrandDetailResponse::class.java)



