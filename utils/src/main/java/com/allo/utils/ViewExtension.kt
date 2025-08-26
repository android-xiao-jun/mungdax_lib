package com.allo.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

fun View.setRoundCorner(radius:Float = RoundOutlineProvider.DEFAULT_RADIUS){
    val originOutlineProvider = outlineProvider
    if (originOutlineProvider !is RoundOutlineProvider
        || originOutlineProvider.radius == radius){
        outlineProvider = RoundOutlineProvider(radius = radius)
    }
    if (!clipToOutline){
        clipToOutline = true
    }
}

class RoundOutlineProvider(val radius: Float = DEFAULT_RADIUS): ViewOutlineProvider() {
    companion object{
        const val DEFAULT_RADIUS = 5F
    }
    override fun getOutline(view: View, outline: Outline?) {
        outline?.setRoundRect(0, 0, view.width, view.height, SizeUtils.dp2px(radius).toFloat())
    }
}