package com.elementary.mualijpro.ui.fragments.changepass

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import com.elementary.mualijpro.utils.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_change_pass.*


class ChangePassFragment : BaseFragment(), View.OnClickListener, ApiResponseEvent {

    private var etOldPassTw: TextWatcher? = null
    private var etNewPassTw: TextWatcher? = null
    private var etRetypeNewPassTw: TextWatcher? = null

    companion object {
        fun newInstance(): ChangePassFragment {
            return ChangePassFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setChangeListeners()
        setListeners()
        llMain.setOnClickListener {
            activity?.let { it1 -> AppUtils.hideKeyboard(it1) }
        }
    }

    private fun setListeners() {
        btnChangePassUpdate.setOnClickListener(this)
    }


    private fun setChangeListeners() {
        etOldPassTw = object : TextWatcher {
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
                validateEt(etOldPass)
            }
        }
        etOldPass.addTextChangedListener(etOldPassTw)

        etNewPassTw = object : TextWatcher {
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
                validateEt(etNewPass)
            }
        }
        etNewPass.addTextChangedListener(etNewPassTw)

        etRetypeNewPassTw = object : TextWatcher {
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
                validateRetypePass()
            }
        }
        etRetypeNewPass.addTextChangedListener(etRetypeNewPassTw)
    }

    private fun validateEt(et: EditText): Boolean {
        return if (AppUtils.isSet(et.text.toString())) {
            AppUtils.setBackground(et, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(et, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validateRetypePass(): Boolean {
        return if (etNewPass.text.toString() == etRetypeNewPass.text.toString()) {
            AppUtils.setBackground(etRetypeNewPass, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(etRetypeNewPass, R.drawable.bg_white_with_red_border)
            false
        }
    }


    private fun validateRequest() {

        if (!validateEt(etOldPass))
            return

        if (!validateEt(etNewPass))
            return

        if (!validateRetypePass())
            return

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        try {

            val paramObject = AppUtils.getGenericParams()
            paramObject.addProperty("old_password", etOldPass.text.toString())
            paramObject.addProperty("new_password", etNewPass.text.toString())
            paramObject.addProperty("confirm_password", etRetypeNewPass.text.toString())
            callChangePassRequest(paramObject)

        } catch (e: Exception) {

        }

    }

    private fun callChangePassRequest(jsonObject: JsonObject) {
        Loader.showLoader(activity!!)
        ApiController.getInstance(activity!!, this).changePass(jsonObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {

        try {
            Loader.hideLoader()
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                errorMessage
            )
        } catch (e: Exception) {

        }

    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {

        try {
            Loader.hideLoader()
            var objectData = webResponse as WebResponse<*>

            // Remove text watcher to avoid call watchers because have to set all edit text to empty
            etOldPass.removeTextChangedListener(etOldPassTw)
            etNewPass.removeTextChangedListener(etNewPassTw)
            etRetypeNewPass.removeTextChangedListener(etRetypeNewPassTw)
            etOldPass.setText("")
            etNewPass.setText("")
            etRetypeNewPass.setText("")
            etOldPass.addTextChangedListener(etOldPassTw)
            etNewPass.addTextChangedListener(etNewPassTw)
            etRetypeNewPass.addTextChangedListener(etRetypeNewPassTw)
            etOldPass.requestFocus()

            CustomAlert.showDropDownNotificationSuccess(
                activity!!,
                resources.getString(R.string.txt_alert_success_information),
                objectData?.message!!
            )
        } catch (e: Exception) {

        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnChangePassUpdate -> {
                validateRequest()
            }
        }
    }

}