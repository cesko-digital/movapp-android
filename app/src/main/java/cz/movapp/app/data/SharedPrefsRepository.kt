package cz.movapp.app.data

import android.content.Context

class SharedPrefsRepository (context: Context){

    private val PREFS_SETTINGS_NAME = "SETTINGS"
    private val PREFERED_LANGUAGE = "PREFLANGUAGE"
    val settings = context.getSharedPreferences(PREFS_SETTINGS_NAME, Context.MODE_PRIVATE)

    fun setPreferedLanguage(language: String) {
        settings.edit().putString(PREFERED_LANGUAGE, language).apply()
    }

    fun getPreferedLanguage(): String? {
        return settings.getString(PREFERED_LANGUAGE,null)
    }
}