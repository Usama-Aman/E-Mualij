package com.elementary.mualijpro

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.elementary.mualijpro.mualij.base.BaseActivity


@Suppress("DEPRECATION")
class TemporaryServiceDownActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_service_down)
        mActivity = this
        MualijProApplication.isServiceDown = true
    }

    public override fun onDestroy() {
        MualijProApplication.isServiceDown = false
        super.onDestroy()
    }



    companion object {
        var mActivity: Activity? = null
    }

}
