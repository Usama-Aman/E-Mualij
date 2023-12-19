package com.elementary.mualijpro.utils

import android.content.Context
import android.content.SharedPreferences
import com.elementary.mualijpro.models.posts.user.User
import com.google.gson.Gson

class UserDataPref(pContext: Context) {

    private val PrefName = "mualijApplicationPro"
    private var context: Context? = null
    private var pref: SharedPreferences? = null

    init {
        context = pContext
        pref = context!!.getSharedPreferences(PrefName, Context.MODE_PRIVATE)
    }

    fun saveUserData(userData: User?) {
        val prefsEditor = pref?.edit()
        val gson = Gson()
        val json = gson.toJson(userData) // myObject - instance of MyObject
        prefsEditor?.putString("DoctorProfile", json)
        prefsEditor?.commit()
    }

    fun emptyUserData() {
        val prefsEditor = pref?.edit()
        prefsEditor?.putString("DoctorProfile", "")
        prefsEditor?.commit()
    }

    fun getUserData(): User? {
        try {
            val gson = Gson()
            val json = pref?.getString("DoctorProfile", "")
            return gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    companion object {

        private val TAG = UserDataPref::class.java.canonicalName
        private var instance: UserDataPref? = null
        fun getInstance(context: Context): UserDataPref {
            if (null == instance) {
                instance = UserDataPref(context)
            }
            return instance as UserDataPref
        }
    }

}
