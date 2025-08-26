package com.allo.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.allo.utils.SizeUtils


import com.allo.view.R


abstract class BaseTopUpDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //去掉 dialog 的标题，需要在 setContentView() 之前

        dialog?.let {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            it.setCancelable(true)
            it.setCanceledOnTouchOutside(true)
        }

        dialog?.window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            val lp = it.attributes
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            //设置 dialog 在底部
            lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            //设置 dialog 动画
            lp.windowAnimations = R.style.TopUpDialogAnimation

            //去掉 dialog 默认的 padding
            it.decorView.setPadding(0, 0, 0, 0)
            it.attributes = lp
        }

        val rootView = bindContentView(inflater, container)
        rootView.minimumWidth = SizeUtils.getScreenWidth()
        onCreateView(rootView)
        return rootView
    }

    /**
     * @return dialog layout res id
     */
    protected abstract fun getContentViewResId(): Int

    protected open fun bindContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getContentViewResId(), container, false)
    }

    /**
     * 该方法在 onCreateView 时调用
     * 一般不再重写 onCreateView(LayoutInflater inflater...);
     */
    protected abstract fun onCreateView(rootView: View?)
}