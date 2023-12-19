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
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import android.app.DatePickerDialog
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.adapters.AvailableScheduleAdapter
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.local.reschedule.FinalRescheduleAvailableList
import com.elementary.mualijpro.models.posts.reschedule.AvailableScheduleDataResponse
import com.elementary.mualijpro.models.posts.reschedule.AvailableScheduleList
import com.elementary.mualijpro.networks.APIsTag
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.utils.*
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class DialogReschedule(
    private var appointmentDate: String,
    private var appointmentId: Int?,
    private var rescheduleAppointment: DashboardDialogsCallBack
) :
    DialogFragment(), ApiResponseEvent {

    var progressBar: ProgressBar? = null
    var btnReschedule: Button? = null
    var ivCross: ImageView? = null
    var rlMain: RelativeLayout? = null
    private var mContext: FragmentActivity? = null
    var rlDate: RelativeLayout? = null
    var dateListener: DatePickerDialog.OnDateSetListener? = null
    private val calendar = Calendar.getInstance()
    private var tvDateValue: TextView? = null
    private var apiDate = ""
    private var apiTime = ""

    var tvSomethingWrong: TextView? = null
    var rvAvailableSchedule: RecyclerView? = null
    var adapterAvailableSchedule: AvailableScheduleAdapter? = null
    var rlNotification: RelativeLayout? = null
    private var startIndex = 0
    var countValue = 5
    private var availableScheduleList = ArrayList<AvailableScheduleList>()
    private var finalAvailableScheduleList = ArrayList<FinalRescheduleAvailableList>()
    var mLayoutManager: LinearLayoutManager? = null

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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_reschedule, parent, false)
        viewInitialize(view)
        setListeners()
        validateAvailableScheduleRequest()
        return view
    }

    private fun viewInitialize(v: View) {
        tvSomethingWrong = v.findViewById(R.id.tvSomethingWrong)
        rvAvailableSchedule = v.findViewById(R.id.rvAvailableSchedule)
        rlNotification = v.findViewById(R.id.rlNotification)
        tvDateValue = v.findViewById(R.id.tvDateValue)
        rlDate = v.findViewById(R.id.rlDate)
        progressBar = v.findViewById(R.id.progressBar)
        btnReschedule = v.findViewById(R.id.btnReschedule)
        ivCross = v.findViewById(R.id.ivCross)
        rlMain = v.findViewById(R.id.rlMain)

        calendar.time = AppUtils.dateFormat(appointmentDate)
        tvDateValue?.text = AppUtils.dateFormat(calendar.time)
        apiDate = AppUtils.baseDateFormatEng(calendar.time)

        dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            tvDateValue?.text = AppUtils.dateFormat(calendar.time)
            apiDate = AppUtils.baseDateFormatEng(calendar.time)
            validateAvailableScheduleRequest()
        }


    }

    private fun setListeners() {
        btnReschedule?.setOnClickListener {
            validateReschedule()
        }

        rlDate?.setOnClickListener {
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(context!!, dateListener, mYear, mMonth, mDay)
            dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }

        ivCross?.setOnClickListener {
            this.dismiss()
        }
    }

    private fun somethingWentWrong(
        list: View,
        tv: TextView,
        isWentWrong: Boolean,
        errorMessage: String
    ) {
        if (isWentWrong) {
            btnReschedule?.visibility = View.INVISIBLE
            list.visibility = View.GONE
            tv.visibility = View.VISIBLE
            tv.text = errorMessage
        } else {
            btnReschedule?.visibility = View.VISIBLE
            list.visibility = View.VISIBLE
            tv.visibility = View.GONE
        }
    }

    private fun validateAvailableScheduleRequest() {

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            somethingWentWrong(
                rvAvailableSchedule!!,
                tvSomethingWrong!!,
                true,
                resources.getString(R.string.txt_alert_internet_connection)
            )
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        apiTime = ""
        startIndex = 0
        countValue = 5
        finalAvailableScheduleList.clear()
        availableScheduleList.clear()
        rvAvailableSchedule?.visibility = View.GONE
        tvSomethingWrong?.visibility = View.GONE
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("date", apiDate)
        callAvailableScheduleList(paramObject)

    }

    private fun callAvailableScheduleList(jsonObject: JsonObject) {
        AppUtils.showProgressBar(progressBar!!, activity!!)
        ApiController.getInstance(activity!!, this).availableScheduleRequest(jsonObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        try {
            AppUtils.hideProgressBar(progressBar!!, activity!!)
            when (requestType) {
                APIsTag.availableScheduleList -> {
                    if (finalAvailableScheduleList.size == 0)
                        somethingWentWrong(
                            rvAvailableSchedule!!,
                            tvSomethingWrong!!,
                            true,
                            resources.getString(R.string.txt_something_went_wrong)
                        )
                    CustomDialogAlert.showDropDownNotificationError(
                        activity!!,
                        rlNotification!!,
                        resources.getColor(R.color.alert_dialog_error),
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )
                }
                APIsTag.reschedule -> {
                    CustomDialogAlert.showDropDownNotificationError(
                        activity!!,
                        rlNotification!!,
                        resources.getColor(R.color.alert_dialog_error),
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )
                }
            }

        } catch (e: Exception) {

        }
    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {
        try {
            var objectData = webResponse as WebResponse<*>
            AppUtils.hideProgressBar(progressBar!!, activity!!)
            when (requestType) {
                APIsTag.availableScheduleList -> {
                    var rescheduleData: AvailableScheduleDataResponse? =
                        CommonFunctions.convertDataType(
                            objectData,
                            AvailableScheduleDataResponse::class.java
                        )
                    setAvailableSchedule(rescheduleData)
                }

                APIsTag.reschedule -> {
                    CustomDialogAlert.showDropDownNotificationError(
                        activity!!,
                        rlNotification!!,
                        resources.getColor(R.color.alert_dialog_success),
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )
                    Handler().postDelayed({
                        rescheduleAppointment.onCallBack(
                            Constants.dashboardRescheduleAppointment,
                            "",
                            ArrayList()
                        )
                        this.dismiss()
                    }, 500)
                }
            }

        } catch (e: Exception) {

        }
    }

    private fun setAvailableSchedule(availableScheduleData: AvailableScheduleDataResponse?) {
        try {
            countValue = if (!MualijProApplication.isPhone) 5 else 3


            if (availableScheduleData?.getAvailableScheduleList() != null)
                availableScheduleList.addAll(availableScheduleData.getAvailableScheduleList()!!)

            if (!isPhone)
                when {
                    availableScheduleList?.size!! >= 6 -> setAvailableScheduleDataRecursively()
                    availableScheduleList?.size!! > 0 -> {
                        var objFinalWeeklyAppointments = FinalRescheduleAvailableList()
                        for (i in 0 until availableScheduleList?.size!!)
                            objFinalWeeklyAppointments.getTimeArray().add(availableScheduleList[i])
                        finalAvailableScheduleList.add(objFinalWeeklyAppointments)
                        setAdapter()
                    }
                    else -> setAdapter()
                }
            else
                when {
                    availableScheduleList?.size!! >= 4 -> setAvailableScheduleDataRecursively()
                    availableScheduleList?.size!! > 0 -> {
                        var objFinalWeeklyAppointments = FinalRescheduleAvailableList()
                        for (i in 0 until availableScheduleList?.size!!)
                            objFinalWeeklyAppointments.getTimeArray().add(availableScheduleList[i])
                        finalAvailableScheduleList.add(objFinalWeeklyAppointments)
                        setAdapter()
                    }
                    else -> setAdapter()
                }
        } catch (e: Exception) {

        }

    }

    private fun setAvailableScheduleDataRecursively() {

        var objFinalWeeklyAppointments = FinalRescheduleAvailableList()
        for (i in startIndex until availableScheduleList?.size!!) {
            objFinalWeeklyAppointments.getTimeArray().add(availableScheduleList[startIndex])
            startIndex++
            if (startIndex == countValue) {
                if (!isPhone)
                    countValue += 5
                else
                    countValue += 3
                break
            }
        }
        finalAvailableScheduleList.add(objFinalWeeklyAppointments)
        if (startIndex < availableScheduleList?.size!!)
            setAvailableScheduleDataRecursively()
        else
            setAdapter()
    }

    private fun setAdapter() {
        if (finalAvailableScheduleList != null && finalAvailableScheduleList.size > 0) {

            somethingWentWrong(rvAvailableSchedule!!, tvSomethingWrong!!, false, "")
            // set rating listing adapter
            adapterAvailableSchedule =
                AvailableScheduleAdapter(this, activity!!, finalAvailableScheduleList)
            rvAvailableSchedule?.adapter = adapterAvailableSchedule
            mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            rvAvailableSchedule?.layoutManager = mLayoutManager
        } else
            somethingWentWrong(
                rvAvailableSchedule!!,
                tvSomethingWrong!!,
                true,
                resources.getString(R.string.txt_no_available_schedule)
            )
    }

    fun onAppointmentItemClicked(mainPosition: Int, timeArrayPosition: Int) {
        for (i in 0 until finalAvailableScheduleList.size) {
            for (f in 0 until finalAvailableScheduleList[i].getTimeArray().size) {
                finalAvailableScheduleList[i].getTimeArray()[f].setTimeColor(resources.getColor(R.color.colorPrimary))
                finalAvailableScheduleList[i].getTimeArray()[f].setBgColor(R.drawable.bg_white_with_green_border)
            }
        }
        apiTime =
            finalAvailableScheduleList[mainPosition].getTimeArray()[timeArrayPosition].getAppointmentTime()!!
        finalAvailableScheduleList[mainPosition].getTimeArray()[timeArrayPosition].setTimeColor(
            resources.getColor(R.color.white)
        )
        finalAvailableScheduleList[mainPosition].getTimeArray()[timeArrayPosition].setBgColor(R.drawable.bg_green_without_border)
        adapterAvailableSchedule?.notifyDataSetChanged()

    }

    private fun validateReschedule() {

        if (apiTime.isEmpty() || apiTime == "") {
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_reschedule_error)
            )
            return
        }

        AppUtils.showProgressBar(progressBar!!, activity!!)
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("date", apiDate)
        paramObject.addProperty("time", apiTime)
        paramObject.addProperty("appointment_id", appointmentId)
        ApiController.getInstance(activity!!, this).rescheduleAppointment(paramObject)

    }

}