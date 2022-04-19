package com.ayata.clad.utils

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.google.android.material.slider.RangeSlider
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class MyCustomRangeSlider : RangeSlider {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        //in case this View is inside a ScrollView you can listen to OnScrollChangedListener to redraw the View
        viewTreeObserver.addOnScrollChangedListener { invalidate() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setSliderTooltipAlwaysVisible(this)
    }

    companion object {
        fun setSliderTooltipAlwaysVisible(slider: RangeSlider?) {
            try {
                val baseSliderCls: Class<*>? = RangeSlider::class.java.superclass
                if (baseSliderCls != null) {
                    val ensureLabelsAddedMethod: Method =
                        baseSliderCls.getDeclaredMethod("ensureLabelsAdded")
                    ensureLabelsAddedMethod.setAccessible(true)
                    ensureLabelsAddedMethod.invoke(slider)
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }
}