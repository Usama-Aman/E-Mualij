package com.elementary.mualijpro.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.elementary.mualijpro.R
import com.elementary.mualijpro.adapters.DaysAdapter
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.elementary.mualijpro.interfaces.PrescriptionSideMenuDays
import java.util.ArrayList

class DialogDays(private var days: DashboardDialogsCallBack) : DialogFragment() {

    private var daysArrayList = ArrayList<String>()
    var lvDays: ListView? = null
    var ivCrossDialog: ImageView? = null
    var rlMain: RelativeLayout? = null
    private var mContext: FragmentActivity? = null
    var adapter: DaysAdapter? = null

    init {
        for (i in 1..30)
            this.daysArrayList.add("" + i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        val decorView = dialog!!.window!!.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.systemUiVisibility = uiOptions
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            DialogFactory.dialogSettings(mContext!!, dialog, rlMain, 1f, 0.9f)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_days, parent, false)
        viewInitialize(view)
        setListeners()
        setData()
        return view
    }

    private fun viewInitialize(v: View) {
        ivCrossDialog = v.findViewById(R.id.ivCrossDialog)
        rlMain = v.findViewById(R.id.rlMain)
        lvDays = v.findViewById(R.id.lvDays)
    }

    private fun setListeners() {
        ivCrossDialog?.setOnClickListener {
            this.dismiss()
        }
    }

    private fun setData() {
        adapter = DaysAdapter(days, mContext!!, daysArrayList)
        lvDays?.adapter = adapter
    }

}