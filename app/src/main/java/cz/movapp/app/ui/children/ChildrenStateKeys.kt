package cz.movapp.app.ui.children

import com.google.gson.reflect.TypeToken
import cz.movapp.android.DataStoreKey

class ChildrenStateKeys {
    companion object{
        val SCROLL_POSITIONS =
            DataStoreKey<Int>("Children.scrollPositions", object : TypeToken<Int>() {}.type)
    }
}