package com.allo.view.dialog

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.annotation.FloatRange
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.allo.utils.SizeUtils.Companion.dp2px
import com.allo.utils.SizeUtils.Companion.getScreenWidth
import com.allo.view.R

/**
 * Author: baizhou
 * Date:   2019年08月9日
 * Desc：  只对Dialog的样式进行基础定义 继承后Dialog的逻辑或则构建方式 自行扩展
 */
abstract class SimpleDialog : DialogFragment() {
    /**
     * 背景昏暗度
     */
    private var mDimAmount = 0.5f

    /**
     * 显示位置
     */
    private var mGravity = Gravity.CENTER

    /**
     * 左右边距
     */
    private var mMargin = 0

    /**
     * 进入退出动画
     */
    private var mAnimStyle = R.style.dialog_anim_in_center

    /**
     * 加载风格
     */
    private var mStyle = R.style.base_dialog_style

    /**
     * 点击外部取消
     */
    private var mOutCancel = true

    /**
     * 默认 WRAP_CONTENT
     */
    private var mWidth: Int = 0

    /**
     * 默认 WRAP_CONTENT
     */
    private var mHeight: Int = 0

    protected lateinit var mContext: Activity

    protected lateinit var mView: View

    protected var progressDialog: MyProgressDialog? = null

    override fun onAttach(context: Activity) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(getLayoutId(), container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventAndData()
    }

    override fun onStart() {
        super.onStart()
        initParams()
    }

    private fun initParams() {
        setStyle(STYLE_NO_TITLE, mStyle)
        val window = dialog?.window
        if (window != null) {
            val params = window.attributes
            params.dimAmount = mDimAmount
            params.gravity = mGravity
            if (mWidth == 0) {
                params.width = getScreenWidth() - 2 * dp2px(mMargin.toFloat())
            } else {
                params.width = dp2px(mWidth.toFloat())
            }
            //设置dialog高度
            when {
                mHeight == 0 -> {
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT
                }
                mHeight > 0 -> {
                    params.height = dp2px(mHeight.toFloat())
                }
                else -> {
                    params.height = WindowManager.LayoutParams.MATCH_PARENT
                }
            }
            if (mAnimStyle != 0) {
                window.setWindowAnimations(mAnimStyle)
            }
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.attributes = params
        }
        isCancelable = mOutCancel
    }

    /**
     * 设置背景昏暗度
     * @param dimAmount
     * @return
     */
    fun setDimAmout(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float): SimpleDialog {
        mDimAmount = dimAmount
        return this
    }

    /**
     * 是否显示底部
     * @param gravity
     * @return
     */
    fun setGravity(gravity: Int): SimpleDialog {
        this.mGravity = gravity
        if (gravity == Gravity.BOTTOM)
            mAnimStyle = R.style.dialog_anim_in_bottom
        if (gravity == Gravity.TOP)
            mAnimStyle = R.style.dialog_anim_in_top
        return this
    }

    /**
     * 设置宽高
     * @param width
     * @param height
     * @return
     */
    fun setSize(width: Int, height: Int): SimpleDialog {
        mWidth = width
        mHeight = height
        return this
    }

    fun setHeight(height: Int): SimpleDialog {
        mHeight = height
        return this
    }

    /**
     * 设置左右margin
     * @param margin
     * @return
     */
    fun setMargin(margin: Int): SimpleDialog {
        mMargin = margin
        return this
    }

    /**
     * 设置显示样式
     * @param style
     * @return
     */
    fun setStyle(@StyleRes style: Int): SimpleDialog {
        mStyle = style
        return this
    }

    /**
     * 设置进入退出动画
     * @param animStyle
     * @return
     */
    fun setAnimStyle(@StyleRes animStyle: Int): SimpleDialog {
        mAnimStyle = animStyle
        return this
    }

    /**
     * 设置是否点击外部取消
     * @param outCancel
     * @return
     */
    fun setOutCancel(outCancel: Boolean): SimpleDialog {
        mOutCancel = outCancel
        return this
    }

    fun show(manager: FragmentManager?) {
        try {
            super.show(manager ?: return, System.currentTimeMillis().toString())
        } catch (ignore: Exception) {
        }
    }

    protected fun showLoading(text: String? = null) {
        if (progressDialog == null) {
            progressDialog = MyProgressDialog.createDialog(mContext, text,true)
        } else {
            progressDialog?.show()
        }
    }

    protected fun hideLoading() {
        try {
            if (progressDialog != null) {
                progressDialog?.cancel()
                progressDialog = null

            }
        } catch (ignored: Exception) {
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initEventAndData()
}


