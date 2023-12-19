package com.elementary.mualijpro.mualij.base

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.elementary.mualijpro.R
import com.elementary.mualijpro.MualijProApplication

import com.elementary.mualijpro.utils.AppSP
import com.elementary.mualijpro.utils.AppUtils

import com.elementary.mualijpro.utils.Loader
import com.google.gson.JsonObject

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppUtils.isEmulator()) {
            finishAffinity()
            finish()
        }
        initialization()
    }

    companion object {
        var reference: Activity? = null
    }

    private fun initialization() {
        reference = this
        AppUtils.multiLanguageConfiguration(this)
        AppUtils.setLanguage(this, AppUtils.getLanguage(this, "en")!!)
        AppUtils.hideKeyboard(this)
        try {
            Loader.hideLoader()
        } catch (e: java.lang.Exception) {

        }
        overridePendingTransition(R.anim.activity_slide_from_right, R.anim.activity_slide_to_left)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun removeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val w = window
            w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun getGenericParams(): JsonObject {

        return JsonObject()

    }

    private fun getDeviceUniqueId(): String {
        return Settings.Secure.getString(
            MualijProApplication.getAppContext()?.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
    }

    fun changeStatusBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    fun getAppPrefInstance(): AppSP? {
        return AppSP.getInstance(this)
    }

}