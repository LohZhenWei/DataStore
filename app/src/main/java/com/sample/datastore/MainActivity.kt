package com.sample.datastore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sample.datastore.DataStorageKey.KEY_INT
import com.sample.datastore.DataStorageKey.KEY_LIST
import com.sample.datastore.DataStorageKey.KEY_OBJECT
import com.sample.datastore.DataStorageKey.KEY_STRING
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    private val dataStorage: DataStorage by lazy {
        DataStorageImpl(applicationContext)
    }

    private val scope by lazy { CoroutineScope(newSingleThreadContext("coroutine")) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //clear()
        getInt()
        getString()
        getObject()
        getList()

    }

    private fun getInt() {
        scope.launch {
            dataStorage.put(KEY_INT, 5)
            val value = dataStorage.get(KEY_INT, 0).first()
            Log.d("test", "int value : $value")
            dataStorage.delete(KEY_INT)
            val value1 = dataStorage.get(KEY_INT, 0).first()
            Log.d("test", "deleted int value : $value1")
        }
    }

    private fun getString() {
        scope.launch {
            dataStorage.putString(KEY_STRING, "SAVED STRING VALUE")
            val string = dataStorage.getString(KEY_STRING).first()
            Log.d("test", "string value : $string")
            dataStorage.delete(KEY_STRING)
            val string1 = dataStorage.getString(KEY_STRING, "abc").first()
            Log.d("test", "deleted string value : $string1")
        }
    }

    private fun getObject() {
        scope.launch {
            dataStorage.putObject(KEY_OBJECT, User())
            val user = dataStorage.getObject<User>(KEY_OBJECT, User())
            Log.d("test", "Object - ${user?.id}--${user?.name}")
        }
    }

    private fun getList() {
        scope.launch {
            val list = listOf("a", "b", "c", "d", "e")
            dataStorage.putObject(KEY_LIST, list)
            val listFromDb = dataStorage.getObject<List<String>>(KEY_LIST, emptyList())
            Log.d("test", "List - $listFromDb")
        }
    }

    private fun clear() {
        scope.launch { dataStorage.clear() }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.coroutineContext.cancel()
    }
}

class User(
    var id: Int = 1,
    var name: String = "user a"
)