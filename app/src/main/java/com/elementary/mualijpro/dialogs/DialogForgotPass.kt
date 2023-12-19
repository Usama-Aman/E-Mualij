package com.elementary.mualijpro.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
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
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.AppUtils.Companion.getGenericParams
import com.elementary.mualijpro.utils.CustomDialogAlert
import com.google.gson.JsonObject

@Suppress("DEPRECATION")
class DialogForgotPass : DialogFragment(), ApiResponseEvent {

    var progressBar: ProgressBar? = null
    var btnReset: Button? = null
    var ivCross: ImageView? = null
    var rlMain: RelativeLayout? = null
    var etEmail: EditText? = null
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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_forgot_pass, parent, false)
        viewInitialize(view)
        setListeners()
        setChangeListeners()
        return view
    }

    private fun viewInitialize(v: View) {
        rlNotification = v.findViewById(R.id.rlNotification)
        etEmail = v.findViewById(R.id.etEmail)
        progressBar = v.findViewById(R.id.progressBar)
        btnReset = v.findViewById(R.id.btnReset)
        ivCross = v.findViewById(R.id.ivCross)
        rlMain = v.findViewById(R.id.rlMain)

        rlBody= v.findViewById(R.id.rlBody)
        rlBg= v.findViewById(R.id.rlBg)
        rlHeader= v.findViewById(R.id.rlHeader)
    }

    private fun setListeners() {


        rlBody?.setOnClickListener {
            validateRequest()
        }


        rlBg?.setOnClickListener {
            validateRequest()
        }

        rlHeader?.setOnClickListener {
            validateRequest()
        }



        btnReset?.setOnClickListener {
            validateRequest()
        }

        ivCross?.setOnClickListener {
            this.dismiss()
        }

        rlMain?.setOnClickListener {
            AppUtils.hideKeyboard(mContext!!)
        }
    }

    private fun setChangeListeners() {

        etEmail?.addTextChangedListener(object : TextWatcher {
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
                validateEmail()
            }
        })

    }


    private fun validateEmail(): Boolean {
        return if (AppUtils.validateEmail(etEmail?.text.toString())) {
            AppUtils.setBackground(etEmail!!, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(etEmail!!, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validateRequest() {

        if (!validateEmail())
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
        paramObject.addProperty("email", etEmail?.text.toString())
        callForgotPassRequest(paramObject)

    }

    private fun callForgotPassRequest(jsonObject: JsonObject) {
        if (AppUtils.isKeyboardOpen(activity!!))
            AppUtils.hideKeyboardForOverlay(activity!!)
        AppUtils.showProgressBar(progressBar!!, activity!!)
        ApiController.getInstance(activity!!, this).forgotPass(jsonObject)
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

            etEmail?.isEnabled = false
            etEmail?.isClickable = false

            btnReset?.isEnabled = false
            btnReset?.isClickable = false

            Handler().postDelayed({
                this.dismiss()
            }, 1500)
        } catch (e: Exception) {

        }
    }

}