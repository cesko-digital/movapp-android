package cz.movapp.app.ui.alphabet

import com.google.gson.reflect.TypeToken
import cz.movapp.android.StateStore

class AlphabetStateKeys{
    companion object{
        val SCROLL_POSITIONS_FROM =
            StateStore.Key<Map<String, Int>>("AlphabetState.scrollPositionsFrom", object : TypeToken<Map<String, Int>>() {}.type)

        val SCROLL_POSITIONS_TO =
            StateStore.Key<Map<String, Int>>("AlphabetState.scrollPositionsTo", object : TypeToken<Map<String, Int>>() {}.type)
    }
}