package com.allo.view.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class DividerGridItemDecoration() : ItemDecoration() {

    var vertical:Int = 0
    var horizontal:Int = 0

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    private fun isLastColum(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        when(val layoutManager = parent.layoutManager){
            is GridLayoutManager ->{
                val spanSize = layoutManager.spanSizeLookup.getSpanSize(pos)
                val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(pos, layoutManager.spanCount)
                if (spanIndex + spanSize == spanCount) { // 如果是最后一列，则不需要绘制右边
                    return true
                }
            }
        }

        return false
    }

    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val maxLine = layoutManager.spanSizeLookup.getSpanGroupIndex(childCount - 1, spanCount)
            val currentLine = layoutManager.spanSizeLookup.getSpanGroupIndex(pos, spanCount)
            if (currentLine >= maxLine) { // 如果是最后一行，则不需要绘制底部
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                var currentTimeLine = childCount
                currentTimeLine -= childCount % spanCount
                // 如果是最后一行，则不需要绘制底部
                return pos >= currentTimeLine
            } else {// StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.layoutManager?.getPosition(view) ?: return
        val spanCount = getSpanCount(parent)
        val childCount = parent.adapter?.itemCount ?: return
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) { // 如果是最后一行，则不需要绘制底
            outRect[0, 0, horizontal] = 0
        } else if (isLastColum(parent, itemPosition, spanCount, childCount)) { // 如果是最后一列，则不需要绘制右边
            outRect[0, 0, 0] = vertical
        } else {
            outRect[0, 0, horizontal] = vertical
        }
    }

}