package com.elementary.mualijpro.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

class DialogFactory {

    companion object {
        fun dialogSettings(ctx: Context, dialog: Dialog, parentView: View?, h: Float, w: Float) {

            val displayMetrics = DisplayMetrics()
            (ctx as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val widthLcl = (displayMetrics.widthPixels * w).toInt()
            val heightLcl = (displayMetrics.heightPixels * h).toInt()
            val paramsLcl = parentView?.layoutParams as FrameLayout.LayoutParams
            paramsLcl.width = widthLcl
            paramsLcl.height = heightLcl
            paramsLcl.gravity = Gravity.CENTER
            parentView.layoutParams = paramsLcl
            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

    }

}