package com.elementary.mualijpro.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.AppUtils.Companion.getGenericParams
import com.elementary.mualijpro.utils.Constants
import com.elementary.mualijpro.utils.CustomDialogAlert
import com.google.gson.JsonObject
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

@Suppress("DEPRECATION")
class DialogReportPatient(
    private var patientDataObj: PatientDataResponse,
    private var alertDialogCallBack: DashboardDialogsCallBack
) : DialogFragment(), ApiResponseEvent {

    var progressBar: ProgressBar? = null
    var btnReport: Button? = null
    var ivCross: ImageView? = null
    var rlMain: RelativeLayout? = null
    var etReason: EditText? = null
    private var mContext: FragmentActivity? = null
    var rlNotification: RelativeLayout? = null


    var rlBody: RelativeLayout? = null
    var rlBg: RelativeLayout? = null
    var rlHeader: RelativeLayout? = null

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
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_report_patient, parent, false)
        viewInitialize(view)
        setListeners()
        setChangeListeners()
        return view
    }

    private fun viewInitialize(v: View) {
        rlNotification = v.findViewById(R.id.rlNotification)
        etReason = v.findViewById(R.id.etReason)
        progressBar = v.findViewById(R.id.progressBar)
        btnReport = v.findViewById(R.id.btnReport)
        ivCross = v.findViewById(R.id.ivCross)
        rlMain = v.findViewById(R.id.rlMain)

        rlBody= v.findViewById(R.id.rlBody)
        rlBg= v.findViewById(R.id.rlBg)
        rlHeader= v.findViewById(R.id.rlHeader)

        etReason?.imeOptions = EditorInfo.IME_ACTION_DONE
        etReason?.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    private fun setListeners() {



        rlBody?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }


        rlBg?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }

        rlHeader?.setOnClickListener {
           AppUtils.hideKeyboard(activity!!)
        }

        btnReport?.setOnClickListener {
            validateRequest()
        }

        ivCross?.setOnClickListener {
            this.dismiss()
        }

        rlMain?.setOnClickListener {
            AppUtils.hideKeyboard(activity!!)
        }
    }

    private fun setChangeListeners() {

        etReason?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                validateReason()
            }
        })

    }


    private fun validateReason(): Boolean {
        return if (AppUtils.isSet(etReason?.text.toString())) {
            AppUtils.setBackground(etReason!!, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(etReason!!, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validateRequest() {

        if (!validateReason())
            return

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        val paramObject = getGenericParams()
        paramObject.addProperty("patient_id", patientDataObj?.getPatientId())
        paramObject.addProperty("appointment_id", patientDataObj?.getAppointmentId())
        paramObject.addProperty("comment", etReason?.text.toString())
        callReportPatient(paramObject)

    }

    private fun callReportPatient(paramObject: JsonObject) {
        if (AppUtils.isKeyboardOpen(activity!!))
            AppUtils.hideKeyboardForOverlay(activity!!)
        AppUtils.showProgressBar(progressBar!!, activity!!)
        ApiController.getInstance(activity!!, this).reportPatient(paramObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        try {
            AppUtils.hideProgressBar(progressBar!!, activity!!)
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                errorMessage
            )
        } catch (e: Exception) {

        }

    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {
        try {
            AppUtils.hideProgressBar(progressBar!!, activity!!)
            var objectData = webResponse as WebResponse<*>
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_success),
                resources.getString(R.string.txt_alert_success_information),
                objectData.message!!
            )
            etReason?.isEnabled = false
            etReason?.isClickable = false

            btnReport?.isEnabled = false
            btnReport?.isClickable = false

            Handler().postDelayed({
                alertDialogCallBack.onCallBack(Constants.dashboardReportPatient, "", ArrayList())
                this.dismiss()
            }, 1500)
        } catch (e: Exception) {

        }
    }

}