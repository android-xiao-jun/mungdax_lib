package com.allo.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


object StatusBarUtils {
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"

    private val sTintStatusBarImpl: ITintStatusBar

    /**
     * 判断是否 MIUI
     */
    // ignore all exception
    val isMIUI: Boolean
        get() {
            var `is`: FileInputStream? = null
            try {
                `is` = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
                val prop = Properties()
                prop.load(`is`)
                return (prop.getProperty(KEY_MIUI_VERSION_CODE) != null
                        || prop.getProperty(KEY_MIUI_VERSION_NAME) != null
                        || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE) != null)
            } catch (e: Exception) {
                return false
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                    }

                }
            }
        }

    /**
     * 判断魅族 Flyme 系统
     */
    val isMeizu: Boolean
        get() = Build.DISPLAY.startsWith("Flyme")

    init {
        // 根据版本来设置实现
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sTintStatusBarImpl = TintStatusBarMImpl()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sTintStatusBarImpl = TintStatusBarLollipopImpl()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sTintStatusBarImpl = TintStatusBarKitkatImpl()
        } else {
            sTintStatusBarImpl = object : ITintStatusBar {
                override fun tintColor(window: Window, @ColorInt color: Int) {
                    // 空实现
                }
            }
        }
    }

    /**
     * 统一调用着色方法
     */
    fun tintColor(activity: Activity, color: String, defaultColor: Int) {
        val window = activity.window
        if (window == null || window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN > 0) {
            // 全屏页面不设置
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val c = UIUtils.parseColor(color, defaultColor)
            sTintStatusBarImpl.tintColor(window, c)
            if (!UIUtils.isColorDarker(c)) {
                setStatusBarDarkMode(window)
            }
        }
    }

    fun tintColor(activity: Activity, color: Int) {
        val window = activity.window
        if (window == null || window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN > 0) {
            // 全屏页面不设置
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sTintStatusBarImpl.tintColor(window, color)
            if (!UIUtils.isColorDarker(color)) {
                setStatusBarDarkMode(window)
            }
        }
    }

    fun getSystemBarHeight(activity: Activity): Int {
        val rectangle = Rect()
        val window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return contentViewTop - statusBarHeight
    }

    /**
     * 设置状态栏的 icon / text 颜色变深
     */
    private fun setStatusBarDarkMode(window: Window) {
        if (isMIUI) {
            val clazz = window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (isMeizu) {
            val params = window.attributes
            try {
                val darkFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(params)
                value = value or bit
                meizuFlags.setInt(params, value)
                window.attributes = params
                darkFlag.isAccessible = false
                meizuFlags.isAccessible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    interface ITintStatusBar {
        fun tintColor(window: Window, @ColorInt color: Int)
    }

    private class TintStatusBarMImpl : TintStatusBarLollipopImpl() {
        @TargetApi(23)
        override fun tintColor(window: Window, color: Int) {
            super.tintColor(window, color)
            //TODO 添加 icon 变深颜色

        }
    }

    private open class TintStatusBarLollipopImpl : ITintStatusBar {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun tintColor(window: Window, color: Int) {
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = color

            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_VISIBLE

            val contentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
            val firstChild = contentView.getChildAt(0)
            if (firstChild != null) {
                ViewCompat.setFitsSystemWindows(firstChild, false)
                ViewCompat.requestApplyInsets(firstChild)
            }
        }
    }

    private class TintStatusBarKitkatImpl : ITintStatusBar {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        override fun tintColor(window: Window, @ColorInt color: Int) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            val contentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
            val firstChild = contentView.getChildAt(0)

            var statusBarView: View? = window.decorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW)
            if (statusBarView != null) {
                (window.decorView as ViewGroup).removeView(statusBarView)
            }

            statusBarView = View(window.context)
            val statusBarHeight = getStatusBarHeight()
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight
            )
            layoutParams.gravity = Gravity.TOP
            statusBarView.layoutParams = layoutParams
            // 状态栏颜色
            statusBarView.setBackgroundColor(color)
            // 防止重复设置
            statusBarView.tag = TAG_FAKE_STATUS_BAR_VIEW
            (window.decorView as ViewGroup).addView(statusBarView)

            if (firstChild == null) {
                return
            }

            if (TAG_MARGIN_ADDED != firstChild.tag) {
                val lp = firstChild.layoutParams as FrameLayout.LayoutParams
                lp.topMargin += statusBarHeight
                firstChild.layoutParams = lp
                firstChild.tag = TAG_MARGIN_ADDED
            }

            ViewCompat.setFitsSystemWindows(firstChild, false)
        }

        companion object {
            // 避免重复设置
            private val TAG_FAKE_STATUS_BAR_VIEW = "statusBarView"
            private val TAG_MARGIN_ADDED = "marginAdded"
        }
    }

    fun hintStatusBarView(v:View?){
        v?:return
        val layoutParams = v.layoutParams
        layoutParams.height = getStatusBarHeight()
        v.layoutParams = layoutParams
    }

    fun getStatusBarHeight(): Int {
        var statusBarHeight = 0
        try {
            val res = Utils.getApp().resources
            // 反射获取系统状态栏高度
            val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = res.getDimensionPixelSize(resourceId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return statusBarHeight
    }

    fun getNavBarHeight(): Int {
        val res = Utils.getApp().resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    fun getStatusBarHeightFixResource(): Int {
        val height = getStatusBarHeight()
        return if (height > 0) {
            height
        } else {
            Math.ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * Utils.getApp().resources.displayMetrics.density).toDouble()).toInt()
        }
    }

    fun transparentStatus(activity: Activity) {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19) {
            val params = activity.window.attributes
            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS

            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if (Build.VERSION.SDK_INT >= 21) {
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    fun setTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        transparentStatusBar(activity)
        setRootView(activity)
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun transparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val parent = activity.findViewById<View>(android.R.id.content) as ViewGroup
        var i = 0
        val count = parent.childCount
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(true)
                childView.clipToPadding = true
            }
            i++
        }
    }

    /**
     * 判断是否存在虚拟按键
     *
     * @return
     */
    fun hasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        try {
            val rs = context.resources
            val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id)
            }

            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return hasNavigationBar
    }

    fun hintNavigationBarView(v: View?) {
        v ?: return
        val layoutParams = v.layoutParams
        layoutParams.height = getNavigationBarHeight(v.context)
        if (layoutParams.height == 0) {
            layoutParams.height = 1
        }
        v.layoutParams = layoutParams
    }


    fun getNavigationBarHeight(activity: Activity): Int {
        val config = BarConfig(activity)
        return config.getNavigationBarHeight()
    }

    fun getNavigationBarHeight(fragment: Fragment): Int {
        if (fragment.activity == null) {
            return 0
        }
        return getNavigationBarHeight(fragment.requireActivity())
    }

    fun getNavigationBarHeight(context: Context): Int {
        val bean: GestureUtils.GestureBean = GestureUtils.getGestureBean(context)
        return if (bean.isGesture && !bean.checkNavigation) {
            0
        } else {
            BarConfig.getNavigationBarHeightInternal(context)
        }
    }
}