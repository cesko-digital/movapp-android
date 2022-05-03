package cz.movapp.app

import android.app.Application
import android.content.SharedPreferences
import android.os.StrictMode
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

const val TAG = "MOVAPP"

fun Application.appModule() = (this as App).appModule

class App : Application() {

    lateinit var appModule: AppModule
    lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
        appModule = AppModule(this.applicationContext)
//        this?.getSharedPreferences(
//            getString("R.string.preference_file_key"), Context.MODE_PRIVATE)

    }

    fun getPreferedLanguageApp() {

    }

    fun setPreferedLanguageApp(language: String) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("language", language)
        editor.commit()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        appModule.appScope.cancel("onLowMemory() called by system")
        appModule.appScope = MainScope()
    }


}