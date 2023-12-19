package com.elementary.mualijpro.utils

import android.content.Context
import com.kaopiz.kprogresshud.KProgressHUD

class Loader {

    companion object {
        var mContext: Context? = null
        var progressKHUD: KProgressHUD? = null

        fun showLoader(context: Context) {
            try {
                this.mContext = context
                AppUtils.touchScreenDisable(this.mContext!!)
                progressKHUD = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(10f)
                    .show()
            } catch (e: Exception) {

            }

        }

        fun hideLoader() {
            try {
                AppUtils.touchScreenEnable(this.mContext!!)
                if (progressKHUD != null && progressKHUD!!.isShowing)
                    progressKHUD!!.dismiss()
            } catch (e: Exception) {

            }
        }
    }

}