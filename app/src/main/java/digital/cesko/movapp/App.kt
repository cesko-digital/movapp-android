package digital.cesko.movapp

import android.app.Application
import digital.cesko.movapp.data.AlphabetDatasource

fun Application.appModule() = (this as App).appModule

class App : Application() {


    lateinit var appModule: AppModule
    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(AlphabetDatasource(this.applicationContext))
    }


}