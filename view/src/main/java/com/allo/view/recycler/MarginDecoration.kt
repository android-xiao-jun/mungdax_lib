package com.allo.view.recycler

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MarginDecoration(val offset:Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)


        val lp = view.layoutParams as ViewGroup.LayoutParams

        val itemWidth = parent.width - 2 * offset

        if (lp.width != itemWidth) {
            lp.width = itemWidth
        }

    }
}