package com.elementary.mualijpro.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.elementary.mualijpro.R
import com.elementary.mualijpro.adapters.StartEndTimeListAdapter
import com.elementary.mualijpro.interfaces.WeeklyScheduleStartEndTime
import com.elementary.mualijpro.models.local.settings.SettingsStartEndTime

import java.util.ArrayList
import android.widget.*
import com.elementary.mualijpro.utils.AppUtils

class DialogStartEndTime(
    startTime: Boolean,
    indexValue: Int,
    textView: TextView,
    rlTime: RelativeLayout,
    timeArrayList: ArrayList<SettingsStartEndTime>,
    private var startEndTime: WeeklyScheduleStartEndTime
) : DialogFragment() {


    private var timeArrayList = ArrayList<SettingsStartEndTime>()
    var lvStartEndTime: ListView? = null
    var etSearch: EditText? = null
    var ivCrossDialog: ImageView? = null
    var rlMain: RelativeLayout? = null
    private var mContext: FragmentActivity? = null
    var adapter: StartEndTimeListAdapter? = null
    private var textView: TextView
    private var rlTime: RelativeLayout
    private var startTime: Boolean = false
    private var indexValue: Int = 0

    init {
        this.timeArrayList = timeArrayList
        this.textView = textView
        this.rlTime = rlTime
        this.startTime = startTime
        this.indexValue = indexValue
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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_start_end_time, parent, false)
        viewInitialize(view)
        setListeners()
        setData()
        return view
    }

    private fun viewInitialize(v: View) {
        ivCrossDialog = v.findViewById(R.id.ivCrossDialog)
        rlMain = v.findViewById(R.id.rlMain)
        etSearch = v.findViewById(R.id.etSearch)
        lvStartEndTime = v.findViewById(R.id.lvStartEndTime)
    }

    private fun setListeners() {
        ivCrossDialog?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            this.dismiss()
        }
    }

    private fun setData() {
        adapter = StartEndTimeListAdapter(startTime, indexValue, textView, rlTime, startEndTime, mContext!!, timeArrayList)
        lvStartEndTime?.adapter = adapter
        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                adapter!!.filter.filter(cs)
            }

            override fun beforeTextChanged(
                arg0: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {
            }

            override fun afterTextChanged(arg0: Editable) {
            }
        })
    }

}