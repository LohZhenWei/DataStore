package com.sample.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.Key

interface DataStorage {
    //generic
    suspend fun <T> put(key: Preferences.Key<T>, value: T)
    fun <T> get(key: Preferences.Key<T>, default: T): Flow<T>

    suspend fun putString(key: Preferences.Key<String>, value: String)
    suspend fun putInt(key: Preferences.Key<Int>, value: Int)
    suspend fun putLong(key: Preferences.Key<Long>, value: Long)
    suspend fun putBoolean(key: Preferences.Key<Boolean>, value: Boolean)
    suspend fun putDouble(key: Preferences.Key<Double>, value: Double)
    suspend fun <T> putObject(key: Preferences.Key<String>, value: T)
    fun getString(key: Preferences.Key<String>, default: String = ""): Flow<String>
    fun getInt(key: Preferences.Key<Int>, default: Int = 0): Flow<Int>
    fun getLong(key: Preferences.Key<Long>, default: Long = 0L): Flow<Long>
    fun getBoolean(key: Preferences.Key<Boolean>, default: Boolean = false): Flow<Boolean>
    fun getDouble(key: Preferences.Key<Double>, default: Double = 0.0): Flow<Double>

    suspend fun <T> delete(key: Preferences.Key<T>)
    suspend fun clear()
}

//For getting custom object.
suspend inline fun <reified T> DataStorage.getObject(
    key: Preferences.Key<String>, default: T?
): T? {
    val string = get(key, "").first()
    if (string.isEmpty()) return null
    return Gson().toObject(string)
}

inline fun <reified T> Gson.toObject(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

class DataStorageImpl(context: Context) : DataStorage {

    companion object {
        private const val STORAGE = "storage"
    }

    private val Context.storage: DataStore<Preferences> by preferencesDataStore(name = STORAGE)

    private val dataStore = context.storage

    override suspend fun <T> put(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    override fun <T> get(key: Preferences.Key<T>, default: T): Flow<T> {
        return dataStore.data.map { it[key] ?: default }
    }

    override suspend fun putString(key: Preferences.Key<String>, value: String) {
        put(key, value)
    }

    override suspend fun putBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        put(key, value)
    }


    override suspend fun putDouble(key: Preferences.Key<Double>, value: Double) {
        put(key, value)
    }

    override suspend fun putInt(key: Preferences.Key<Int>, value: Int) {
        put(key, value)
    }

    override suspend fun putLong(key: Preferences.Key<Long>, value: Long) {
        put(key, value)
    }

    override suspend fun <T> putObject(key: Preferences.Key<String>, value: T) {
        put(key, Gson().toJson(value))
    }

    override fun getString(key: Preferences.Key<String>, default: String): Flow<String> {
        return get(key, default)
    }

    override fun getBoolean(key: Preferences.Key<Boolean>, default: Boolean): Flow<Boolean> {
        return get(key, default)
    }

    override fun getDouble(key: Preferences.Key<Double>, default: Double): Flow<Double> {
        return get(key, default)
    }

    override fun getInt(key: Preferences.Key<Int>, default: Int): Flow<Int> {
        return get(key, default)
    }

    override fun getLong(key: Preferences.Key<Long>, default: Long): Flow<Long> {
        return get(key, default)
    }


    override suspend fun <T> delete(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    override suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}

object DataStorageKey {
    val KEY_STRING = stringPreferencesKey("KEY_STRING")
    val KEY_INT = intPreferencesKey("KEY_INT")

    val KEY_OBJECT = stringPreferencesKey("KEY_OBJECT")
    val KEY_LIST = stringPreferencesKey("KEY_LIST")
}