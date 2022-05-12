package cz.movapp.app.ui.alphabet

import com.google.gson.reflect.TypeToken
import cz.movapp.android.DataStoreKey

class AlphabetStateKeys{
    companion object{
        val SCROLL_POSITIONS_FROM =
            DataStoreKey<Map<String, Int>>("AlphabetState.scrollPositionsFrom", object : TypeToken<Map<String, Int>>() {}.type)

        val SCROLL_POSITIONS_TO =
            DataStoreKey<Map<String, Int>>("AlphabetState.scrollPositionsTo", object : TypeToken<Map<String, Int>>() {}.type)
    }
}