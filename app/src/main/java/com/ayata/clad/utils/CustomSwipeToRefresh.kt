package com.ayata.clad.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class CustomSwipeToRefresh(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {
    private val mTouchSlop: Int
    private var mPrevX = 0f
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN -> mPrevX = MotionEvent.obtain(event).getX()
            MotionEvent.ACTION_MOVE -> {
                val eventX: Float = event.getX()
                val xDiff = Math.abs(eventX - mPrevX)
                if (xDiff > mTouchSlop) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }

    init {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()
    }
}