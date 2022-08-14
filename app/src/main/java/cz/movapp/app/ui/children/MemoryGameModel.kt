package cz.movapp.app.ui.children

import android.graphics.drawable.Drawable
import android.widget.ImageView

data class MemoryGameGridViewData(
    val coordinateX: Int,
    val coordinateY: Int,
    val item: ChildrenDictionaryData,
    val mainSound: Boolean,
    val drawable: Drawable?
)

data class MemoryGameCardData(
    val data: MemoryGameGridViewData,
    val image: ImageView
)
