package com.elementary.mualijpro.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

@Suppress("DEPRECATION")
class AppSP

private constructor(mContext: Context) {

    var instanceSP: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext
            .applicationContext)

    // read integer preferences
    fun readInt(key: String): Int {
        return instanceSP.getInt(key, -1)
    }

    fun readInt(key: String, value: Int): Int {
        return instanceSP.getInt(key, value)
    }

    // read String preferences
    fun readString(key: String): String? {
        return instanceSP.getString(key, "null")
    }

    // read String preferences
    fun readString(key: String, value: String): String? {
        return instanceSP.getString(key, value)
    }

    // read boolean preferences and default value will be false
    fun readBool(key: String, defaultVal: Boolean): Boolean {
        return instanceSP.getBoolean(key, defaultVal)
    }

    // save String preferences
    fun savePreferences(key: String, value: String?): Boolean {
        return instanceSP.edit().putString(key, value).commit()
    }

    // save integer preferences
    fun savePreferences(key: String, value: Int): Boolean {
        return instanceSP.edit().putInt(key, value).commit()
    }

    // save boolean preferences
    fun savePreferences(key: String, value: Boolean): Boolean {
        return instanceSP.edit().putBoolean(key, value).commit()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: AppSP? = null

        fun getInstance(context: Context): AppSP {

            if (null == instance) {
                instance = AppSP(context)
            }
            return instance as AppSP
        }
    }

}
