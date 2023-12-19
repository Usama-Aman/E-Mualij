package com.elementary.mualijpro.ui.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.elementary.mualijpro.R
import com.elementary.mualijpro.dialogs.DialogForgotPass
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.ui.activities.base.BaseActivity
import com.elementary.mualijpro.ui.activities.main.MainActivity
import com.elementary.mualijpro.utils.*
import com.elementary.mualijpro.utils.AppUtils.Companion.getGenericParams
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etEmail
import java.lang.Exception

class LoginActivity : BaseActivity(), ApiResponseEvent, View.OnClickListener {

    private var dialogForgotPass: DialogForgotPass? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Is coming from any hacking technique
        if (intent != null && intent.getIntExtra(
                Constants.intentConstKey,
                0
            ) != Constants.intentConstValue
        )
            finish()

        setContentView(R.layout.activity_login)
        initializations()
        setChangeListeners()
    }

    private fun initializations() {
        tvForgotPass.setOnClickListener(this)
        btnSignIn.setOnClickListener(this)
    }

    private fun callLoginRequest(jsonObject: JsonObject) {
        Loader.showLoader(this)
        ApiController.getInstance(this, this).loginUser(jsonObject)
    }

    private fun setChangeListeners() {

        llMain.setOnClickListener { AppUtils.hideKeyboard(this) }
        rlBody.setOnClickListener { AppUtils.hideKeyboard(this) }

        etEmail.addTextChangedListener(object : TextWatcher {
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

        etPass.addTextChangedListener(object : TextWatcher {
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
                validatePass()
            }
        })

    }

    private fun validateEmail(): Boolean {
        return if (AppUtils.validateEmail(etEmail.text.toString())) {
            AppUtils.setBackground(etEmail, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(etEmail, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validatePass(): Boolean {
        return if (AppUtils.isSet(etPass.text.toString())) {
            AppUtils.setBackground(etPass, R.drawable.bg_white_with_gray_border)
            true
        } else {
            AppUtils.setBackground(etPass, R.drawable.bg_white_with_red_border)
            false
        }
    }

    private fun validateRequest() {

        if (!validateEmail())
            return

        if (!validatePass())
            return

        if (!AppUtils.isOnline(this)) {
            CustomAlert.showDropDownNotificationError(
                this,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        val paramObject = getGenericParams()
        paramObject.addProperty("email", etEmail.text.toString())
        paramObject.addProperty("password", etPass.text.toString())
        paramObject.addProperty("last_login_from", AppUtils.getIPAddress(true))
        callLoginRequest(paramObject)

    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        Loader.hideLoader()
        CustomAlert.showDropDownNotificationError(
            this,
            resources.getString(R.string.txt_alert_information),
            errorMessage
        )
    }

    // API Success Response Handling
    override fun onSuccess(requestType: Int, webResponse: Any) {

        Loader.hideLoader()
        try {
            var objectData = webResponse as WebResponse<*>
            var userdata: User? = CommonFunctions.convertDataType(objectData, User::class.java)
            val userSP: UserDataPref = UserDataPref.getInstance(this)
            userdata?.setToken(AppUtils.encryptValue(userdata?.getToken()!!))
            userSP.saveUserData(userdata)

            val sp: AppSP = AppSP.getInstance(this)
            sp.savePreferences(Constants.accessToken, userdata?.getToken())
            sp.savePreferences(Constants.isLoggedIn, true)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
            startActivity(intent)
            finish()
        } catch (e: Exception) {

        }


    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSignIn -> {
                validateRequest()
            }
            R.id.tvForgotPass -> {
                dialogForgotPass = DialogForgotPass()
                supportFragmentManager.let { dialogForgotPass!!.show(it, "") }
            }
        }
    }
}
