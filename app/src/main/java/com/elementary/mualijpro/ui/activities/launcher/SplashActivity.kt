package com.elementary.mualijpro.ui.activities.launcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.elementary.mualijpro.R
import com.elementary.mualijpro.ui.activities.auth.LoginActivity
import com.elementary.mualijpro.ui.activities.base.BaseActivity
import com.elementary.mualijpro.ui.activities.main.MainActivity
import com.elementary.mualijpro.utils.AppSP
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Suppress("DEPRECATION")
class SplashActivity : BaseActivity() {

    private var isComingFromNotification = false
    private var notificationType = ""
    private var appointmentId = ""
    private val splashTime: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        // OPTIONAL: If crash reporting has been explicitly disabled previously, add:
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
        callNextActivity()
    }

    private fun init() {

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        if (intent.hasExtra("isComingFromNotification"))
            isComingFromNotification = intent.getBooleanExtra("isComingFromNotification", false)
        if (intent.hasExtra("notificationType"))
            notificationType = intent.getStringExtra("notificationType")
        if (intent.hasExtra("appointmentId"))
            appointmentId = intent.getStringExtra("appointmentId")

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                val sp: AppSP = AppSP.getInstance(this)
                sp.savePreferences(Constants.FireBaseToken, token)
            })
    }

    private fun callNextActivity() {
        Handler().postDelayed(
            {
                if (AppSP.getInstance(this).readBool(Constants.isLoggedIn, false)) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("isComingFromNotification", isComingFromNotification)
                    intent.putExtra("notificationType", notificationType)
                    intent.putExtra("appointmentId", appointmentId)
                    intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
                    startActivity(intent)
                    finish()
                }

            }, splashTime
        )
    }

}
