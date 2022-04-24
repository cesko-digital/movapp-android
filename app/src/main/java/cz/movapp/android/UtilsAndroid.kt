package cz.movapp.android

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.getSavableScrollState(): Int {
    return when (this.layoutManager) {
        null -> throw UnsupportedOperationException("RecyclerView: No LayoutManager set")
        is LinearLayoutManager -> (this.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        else -> throw UnsupportedOperationException("RecyclerView: Can't save scroll state. Unknown LayoutManager")
    }
}

fun RecyclerView.restoreSavableScrollState(position: Int) {
    val layoutManager = this.layoutManager as LinearLayoutManager?
    layoutManager?.scrollToPositionWithOffset(position, 0)
}