package com.allo.view.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.allo.utils.SizeUtils.Companion.dp2px
import kotlin.math.abs

class CardScaleHelper {
    private var recyclerView: RecyclerView? = null
    private var mScale = 0.9f // 两边视图scale
    private var mPagePadding = 15 // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
    private var mShowLeftCardWidth = 15 // 左边卡片显示大小
    private var mCardWidth = 0 // 卡片宽度
    private var mOnePageWidth = 0 // 滑动一页的距离
    private var mCardGalleryWidth = 0
    var currentItemPos = 0
    private var mCurrentItemOffset = 0
    private val mLinearSnapHelper = CardLinearSnapHelper()

    fun attachToRecyclerView(mRecyclerView: RecyclerView) {
        // 开启log会影响滑动体验, 调试时才开启
        this.recyclerView = mRecyclerView
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val adapter = recyclerView.adapter ?: return
                when(newState){
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        mLinearSnapHelper.noNeedToScroll = (mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(adapter.itemCount - 1))
                    }
                    else -> {
                        mLinearSnapHelper.noNeedToScroll = false

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                if (dx != 0) { //去掉奇怪的内存疯涨问题
                    mCurrentItemOffset += dx
                    computeCurrentItemPos()
                    onScrolledChangedCallback()
                }
            }
        })
        initWidth()
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView)
    }

    /**
     * 初始化卡片宽度
     */
    private fun initWidth() {
        recyclerView?.post {
            mCardGalleryWidth = recyclerView!!.width
            mCardWidth = mCardGalleryWidth - dp2px((2 * (mPagePadding + mShowLeftCardWidth)).toFloat())
            mOnePageWidth = mCardWidth
            recyclerView?.smoothScrollToPosition(currentItemPos)
            onScrolledChangedCallback()
        }
    }

    private fun getDestItemOffset(destPos: Int): Int {
        return mOnePageWidth * destPos
    }

    /**
     * 计算mCurrentItemOffset
     */
    private fun computeCurrentItemPos() {
        if (mOnePageWidth <= 0) return
        var pageChanged = false
        // 滑动超过一页说明已翻页
        if (abs(mCurrentItemOffset - currentItemPos * mOnePageWidth) >= mOnePageWidth) {
            pageChanged = true
        }
        if (pageChanged) {
            val tempPos = currentItemPos
            currentItemPos = mCurrentItemOffset / mOnePageWidth
        }
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private fun onScrolledChangedCallback() {
        val rv = recyclerView ?: return
        val offset = mCurrentItemOffset - currentItemPos * mOnePageWidth
        val percent = (abs(offset) * 1.0 / mOnePageWidth).coerceAtLeast(0.0001).toFloat()
        var leftView: View? = null
        var rightView: View? = null
        if (currentItemPos > 0) {
            leftView = recyclerView?.layoutManager?.findViewByPosition(currentItemPos - 1)
        }
        val currentView: View? = recyclerView?.layoutManager?.findViewByPosition(currentItemPos)
        if (currentItemPos < (recyclerView?.adapter?.itemCount ?: 0) - 1) {
            rightView = recyclerView?.layoutManager?.findViewByPosition(currentItemPos + 1)
        }
        if (leftView != null) {
            // y = (1 - mScale)x + mScale
            leftView.scaleY = (1 - mScale) * percent + mScale
        }
        if (currentView != null) {
            // y = (mScale - 1)x + 1
            currentView.scaleY = (mScale - 1) * percent + 1
        }
        if (rightView != null) {
            // y = (1 - mScale)x + mScale
            rightView.scaleY = (1 - mScale) * percent + mScale
        }
    }

    fun setScale(scale: Float) {
        mScale = scale
    }

    fun setPagePadding(pagePadding: Int) {
        mPagePadding = pagePadding
    }

    fun setShowLeftCardWidth(showLeftCardWidth: Int) {
        mShowLeftCardWidth = showLeftCardWidth
    }
}