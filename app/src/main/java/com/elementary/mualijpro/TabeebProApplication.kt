package com.elementary.mualijpro

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.taxi.bravoapp.utils.SocketSingleton

class MualijProApplication : Application() {

    var application: MualijProApplication? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        application = this
        SocketSingleton.getInstance(this)

        isPhone = this.resources.getBoolean(R.bool.isPhone)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun instance(): MualijProApplication? {
        return application
    }

    companion object {
        var isPhone = false
        var isServiceDown = false
        var context: Context? = null
        fun getAppContext(): Context? {
            return context
        }
    }

}
