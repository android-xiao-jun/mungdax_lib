package com.allo.view.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.allo.utils.RegexUtils

class ContactsItemDecoration(builder: Builder) : BaseHeaderSectionItemDecoration(builder) {

    private var sortLetter: SortLetter? = null
    private var mTypeface: Typeface? = null

    init {
        sortLetter = builder.mSortLetter
        mTypeface = builder.mTypeface
    }

    var startPosition: Int
        get() = mStartPostion
        set(position) {
            mStartPostion = position
        }

    /**
     * 排序好的列表两个 item 之间的首字母不一样，则需要分组的 header
     */
    override fun hasHeader(position: Int): Boolean {
        if (position < mStartPostion) {
            return false
        }
        if (position - mStartPostion == 0) {
            return true
        }
        val p = position - mStartPostion
        // 两个分类不一样, 中间肯定有 header section
        val current = sortLetter?.getSortLetter(position - startPosition)
        val before = sortLetter?.getSortLetter(position - 1 - startPosition)
        return current != before
    }

    override fun drawHeader(
        c: Canvas,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        child: View,
        lp: RecyclerView.LayoutParams,
        position: Int
    ) {
        mPaint.color = mBackgroundColor
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        mPaint.color = mTextColor
        mPaint.textSize = mTextSize.toFloat()
        val title = sortLetter?.getSortLetter(position - startPosition)
        if (!title.isNullOrEmpty()) {
            mPaint.getTextBounds(title, 0, title.length, mTextBound)
            if (RegexUtils.uyghurFirst(title) && mTypeface != null) {
                mPaint.typeface = mTypeface
            } else {
                mPaint.typeface = null
            }
            val textLeft =
                if (startMargin == -1) child.paddingLeft.toFloat() else startMargin.toFloat()
            c.drawText(
                title,
                textLeft,
                (bottom - ((mHeadHeight shr 1) - (mTextBound.height() shr 1))).toFloat(),
                mPaint
            )
            /*mPaint.color = ContextCompat.getColor(Utils.getApp(), R.color.color_decoration)
            c.drawLine(
                textLeft + mTextBound.right + SizeUtils.dp2px(10f),
                (top + bottom) / 2f,
                right.toFloat() - SizeUtils.dp2px(45f),
                (top + bottom) / 2f,
                mPaint
            )*/
        }
    }

    /**
     * 当前 position 的 letter 不同于下一个 position 的 letter
     * 则当前 position 是该分组的结尾
     */
    override fun isEndOfGroup(position: Int): Boolean {
        val p = position - mStartPostion
        if (p < 0) {
            return false
        }
        val current = sortLetter?.getSortLetter(position - startPosition)
        val next = sortLetter?.getSortLetter(position - startPosition + 1)
        return if (TextUtils.isEmpty(next)) {
            true
        } else current != next
    }

    class Builder : BaseHeaderSectionItemDecoration.Builder<ContactsItemDecoration?, Builder?>() {

        internal var mSortLetter: SortLetter? = null
        internal var mTypeface: Typeface? = null

        override fun getThis(): Builder {
            return this
        }

        override fun build(): ContactsItemDecoration {
            return ContactsItemDecoration(this)
        }

        fun setSortLetter(sortLetter: SortLetter): Builder {
            mSortLetter = sortLetter
            return this
        }

        fun setTypeface(typeface: Typeface): Builder {
            mTypeface = typeface
            return this
        }
    }


    interface SortLetter {
        fun getSortLetter(position: Int): String?
    }

}