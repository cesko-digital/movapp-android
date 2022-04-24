package cz.movapp.app.ui.alphabet

import com.google.gson.reflect.TypeToken
import cz.movapp.android.StateStore

class AlphabetStateKeys{
    companion object{
        val SCROLL_POSITIONS =
            StateStore.Key<Map<String, Int>>("AlphabetState.scrollPositions", object : TypeToken<Map<String, Int>>() {}.type)
    }
}