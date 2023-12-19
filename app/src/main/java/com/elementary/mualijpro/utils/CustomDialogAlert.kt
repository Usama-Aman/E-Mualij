package com.elementary.mualijpro.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.elementary.mualijpro.R

@Suppress("DEPRECATION")
class CustomDialogAlert {

    companion object {

        private val mContext: Activity? = null
        var rlNotification: RelativeLayout? = null
        private var mHandlerShow: Handler? = null
        var timeToShow: Long = 3000

        fun showDropDownNotificationError(
            mContext: Context,
            relativeLayout: RelativeLayout,
            color: Int,
            title: String,
            message: String
        ) {

            if (rlNotification != null)
                if (rlNotification?.visibility == View.VISIBLE)
                    return


            removeNotification()
            rlNotification = relativeLayout
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogNotification = inflater.inflate(R.layout.dialog_alert_view, null)
            rlNotification?.addView(dialogNotification)
            rlNotification?.visibility = View.VISIBLE

            val bgLayout = dialogNotification!!.findViewById(R.id.animatedLayout) as RelativeLayout
            val tvTitle = dialogNotification!!.findViewById(R.id.msg_title) as TextView
            val tvMessage = dialogNotification!!.findViewById(R.id.message_detail) as TextView

            tvTitle.text = title
            tvMessage.text = message

            val notificationImage = dialogNotification!!.findViewById(R.id.error_icon) as ImageView
            notificationImage.setOnClickListener {
                if (rlNotification?.visibility == View.VISIBLE) {
                    rlNotification?.visibility = View.GONE
                }
            }

            if (color == mContext.resources.getColor(R.color.alert_dialog_error)) {
                notificationImage.setImageResource(R.drawable.ic_error)
                bgLayout.setBackgroundColor(mContext.resources.getColor(R.color.alert_dialog_error))
            } else {
                notificationImage.setImageResource(R.drawable.ic_success)
                bgLayout.setBackgroundColor(mContext.resources.getColor(R.color.alert_dialog_success))
            }

            if (mHandlerShow == null)
                mHandlerShow = Handler()

            if (mHandlerShow != null)
                mHandlerShow?.removeCallbacksAndMessages(null);



            if (dialogNotification != null) {
                mHandlerShow?.postDelayed({
                    (mContext as Activity).runOnUiThread {
                        if (rlNotification?.visibility == View.VISIBLE) {
                            rlNotification?.visibility = View.GONE
                        }
                    }
                }, timeToShow)
            }

        }


        fun showDropDownNotificationErrorType(
            mContext: Context,
            relativeLayout: RelativeLayout,
            color: Int,
            title: String,
            message: String
        ) {

            if (rlNotification != null)
                if (rlNotification?.visibility == View.VISIBLE)
                    return


            removeNotification()
            rlNotification = relativeLayout
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogNotification = inflater.inflate(R.layout.dialog_alert_view, null)
            rlNotification?.addView(dialogNotification)
            rlNotification?.visibility = View.VISIBLE

            val bgLayout = dialogNotification!!.findViewById(R.id.animatedLayout) as RelativeLayout
            val tvTitle = dialogNotification!!.findViewById(R.id.msg_title) as TextView
            val tvMessage = dialogNotification!!.findViewById(R.id.message_detail) as TextView

            tvTitle.text = title
            tvMessage.text = message

            val notificationImage = dialogNotification!!.findViewById(R.id.error_icon) as ImageView
            notificationImage.setOnClickListener {
                if (rlNotification?.visibility == View.VISIBLE) {
                    rlNotification?.visibility = View.GONE
                }
            }

            if (color == mContext.resources.getColor(R.color.alert_dialog_error)) {
                notificationImage.setImageResource(R.drawable.ic_error)
                bgLayout.setBackgroundColor(mContext.resources.getColor(R.color.alert_dialog_error))
            } else {
                notificationImage.setImageResource(R.drawable.ic_success)
                bgLayout.setBackgroundColor(mContext.resources.getColor(R.color.alert_dialog_success))
            }

            if (mHandlerShow == null)
                mHandlerShow = Handler()

            if (mHandlerShow != null)
                mHandlerShow?.removeCallbacksAndMessages(null);



            if (dialogNotification != null) {
                mHandlerShow?.postDelayed({
                    (mContext as Activity).runOnUiThread {
                        removeNotification()
                    }
                }, 600)
            }

        }

        private fun removeNotification() {
            try {

                if (rlNotification != null)
                    if (rlNotification?.visibility == View.VISIBLE) {
                        rlNotification?.clearAnimation()
                        rlNotification?.visibility = View.GONE
                    }

                if (mHandlerShow != null)
                    mHandlerShow?.removeCallbacksAndMessages(null)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}