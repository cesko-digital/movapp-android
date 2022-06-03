package cz.movapp.android

import java.lang.reflect.Type

/**
 * @param name
 * @param type type information for GSON deserialization. example for Map<Boolean, Int> = "object : TypeToken<Map<Boolean, Int>>() {}.type"
 */
data class DataStoreKey<T>(val name: String, val type: Type? = null) {
}