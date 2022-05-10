package cz.movapp.app

import com.google.gson.reflect.TypeToken
import cz.movapp.android.StateStore
import cz.movapp.app.data.LanguagePair

class LanguageStateKeys {
    companion object{
        val SELECTED_PAIR =
            StateStore.Key<LanguagePair>("LanguagePair", object : TypeToken<LanguagePair>() {}.type)
    }
}