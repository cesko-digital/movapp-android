package cz.movapp.android

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.reflect.Type


/**
 * @param name
 * @param type type information for GSON deserialization. example for Map<Boolean, Int> = "object : TypeToken<Map<Boolean, Int>>() {}.type"
 */
data class DataStoreKey<T>(val name: String, val type: Type? = null) {
}

/**
 * Key/Value pair disk storage with GSON serialization
 */
class DataStore(val appScope: CoroutineScope, dataStore: DataStore<Preferences>, val saveStateDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    DataStore<Preferences> by dataStore {

    companion object {

    }

    val gson = Gson()

    /**
     * !! Add @Keep annotation fro serialized classes.
     *
     * @return value or null if not found
     */
    inline fun <reified T> restoreState(
        key: DataStoreKey<T>,
    ): Flow<T?> {
        if (key.type == null && T::class.java.typeParameters?.isNotEmpty() == true) {
            throw UnsupportedOperationException("Top level generic types like Map<Boolean, Int> are not supported. Add type parameter")
        }

        return data
            .map { pref ->
                val value = pref[stringPreferencesKey(key.name)]
                if (value != null) {
                    try {
                        val fromJson = if (key.type != null) {
                            gson.fromJson(value, key.type)
                        } else {
                            gson.fromJson(value, T::class.java)
                        }

                        fromJson
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        null
                    }
                } else {
                    null
                }
            }
    }


    /**
     * @param type needs type info in runtime.
     * example for Map<Boolean, Int> = "object : TypeToken<Map<Boolean, Int>>() {}.type"
     *
     * !! Add @Keep annotation fro serialized classes.
     *
     * @return value or null if not found
     */
    inline fun <reified T> restoreState(
        key: DataStoreKey<T>,
        type: Type
    ): Flow<T?> {

        return data
            .map { pref ->
                val value = pref[stringPreferencesKey(key.name)]
                if (value != null) {
                    try {
                        val fromJson: T = gson.fromJson(value, type)
                        fromJson
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        null
                    }
                } else {
                    null
                }
            }
    }


    /**
     * !! add @Keep annotation fro serialized classes.
     */
    inline fun <reified T> saveState(key: DataStoreKey<T>, value: T) {
        appScope.launch(saveStateDispatcher) {
            this@DataStore.edit { pref: MutablePreferences ->
                pref[stringPreferencesKey(key.name)] = gson.toJson(value)
            }
        }
    }

}