package cz.movapp.app

import android.app.Application
import android.content.Context
import android.os.StrictMode
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

const val TAG = "MOVAPP"

fun Application.appModule() = (this as App).appModule

class App : Application() {

    lateinit var appModule: AppModule

    companion object {
        lateinit var ctx: Context
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
        instance = this
        ctx = applicationContext
        appModule = AppModule(this.applicationContext)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        appModule.appScope.cancel("onLowMemory() called by system")
        appModule.appScope = MainScope()
    }


}