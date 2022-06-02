package cz.movapp.android

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

/**
 * Mock of [GsonDataStore] backed by map
 */
fun gsonDataStoreMock(
    dataMap: MutableMap<Preferences.Key<String>, String?> = mutableMapOf(),
    appScope: CoroutineScope,
    dispacher: CoroutineDispatcher = Dispatchers.Unconfined
): GsonDataStore {
    val prefMock: Preferences = preferencesMock(dataMap)

    val dataStoreMock = GsonDataStore(appScope, object : DataStore<Preferences> {
        override val data: Flow<Preferences>
            get() = flowOf(prefMock)

        override suspend fun updateData(transform: suspend (pref: Preferences) -> Preferences): Preferences {
            val pref2 = transform(prefMock)
            dataMap.putAll(pref2.asMap() as Map<Preferences.Key<String>, String>)
            return pref2
        }
    }, dispacher)
    return dataStoreMock
}


fun preferencesMock(dataMap: MutableMap<Preferences.Key<String>, String?>): Preferences {
    val prefMock: Preferences = mockCheck(Preferences::class.java)

    Mockito.doAnswer { invocation ->
        dataMap[invocation.arguments[0] as Preferences.Key<String>]
    }.whenever(prefMock).get(any<Preferences.Key<String>>())

    Mockito.doAnswer { invocation ->
        dataMap
    }.whenever(prefMock).asMap()
    return prefMock
}