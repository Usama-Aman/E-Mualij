package com.elementary.mualijpro.utils

import android.app.Activity
import android.graphics.Typeface
import android.view.ViewGroup
import com.elementary.mualijpro.R
import com.irozon.sneaker.Sneaker

class CustomAlert {

    companion object {

        fun showDropDownNotificationError(mContext: Activity, title: String, message: String) {
            if (message != "")
                Sneaker.with(mContext)
                    .setTitle(title, R.color.white)
                    .setMessage(message, R.color.white)
                    .setDuration(3000)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setTypeface(
                        Typeface.createFromAsset(
                            mContext.assets,
                            "font/sharp_sans_medium.otf"
                        )
                    )
                    .setIcon(
                        R.drawable.ic_error,
                        R.color.white,
                        true
                    )
                    .setCornerRadius(10, 5)
                    .sneak(R.color.alert_dialog_error)
        }

        fun showDropDownNotificationSuccess(mContext: Activity, title: String, message: String) {
            if (message != "")
                Sneaker.with(mContext)
                    .setTitle(title, R.color.white)
                    .setMessage(message, R.color.white)
                    .setDuration(3000)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setTypeface(
                        Typeface.createFromAsset(
                            mContext.assets,
                            "font/sharp_sans_medium.otf"
                        )
                    )
                    .setIcon(
                        R.drawable.ic_success,
                        R.color.white,
                        true
                    )
                    .setCornerRadius(10, 5)
                    .sneak(R.color.alert_dialog_success)
        }

    }

}
