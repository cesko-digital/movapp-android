package cz.movapp.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cz.movapp.android.StateStore
import cz.movapp.app.data.AlphabetDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings.datastore")

class AppModule(
    val appContext: Context,
    var appScope: CoroutineScope = MainScope(),
    val alphabetDataSource: AlphabetDatasource = AlphabetDatasource(appContext),
    val stateStore: StateStore = StateStore(appScope, appContext.dataStore)
)

