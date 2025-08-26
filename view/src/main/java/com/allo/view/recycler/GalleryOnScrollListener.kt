package com.allo.view.recycler

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView


class CardLinearSnapHelper(var noNeedToScroll:Boolean = false) : LinearSnapHelper() {
    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray? {
        return if (noNeedToScroll) {
            intArrayOf(0, 0)
        } else {
            super.calculateDistanceToFinalSnap(layoutManager, targetView)
        }
    }
}