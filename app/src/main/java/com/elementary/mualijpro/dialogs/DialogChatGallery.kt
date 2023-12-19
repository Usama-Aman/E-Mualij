package com.elementary.mualijpro.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.elementary.mualijpro.R
import com.elementary.mualijpro.utils.*

class DialogChatGallery(
    private var imageUrl: String
) : DialogFragment() {

    private var ivFullImage: ImageView? = null
    var llMain: LinearLayout? = null
    var rlCross: RelativeLayout? = null
    private var mContext: FragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        if (AppUtils.getLanguage(context!!, "en").equals("en"))
            dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimationEn
        else
            dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimationAr
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            DialogFactory.dialogSettings(mContext!!, dialog, llMain, 1f, 1f)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_chat_gallery, parent, false)
        viewInitialize(view)
        setData()
        setListeners()
        return view
    }

    private fun viewInitialize(v: View) {
        ivFullImage = v.findViewById(R.id.ivFullImage)
        llMain = v.findViewById(R.id.llMain)
        rlCross = v.findViewById(R.id.rlCross)
    }

    private fun setData() {

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.profile_placeholder)
            .error(R.drawable.profile_placeholder)
            .into(ivFullImage!!)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setListeners() {
        rlCross?.setOnClickListener {
            this.dismiss()
        }
    }

}