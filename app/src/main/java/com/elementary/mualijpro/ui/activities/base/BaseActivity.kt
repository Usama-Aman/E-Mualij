package com.elementary.mualijpro.ui.activities.base

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.utils.*
import java.lang.Exception

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* if (AppUtils.isEmulator()) {
             finishAffinity()
             finish()
         }*/
        initialization()
    }

    private fun initialization() {
        AppUtils.multiLanguageConfiguration(this)
        AppUtils.setLanguage(this, "en"/*AppUtils.getLanguage(this, "en")!!*/)
        AppUtils.hideKeyboard(this)

        isPhone = this.resources.getBoolean(R.bool.isPhone)

        requestedOrientation = if (isPhone)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        try {
            Loader.hideLoader()
        } catch (e: Exception) {

        }
        overridePendingTransition(R.anim.activity_slide_from_right, R.anim.activity_slide_to_left)
        removeStatusBar()
    }

    private fun removeStatusBar() {
        if (!isPhone)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val w = window
            w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

}