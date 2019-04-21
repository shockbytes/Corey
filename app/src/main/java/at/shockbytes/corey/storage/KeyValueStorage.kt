package at.shockbytes.corey.storage

interface KeyValueStorage {

    fun putBoolean(value: Boolean, key: String)

    fun getBoolean(key: String): Boolean

    fun putInt(value: Int, key: String)

    fun getInt(key: String, defaultValue: Int): Int
}