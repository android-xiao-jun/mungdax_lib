package com.allo.view.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import kotlin.math.abs

class ScalableCardHelper @JvmOverloads constructor(
    pageChangeListener: OnPageChangeListener? = null
) {
    private val snapHelper = PagerSnapHelper()
    private var recyclerView: RecyclerView? = null
    private var pageChangeListenerRef: WeakReference<OnPageChangeListener>? = null
    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                pageScrolled()
            }
        }

    init {
        if (pageChangeListener != null)
            pageChangeListenerRef = WeakReference(pageChangeListener)
    }

    private fun pageScrolled() {
        val rv = recyclerView ?: return
        if (rv.childCount == 0) return
        val layoutManager = rv.layoutManager ?: return
        val snapingView = snapHelper.findSnapView(layoutManager) ?: return
        val snapingViewPosition = rv.getChildAdapterPosition(snapingView)
        val leftSnapingView = layoutManager.findViewByPosition(snapingViewPosition - 1)
        val rightSnapingView = layoutManager.findViewByPosition(snapingViewPosition + 1)
        val leftSnapingOffset = calculateOffset(rv, leftSnapingView)
        val rightSnapingOffset = calculateOffset(rv, rightSnapingView)
        val currentSnapingOffset = calculateOffset(rv, snapingView)
        snapingView.apply {
            scaleX = currentSnapingOffset
            scaleY = currentSnapingOffset
        }
        leftSnapingView?.apply {
            scaleX = leftSnapingOffset
            scaleY = leftSnapingOffset
        }
        rightSnapingView?.apply {
            scaleX = rightSnapingOffset
            scaleY = rightSnapingOffset
        }
        if (currentSnapingOffset >= 1) {
            pageChangeListenerRef?.get()?.onPageSelected(snapingViewPosition)
        }
    }

    val currentPage: Int
        get() {
            val rv = recyclerView ?: throw RuntimeException("should call attachToRecyclerView first")
            val page = snapHelper.findSnapView(rv.layoutManager) ?: return -1
            return rv.getChildAdapterPosition(page)
        }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(scrollListener)
        recyclerView.addItemDecoration(ScalableCardItemDecoration())
        recyclerView.post { pageScrolled() }
    }

    /**
     * 通过计算`view`中间点与[RecyclerView]的中间点的距离，算出`view`的偏移量。
     *
     * @param view              view
     * @return
     */
    private fun calculateOffset(recyclerView: RecyclerView, view: View?): Float {
        if (view == null) return (-1).toFloat()
        val layoutManager = recyclerView.layoutManager
        val isVertical = layoutManager!!.canScrollVertically()
        val viewStart = if (isVertical) view.top else view.left
        val viewEnd = if (isVertical) view.bottom else view.right
        val centerX = if (isVertical) recyclerView.height / 2 else recyclerView.width / 2
        val childCenter = (viewStart + viewEnd) / 2
        val distance = abs(childCenter - centerX)
        if (distance > centerX) return STAY_SCALE
        val offset = 1f - distance / centerX.toFloat()
        return (1f - STAY_SCALE) * offset + STAY_SCALE
    }

    fun detachFromRecyclerView(recyclerView: RecyclerView?) {
        recyclerView?.removeOnScrollListener(scrollListener)
        this.recyclerView = null
    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }

    private class ScalableCardItemDecoration : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val holder = parent.getChildViewHolder(view)
            val position =
                if (holder.adapterPosition == RecyclerView.NO_POSITION) holder.oldPosition else holder.adapterPosition
            val layoutManager = parent.layoutManager
            val itemCount = layoutManager!!.itemCount
            if (position != 0 && position != itemCount - 1) {
                return
            }
            val peekWidth = getPeekWidth(parent, view)
            val isVertical = layoutManager.canScrollVertically()
            //移除item时adapter position为-1。
            if (isVertical) {
                if (position == 0) {
                    outRect[0, peekWidth, 0] = 0
                } else if (position == itemCount - 1) {
                    outRect[0, 0, 0] = peekWidth
                } else {
                    outRect[0, 0, 0] = 0
                }
            } else {
                if (position == 0) {
                    outRect[peekWidth, 0, 0] = 0
                } else if (position == itemCount - 1) {
                    outRect[0, 0, peekWidth] = 0
                } else {
                    outRect[0, 0, 0] = 0
                }
            }
        }
    }

    companion object {
        private const val STAY_SCALE = 0.9F
        fun getPeekWidth(recyclerView: RecyclerView, itemView: View): Int {
            val layoutManager = recyclerView.layoutManager
            val isVertical = layoutManager!!.canScrollVertically()
            val position = recyclerView.getChildAdapterPosition(itemView)
            //TODO RecyclerView使用wrap_content时，获取的宽度可能会是0。
            var parentWidth = recyclerView.measuredWidth
            var parentHeight = recyclerView.measuredHeight //有时会拿到0
            parentWidth = if (parentWidth == 0) recyclerView.width else parentWidth
            parentHeight = if (parentHeight == 0) recyclerView.height else parentHeight
            val parentEnd = if (isVertical) parentHeight else parentWidth
            val parentCenter = parentEnd / 2
            var itemSize = if (isVertical) itemView.measuredHeight else itemView.measuredWidth
            if (itemSize == 0) {
                val layoutParams = itemView.layoutParams
                val widthMeasureSpec = RecyclerView.LayoutManager.getChildMeasureSpec(
                    parentWidth,
                    layoutManager.widthMode,
                    recyclerView.paddingLeft + recyclerView.paddingRight,
                    layoutParams.width, layoutManager.canScrollHorizontally()
                )
                val heightMeasureSpec = RecyclerView.LayoutManager.getChildMeasureSpec(
                    parentHeight,
                    layoutManager.heightMode,
                    recyclerView.paddingTop + recyclerView.paddingBottom,
                    layoutParams.height, layoutManager.canScrollVertically()
                )
                itemView.measure(widthMeasureSpec, heightMeasureSpec)
                itemSize = if (isVertical) itemView.measuredHeight else itemView.measuredWidth
            }


            /*
            计算ItemDecoration的大小，确保插入的大小正好使view的start + itemSize / 2等于parentCenter。
         */
            val startOffset = parentCenter - itemSize / 2
            val endOffset = parentEnd - (startOffset + itemSize)
            return if (position == 0) startOffset else endOffset
        }
    }
}