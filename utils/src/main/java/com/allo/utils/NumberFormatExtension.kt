package com.allo.utils

import java.text.DecimalFormat


fun String?.toInt(fallback: Int = 0):Int {
    return this?.toIntOrNull()?:fallback
}

fun String?.toLong(fallback: Long = 0L):Long {
    return this?.toLongOrNull()?:fallback
}

fun String?.toShort(fallback: Short = 0):Short{
    return this?.toShort()?:fallback
}

fun Float?.dp2px():Int{
    return SizeUtils.dp2px(this ?: 0f)
}

fun Float?.sp2px():Int{
    return SizeUtils.sp2px(this ?: 0f)
}


fun Int.toBigDec() = when {
    this > 10000 -> "${DecimalFormat("#.0").format(toDouble() / 10000)}w}"
    this > 1000 -> "${DecimalFormat("#.0").format(toDouble() / 1000)}k}"
    else -> toString()
}