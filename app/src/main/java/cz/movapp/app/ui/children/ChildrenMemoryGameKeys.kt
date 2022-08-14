package cz.movapp.app.ui.children

import com.google.gson.reflect.TypeToken
import cz.movapp.android.DataStoreKey

class ChildrenMemoryGameKeys {
    companion object {
        val MUTE =
            DataStoreKey<Boolean>(
                "MemoryGame.mute",
                object : TypeToken<Boolean>() {}.type
            )
        val BEST_SCORE =
            DataStoreKey<Int>(
                "MemoryGame.bestScore",
                object : TypeToken<Int>() {}.type
            )
    }
}