package com.zhihu.matisse.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.allo.utils.LanguageUtils
import com.allo.view.dialog.BaseDialog
import com.zhihu.matisse.R
import com.zhihu.matisse.databinding.MatisseDialogUserprofileConfirmBinding

class MatisseUserProfileConfirmDialog(
    context: FragmentActivity,
    private var mTitleText: CharSequence?,
    private var mContentText: CharSequence?,
    private var mConfirmText: CharSequence?,
    private var mCancelText: CharSequence?,
    private var mListener: ((Boolean?) -> Unit)?
) : BaseDialog() {

    companion object {

        fun show(
            ctx: FragmentActivity,
            title: CharSequence?,
            content: CharSequence?,
            confirm: CharSequence?,
            cancel: CharSequence?,
            listener: ((Boolean?) -> Unit)?
        ): MatisseUserProfileConfirmDialog {
            val fragmentManager = ctx.supportFragmentManager
            var dialog = fragmentManager.findFragmentByTag("UserProfileConfirmDialog")
            if (dialog !is MatisseUserProfileConfirmDialog) {
                dialog = MatisseUserProfileConfirmDialog(ctx, title, content, confirm, cancel, listener)
            }
            if (!ctx.isFinishing && !dialog.isAdded()) {
                fragmentManager.beginTransaction()
                    .add(dialog, "UserProfileConfirmDialog")
                    .commitAllowingStateLoss()
            }
            return dialog
        }

        fun show(
            ctx: FragmentActivity,
            tag: String,
            title: CharSequence?,
            content: CharSequence?,
            confirm: CharSequence?,
            cancel: CharSequence?,
            listener: ((Boolean?) -> Unit)?
        ): MatisseUserProfileConfirmDialog {
            val fragmentManager = ctx.supportFragmentManager
            var dialog = fragmentManager.findFragmentByTag(tag)
            if (dialog !is MatisseUserProfileConfirmDialog) {
                dialog = MatisseUserProfileConfirmDialog(ctx, title, content, confirm, cancel, listener)
            }
            if (!ctx.isFinishing && !dialog.isAdded()) {
                fragmentManager.beginTransaction()
                    .add(dialog, tag)
                    .commitAllowingStateLoss()
            }
            return dialog
        }
    }

    private lateinit var mBinding: MatisseDialogUserprofileConfirmBinding

    override fun bindContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = MatisseDialogUserprofileConfirmBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun getContentViewResId(): Int {
        return R.layout.matisse_dialog_userprofile_confirm
    }

    override fun onCreateView(rootView: View?) {
        if (!LanguageUtils.isZh()) {
            mBinding.llButton.orientation = LinearLayout.VERTICAL
        } else {
            mBinding.llButton.orientation = LinearLayout.HORIZONTAL
        }
        mBinding.llButton.requestLayout()
        //标题
        if (mTitleText?.isNotEmpty() == true) {
            mBinding.tvTitle.text = mTitleText
        } else mBinding.tvTitle.visibility = View.GONE
        //内容
        mBinding.tvContent.visibility = if (mContentText.isNullOrEmpty()) View.GONE else View.VISIBLE
        mBinding.tvContent.text = mContentText
        //左
        if (mCancelText?.isNotEmpty() == true) {
            mBinding.tvCancel.text = mCancelText
        } else {
            mBinding.tvCancel.visibility = View.GONE
            mBinding.vSpacer.visibility = View.GONE
        }
        //右
        if (mConfirmText?.isNotEmpty() == true) {
            mBinding.tvSure.text = mConfirmText
        } else {
            mBinding.tvSure.visibility = View.GONE
            mBinding.vSpacer.visibility = View.GONE
        }
        mBinding.tvCancel.setOnClickListener {
            mListener?.invoke(false)
            dismissAllowingStateLoss()
        }
        mBinding.tvSure.setOnClickListener {
            mListener?.invoke(true)
            dismissAllowingStateLoss()
        }
        mBinding.root.setOnClickListener {
            mListener?.invoke(null)
            dismiss()
        }
    }
}