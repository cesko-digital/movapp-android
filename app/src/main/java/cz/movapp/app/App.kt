package cz.movapp.app

import android.app.Application
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

const val TAG = "MOVAPP"

fun Application.appModule() = (this as App).appModule

class App : Application() {

    lateinit var appModule: AppModule


    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this.applicationContext)
    }


    override fun onLowMemory() {
        super.onLowMemory()
        appModule.appScope.cancel("onLowMemory() called by system")
        appModule.appScope = MainScope()
    }

}