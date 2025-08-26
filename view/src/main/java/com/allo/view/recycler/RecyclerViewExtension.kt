package com.allo.view.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.removeAllItemDecoration(){
    val itemDecorationCount = this.itemDecorationCount
    kotlin.runCatching {
        for (i in 0..itemDecorationCount){
            removeItemDecorationAt(0)
        }
    }
}

fun RecyclerView.findFirstCompletelyVisibleItemPosition():Int{
    return when(val layoutManager = layoutManager){
        is LinearLayoutManager -> layoutManager.findFirstCompletelyVisibleItemPosition()
        else-> 0
    }
}
fun RecyclerView.findLastCompletelyVisibleItemPosition():Int{
    return when(val layoutManager = layoutManager){
        is LinearLayoutManager -> layoutManager.findLastCompletelyVisibleItemPosition()
        else-> 0
    }
}
