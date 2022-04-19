package cz.movapp.app

import android.app.Application
import cz.movapp.app.data.AlphabetDatasource

fun Application.appModule() = (this as App).appModule

class App : Application() {


    lateinit var appModule: AppModule
    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(AlphabetDatasource(this.applicationContext))
    }


}