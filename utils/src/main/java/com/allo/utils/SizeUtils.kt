package com.allo.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlin.math.sqrt

class SizeUtils private constructor() {
    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnGetSizeListener {
        fun onGetSize(view: View?)
    }

    companion object {
        /**
         * Value of dp to value of px.
         *
         * @param dpValue The value of dp.
         * @return value of px
         */
        @JvmStatic
        fun dp2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun dp2pxF(dpValue: Float): Float {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f)
        }

        /**
         * Value of px to value of dp.
         *
         * @param pxValue The value of px.
         * @return value of dp
         */
        fun px2dp(pxValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * Value of sp to value of px.
         *
         * @param spValue The value of sp.
         * @return value of px
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = Resources.getSystem().displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * Value of px to value of sp.
         *
         * @param pxValue The value of px.
         * @return value of sp
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = Resources.getSystem().displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * Converts an unpacked complex data value holding a dimension to its final floating
         * point value. The two parameters <var>unit</var> and <var>value</var>
         * are as in [TypedValue.TYPE_DIMENSION].
         *
         * @param value The value to apply the unit to.
         * @param unit  The unit to convert from.
         * @return The complex floating point value multiplied by the appropriate
         * metrics depending on its unit.
         */
        fun applyDimension(value: Float, unit: Int): Float {
            val metrics = Resources.getSystem().displayMetrics
            when (unit) {
                TypedValue.COMPLEX_UNIT_PX -> return value
                TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
                TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
                TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
                TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
                TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
            }
            return 0f
        }

        /**
         * Force get the size of view.
         *
         * e.g.
         * <pre>
         * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
         * Override
         * public void onGetSize(final View view) {
         * view.getWidth();
         * }
         * });
        </pre> *
         *
         * @param view     The view.
         * @param listener The get size listener.
         */
        fun forceGetViewSize(view: View, listener: OnGetSizeListener?) {
            view.post { listener?.onGetSize(view) }
        }

        /**
         * Return the width of view.
         *
         * @param view The view.
         * @return the width of view
         */
        fun getMeasuredWidth(view: View): Int {
            return measureView(view)[0]
        }

        /**
         * Return the height of view.
         *
         * @param view The view.
         * @return the height of view
         */
        fun getMeasuredHeight(view: View): Int {
            return measureView(view)[1]
        }

        /**
         * Measure the view.
         *
         * @param view The view.
         * @return arr[0]: view's width, arr[1]: view's height
         */
        fun measureView(view: View): IntArray {
            var lp = view.layoutParams
            if (lp == null) {
                lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
            val lpHeight = lp.height
            val heightSpec: Int
            heightSpec = if (lpHeight > 0) {
                View.MeasureSpec.makeMeasureSpec(
                    lpHeight,
                    View.MeasureSpec.EXACTLY
                )
            } else {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
            view.measure(widthSpec, heightSpec)
            return intArrayOf(view.measuredWidth, view.measuredHeight)
        }

        private var sScreenWidth = 0
        private var sScreenHeight = 0
        private var sDensity = 0f


        /**
         * 获取屏幕宽度
         */
        fun getScreenWidth(): Int {

            if (sScreenWidth == 0) {
                val displayMetrics = DisplayMetrics()
                val wm = getWindowManager()
                wm?.defaultDisplay?.getMetrics(displayMetrics)
                sScreenWidth = displayMetrics.widthPixels
            }
            return sScreenWidth
        }

        /**
         * 获取屏幕高度
         */
        fun getScreenHeight(): Int {

            if (sScreenHeight == 0) {
                val displayMetrics = DisplayMetrics()
                val wm = getWindowManager()
                wm?.defaultDisplay?.getMetrics(displayMetrics)
                sScreenHeight = displayMetrics.heightPixels
            }
            return sScreenHeight
        }

        fun getRealHeight(): Int {
            val outSize = Point()
            getWindowManager()?.defaultDisplay?.getRealSize(outSize)
            return outSize.y
        }

        /**
         * 获取屏幕密度
         */
        fun getDensity(): Float {

            if (sDensity == 0f) {
                val displayMetrics = DisplayMetrics()
                val wm = getWindowManager()
                wm!!.defaultDisplay.getMetrics(displayMetrics)
                sDensity = displayMetrics.density
            }
            return sDensity
        }

        private fun getWindowManager(): WindowManager? {
            return if (Utils.getApp() == null) {
                null
            } else Utils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

        fun getScreenInch(activity: Activity): Double {
            val vm = getWindowManager() ?: return 0.0
            val metrics = DisplayMetrics()
            vm.defaultDisplay.getMetrics(metrics)
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.display?.getRealSize(point)
            } else {
                vm.defaultDisplay.getSize(point)
            }
            val w: Double = (point.x / metrics.xdpi).toDouble() // unit is inch
            val h: Double = (point.y / metrics.ydpi).toDouble() // unit is inch
            return sqrt(w * w + h * h)
        }

    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}