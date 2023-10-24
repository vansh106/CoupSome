package `in`.coupsome.util

import android.content.Context

class CoupsomeSharedPreferences(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("coupsome", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun has(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    fun put(
        key: String,
        value: Any,
    ) {
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0f)
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0L)
    }

    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun remove(key: String) {
        editor.remove(key)
        editor.apply()
    }


    companion object {
        const val IS_FIRST_TIME = "is_first_time"
    }
}