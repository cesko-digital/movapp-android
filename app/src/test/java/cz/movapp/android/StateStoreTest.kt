package cz.movapp.android

import androidx.datastore.preferences.core.Preferences
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.lang.reflect.Type


class StateStoreTest{

    @Test
    fun gsonGenericsDeserialization() {
        val stringBoolToInit = """{"true": 1}"""
        assertThat(Gson().fromJson(stringBoolToInit, HashMap::class.java)).isNotEqualTo(hashMapOf( true to 1))
        assertThat(Gson().fromJson(stringBoolToInit, HashMap::class.java)).isEqualTo(hashMapOf( "true" to 1.0))

        val boolToInit = """{true: 1}"""
        val fromJson:Map<Boolean, Int> = Gson().fromJson(boolToInit, object : TypeToken<Map<Boolean, Int>>() {}.type)
        assertThat(fromJson).isEqualTo(hashMapOf( true to 1))


        val fromJsonStr:Map<Boolean, Int> = Gson().fromJson(stringBoolToInit, object : TypeToken<Map<Boolean, Int>>() {}.type)
        assertThat(fromJsonStr).isEqualTo(hashMapOf( true to 1))
    }


    val k1 = StateStore.Key<String>("k1")
    val v1 = "v1"

    val k2 = StateStore.Key<String>("k3")
    val v2 = "v3"

    val k3 = StateStore.Key<Map<Boolean,Int>>("k2")
    val v3 = mapOf(true to 1)


    @Test
    fun `restore saves restores values`() {
        runBlocking {
            val dataMap = mutableMapOf<Preferences.Key<String>, String?>()
            val stateStoreMock = stateStoreMock(dataMap, this)

            stateStoreMock.saveState(k1, v1)
            assertThat(dataMap).isNotEmpty()
            assertThat(stateStoreMock.restoreState(k1).first()).isEqualTo(v1)

            stateStoreMock.saveState(k3, v3)
            stateStoreMock.saveState(k2, v2)

            println(dataMap.size)
            assertThat(dataMap).hasSize(3)
        }
    }

    @Test
    fun `restore saves restores generic values`() {
        runBlocking {
            val dataMap = mutableMapOf<Preferences.Key<String>, String?>()
            val stateStoreMock = stateStoreMock(dataMap, this)


            stateStoreMock.saveState(k3, v3)
            val restoreState = stateStoreMock.restoreState(k3,object : TypeToken<Map<Boolean, Int>>() {}.type)


            assertThat(restoreState.first()!!).isEqualTo(v3)
        }
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `restore throws exception when top level is generic type and Key type is null`() {
        runBlocking {
            val dataMap = mutableMapOf<Preferences.Key<String>, String?>()
            val stateStoreMock = stateStoreMock(dataMap, this)

            stateStoreMock.saveState(k3, v3)
            val restoreState = stateStoreMock.restoreState(k3)

            assertThat(restoreState.first()!!).isEqualTo(v4)
        }
    }

    val k4 = StateStore.Key<Map<Boolean,Map<Boolean, Int>>>("k4", object : TypeToken<Map<Boolean, Map<Boolean, Int>>>() {}.type)
    val v4 = mapOf(true to mapOf(true to 1))

    @Test
    fun `restore when Key type set`() {
        runBlocking {
            val dataMap = mutableMapOf<Preferences.Key<String>, String?>()
            val stateStoreMock = stateStoreMock(dataMap, this)


            stateStoreMock.saveState(k4, v4)
            val restoreState = stateStoreMock.restoreState(k4)


            assertThat(restoreState.first()!!).isEqualTo(v4)
        }
    }


    //TODO create GSONs TypeToken from reified to not duplicate types in declaration like
    // Key.create<Map<Boolean, Int>>("Key")
    // instead of
    // Key<Map<Boolean, Int>>("Key", object : TypeToken<Map<Boolean, Int>>() {}.type)
    //
    val k5 = TODOBetterKey.create<Map<Boolean,Map<Boolean, Int>>>("k5")
    val v5 = mapOf(true to mapOf(true to 1))

    //@Test
    fun `restore when Key type set2`() {
        runBlocking {
            val dataMap = mutableMapOf<Preferences.Key<String>, String?>()
            val stateStoreMock = stateStoreMock(dataMap, this)

            stateStoreMock.saveState(k5, v5)
            val restoreState = stateStoreMock.restoreState(k5)
            assertThat(restoreState.first()!!).isEqualTo(v5)
        }
    }

    /**
     * @param name
     * @param type type information for GSON deserialization. example for Map<Boolean, Int> = "object : TypeToken<Map<Boolean, Int>>() {}.type"
     */
    data class TODOBetterKey<T>(val name: String, val type: Type? = null) {
        companion object{
            inline fun <reified T> create(name: String, type :Type? = null): StateStore.Key<T> {
//                checkGenerics<T>(type)
                if (type == null && T::class.java.typeParameters?.isNotEmpty() == true) {
                    return StateStore.Key(name, TypeToken.get(T::class.java).type)
                } else {
                    return StateStore.Key(name)
                }

            }

            inline fun <reified T> checkGenerics(type: Type?) {
                if (type == null && T::class.java.typeParameters?.isNotEmpty() == true) {
                    throw UnsupportedOperationException("Top level generic types like Map<Boolean, Int> not support add type parameter")
                }
            }
        }
    }

}