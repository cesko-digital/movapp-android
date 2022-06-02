package cz.movapp.app

import com.google.gson.reflect.TypeToken
import cz.movapp.android.DataStoreKey
import cz.movapp.app.data.LanguagePair

class LanguageStateKeys {
    companion object{
        val SELECTED_PAIR =
            DataStoreKey<LanguagePair>("LanguagePair", object : TypeToken<LanguagePair>() {}.type)
    }
}