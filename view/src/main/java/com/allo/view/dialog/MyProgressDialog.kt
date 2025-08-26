package com.allo.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.allo.view.R

class MyProgressDialog private constructor(context: Context, text: CharSequence?, theme: Int) :
    Dialog(context, theme) {

    private var loadingImg: ImageView
    private var animation: RotateAnimation
    var backPressListener: (() -> Unit)? = null

    companion object {
        fun createDialog(
            context: Context,
            text: CharSequence? = null,
            cancelable: Boolean,
            canTouchOutsideCancel: Boolean? = true
        ): MyProgressDialog {
            val myProgressDialog = MyProgressDialog(
                context, text, R.style.MyProgressDialog
            )
            myProgressDialog.setCancelable(cancelable)
            myProgressDialog.setCanceledOnTouchOutside(canTouchOutsideCancel ?: false)
            try {
                myProgressDialog.show()
            } catch (e: Exception) {
                return myProgressDialog
            }
            return myProgressDialog
        }
    }

    init {
        setContentView(R.layout.layout_progress)
        loadingImg = findViewById<View>(R.id.loading_img) as ImageView
        if (!text.isNullOrEmpty()) {
            findViewById<TextView>(R.id.loading_tv).apply {
                this.text = text
                visibility = View.VISIBLE
            }
        }
        animation = RotateAnimation(
            0f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        animation.duration = 1000
        animation.repeatCount = Animation.INFINITE
        val lir = LinearInterpolator()
        animation.interpolator = lir
        loadingImg.animation = animation
        animation.startNow()
        setOnCancelListener { animation.cancel() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backPressListener?.invoke()
    }
}