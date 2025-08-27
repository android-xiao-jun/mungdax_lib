package com.allo.utils

import android.app.Activity

object ActivityExtension {
    /**
     * 统一处理允许录屏和非录屏 控制字段
     */
    @JvmField
    public var OPEN_RECORD = BuildConfig.DEBUG
}

inline fun <reified T> Activity.intentData(key: String) = lazy {
    intent?.extras?.get(key) as? T
}

inline fun <reified T> Activity.intentData(key: String, default: T) = lazy {
    intent?.extras?.get(key) as? T ?: default
}

inline fun <reified T> Activity.safeIntentData(name: String) = lazy<T> {
    checkNotNull(intent?.extras?.get(name) as? T) { "No intent value for key \"$name\"" }
}