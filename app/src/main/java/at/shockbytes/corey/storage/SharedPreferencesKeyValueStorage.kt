package at.shockbytes.corey.storage

import android.content.SharedPreferences

class SharedPreferencesKeyValueStorage(
    private val sharedPreferences: SharedPreferences
) : KeyValueStorage {

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun putBoolean(value: Boolean, key: String) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun putInt(value: Int, key: String) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
}