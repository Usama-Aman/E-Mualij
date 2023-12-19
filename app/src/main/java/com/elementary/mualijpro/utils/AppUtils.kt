package com.elementary.mualijpro.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.location.Geocoder
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.R
import com.elementary.mualijpro.TemporaryServiceDownActivity
import com.elementary.mualijpro.dialogs.DialogChat
import com.elementary.mualijpro.interfaces.AlertDialogCallBack
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.google.gson.JsonObject
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class AppUtils {

    companion object {

        fun getGenericParams(): JsonObject {

            val paramObject = JsonObject()
            paramObject.addProperty(Constants.paramDeviceId, getDeviceUniqueId())
            paramObject.addProperty(Constants.paramOsVersion, Build.VERSION.SDK_INT)
            paramObject.addProperty(Constants.paramDeviceName, Build.MODEL)
            paramObject.addProperty(Constants.paramDeviceType, Constants.deviceType)

            return paramObject

        }

        fun appIsInBackground(): Boolean {
            var myProcess = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(myProcess)
            return myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }

        fun socketGenericParams(activity: Activity): JSONObject {

            val paramObject = JSONObject()
            paramObject.put(
                Constants.socketParamFromId,
                UserDataPref.getInstance(activity).getUserData()?.getId()
            )
            paramObject.put(Constants.socketParamToType, Constants.toType)
            paramObject.put(Constants.socketParamFromType, Constants.fromType)
            return paramObject

        }

        @SuppressLint("HardwareIds")
        fun getDeviceUniqueId(): String {
            return Settings.Secure.getString(
                MualijProApplication.getAppContext()?.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }

        fun getDefaultLang(): String? {
            return AppSP.getInstance(MualijProApplication.getAppContext()!!).readString(
                Constants.userCurrentLang,
                "ur"
            )!!
        }

        fun showImageWithGlideBitmap(context: Context, bitmap: Bitmap, imageView: ImageView) {
            Glide.with(context)
                .asBitmap()
                .load(bitmap)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        imageView.setImageBitmap(resource)
                    }
                })
        }

        fun showPermissionAlertDialog(context: Context) {
            val dialog = Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_permision_settings)
            val btnSettings = dialog.findViewById<Button>(R.id.btnSettings)
            btnSettings.setOnClickListener {
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            dialog.show()
        }


        fun loadImageWithGlide(context: Context, imgURL: String, iv: ImageView, drawable: Int) {
            try {
                Glide.with(context)
                    .load(imgURL)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(drawable)
                            .error(drawable)
                            .centerCrop()
                    )
                    .into(iv)
            } catch (e: Exception) {

            }

        }

        fun loadProfileImageWithGlide(
            context: Context,
            imgURL: String,
            iv: ImageView,
            drawable: Int
        ) {

            try {

                Glide.with(context)
                    .load(imgURL)
                    .thumbnail(0.4f)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .placeholder(drawable)
                            .error(drawable)
                            .override(100, 100)
                            .centerCrop()
                    )
                    .into(iv)
            } catch (e: Exception) {

            }

        }

        fun isOnline(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return (netInfo != null && netInfo.isConnectedOrConnecting
                    && cm.activeNetworkInfo!!.isAvailable
                    && cm.activeNetworkInfo!!.isConnected)
        }


        fun changeDateIntoDayName(activity: Activity, date: String): String {

            val input = SimpleDateFormat(Constants.baseDateFormat)
            var output = SimpleDateFormat("dd MMM")
            var calendar = Calendar.getInstance()

            var currentDate = input.format(calendar!!.time)
            calendar.add(Calendar.DATE, 1)
            var tomorrowDate = input.format(calendar!!.time)
            val receivedDate = input.format(input.parse(date))

            return when {
                currentDate == receivedDate ->
                    activity.resources.getString(R.string.txt_today) + ", " + output.format(
                        input.parse(
                            receivedDate
                        )
                    )
                tomorrowDate == receivedDate ->
                    activity.resources.getString(R.string.txt_tomorrow) + ", " + output.format(
                        input.parse(
                            receivedDate
                        )
                    )
                else -> {
                    output = SimpleDateFormat("EEEE, MMM dd")
                    output.format(input.parse(receivedDate))
                }
            }
        }


        fun hideKeyboard(context: Activity) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = context.currentFocus
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        fun hideKeyboardForOverlay(context: Activity) {
            val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            try {
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            } catch (e: Exception) {

            }

        }

        private fun getRootView(activity: Activity): View {
            return activity.findViewById<View>(android.R.id.content)
        }

        fun isKeyboardOpen(activity: Activity): Boolean {
            val visibleBounds = Rect()
            var rootView = this.getRootView(activity)
            rootView.getWindowVisibleDisplayFrame(visibleBounds)
            val heightDiff = rootView.height - visibleBounds.height()
            val marginOfError = this.convertDpToPx(activity, 50F).roundToInt()
            return heightDiff > marginOfError
        }

        private fun convertDpToPx(activity: Activity, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                activity.resources.displayMetrics
            )
        }


        fun hideKeyboardFrom(context: FragmentActivity?, view: View) {
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun dateFormat(date: Date): String {
            val df = SimpleDateFormat("dd/MM/yyyy")
            return df.format(date)
        }

        fun dateFormat(date: String): Date {
            var input = SimpleDateFormat(Constants.baseDateFormat)
            return input.parse(date)
        }

        fun baseDateFormatEng(date: Date): String {
            val df = SimpleDateFormat(Constants.baseDateFormat, Locale.ENGLISH)
            return df.format(date)
        }

        fun validateEmail(email: String): Boolean {
            return !(email.isEmpty() || !isValidEmail(email))
        }

        private fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        }

        fun validateName(name: String): Boolean {
            return name.isNotEmpty()
        }

        fun isSet(value: String): Boolean {
            return value.isNotEmpty()
        }

        fun isSetLength(value: String): Boolean {
            return !(value.isEmpty() || value.length > 20)
        }

        fun validatePhone(value: String): Boolean {
            return !(value.isEmpty() || value.length < 11)
        }

        fun getAddress(context: Context, latitude: Double, longitude: Double): String {
            val results = StringBuilder()
            try {
                val geocode = Geocoder(context, Locale.getDefault())
                val addresses = geocode.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    val address = addresses[0]
                    if (address.getAddressLine(0) != null && address.getAddressLine(0)
                            .isNotEmpty()
                    ) {
                        results.append(address.getAddressLine(0)).append(" ,")
                        return results.toString()
                    }

                    if (isSet(address.featureName))
                        results.append(address.thoroughfare).append(" ,")
                    if (isSet(address.thoroughfare))
                        results.append(address.thoroughfare).append(" ,")
                    if (isSet(address.locality))
                        results.append(address.locality).append(" ,")
                    if (isSet(address.countryName))
                        results.append(address.countryName)

                }
            } catch (e: Exception) {

            }

            return results.toString()
        }

        fun touchScreenDisable(context: Context) {
            (context as Activity).window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }

        fun touchScreenEnable(context: Context) {
            (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        fun changeDateFormat(date: String): String {

            var input = SimpleDateFormat(Constants.baseDateFormat)
            var output = SimpleDateFormat("MMM dd")
            try {
                return output.format(input.parse(date))
            } catch (e: Exception) {
            }
            return ""
        }

        fun changeDateFormatGeneric(date: String): String {

            var input = SimpleDateFormat(Constants.baseDateFormat)
            var output = SimpleDateFormat("dd/MM/yyyy")
            try {
                return output.format(input.parse(date))
            } catch (e: Exception) {

            }
            return ""
        }

        fun changeDateFormatToEng(date: String): String {
            var input = SimpleDateFormat(Constants.baseDateFormat) //"yyyy-MM-dd"
            var output = SimpleDateFormat(Constants.baseDateFormat, Locale.ENGLISH) //"yyyy-MM-dd"
            try {
                return output.format(input.parse(date))
            } catch (e: Exception) {

            }
            return ""
        }

        fun setBackground(view: View, bg: Int) {
            view.setBackgroundResource(bg)
        }

        fun multiLanguageConfiguration(mContext: Context) {
            var locale: Locale?
            val language = AppSP.getInstance(mContext).readString(Constants.userCurrentLang, "ur")
            if (isSet(language!!)) {
                locale = Locale(language)
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                mContext.resources.updateConfiguration(config, mContext.resources.displayMetrics)
            }
        }

        fun getLanguage(mContext: Context, defaultValue: String): String? {
            return AppSP.getInstance(mContext).readString(Constants.userCurrentLang, defaultValue)
        }

        fun setLanguage(mContext: Context, languageCode: String) {
            AppSP.getInstance(mContext).savePreferences(Constants.userCurrentLang, languageCode)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleHelper.setLocale(mContext, getLanguage(mContext, languageCode))
            }
        }

        fun setListViewHeightBasedOnChildren(listView: ListView) {
            val listAdapter = listView.adapter
                ?: // pre-condition
                return

            var totalHeight = 0
            for (i in 0 until listAdapter.count) {
                val listItem = listAdapter.getView(i, null, listView)
                listItem.measure(0, 0)
                totalHeight += listItem.measuredHeight
            }

            val params = listView.layoutParams
            params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
            listView.layoutParams = params
            listView.requestLayout()
        }

        fun showProgressBarChat(progressBar: FrameLayout, context: Context) {

            if (progressBar.visibility == View.GONE) {
                touchScreenDisable(context)
                progressBar.visibility = View.VISIBLE
            }
        }

        fun hideProgressBarChat(progressBar: FrameLayout, context: Context) {
            if (progressBar.visibility == View.VISIBLE) {
                touchScreenEnable(context)
                progressBar.visibility = View.GONE
            }
        }

        fun showProgressBar(progressBar: ProgressBar, context: Context) {
            touchScreenDisable(context)
            progressBar.visibility = View.VISIBLE
        }

        fun hideProgressBar(progressBar: ProgressBar, context: Context) {
            touchScreenEnable(context)
            progressBar.visibility = View.GONE
        }

        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            val isIPv4 = sAddr.indexOf(':') < 0

                            if (useIPv4) {
                                if (isIPv4)
                                    return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                        0,
                                        delim
                                    ).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return ""
        }

        fun logoutConfirmationAlert(activity: Activity, alertDialogCallBack: AlertDialogCallBack) {
            hideKeyboard(activity!!)
            val pDialog = SweetAlertDialog(activity!!, SweetAlertDialog.WARNING_TYPE)
            pDialog.titleText = activity.resources.getString(R.string.txt_confirmation)
            pDialog.contentText = activity.resources.getString(R.string.txt_logout_confirmation)
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            pDialog.setConfirmClickListener {
                pDialog.dismissWithAnimation()
                alertDialogCallBack.onDialogPositiveClick()
            }
            pDialog.isShowCancelButton
            pDialog.cancelText = activity.resources.getString(R.string.txt_cancel)
            pDialog.confirmText = activity.resources.getString(R.string.txt_logout)
            pDialog.setCancelable(false)
            pDialog.show()
        }

        fun cancelAppointmentConfirmationAlert(
            activity: Activity,
            alertDialogCallBack: DashboardDialogsCallBack
        ) {
            hideKeyboard(activity!!)
            val pDialog = SweetAlertDialog(activity!!, SweetAlertDialog.WARNING_TYPE)
            pDialog.titleText = activity.resources.getString(R.string.txt_confirmation)
            pDialog.contentText =
                activity.resources.getString(R.string.txt_cancel_appointment_confirmation)
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            pDialog.setConfirmClickListener {
                pDialog.dismissWithAnimation()
                alertDialogCallBack.onCallBack(
                    Constants.dashboardCancelAppointment,
                    "",
                    ArrayList()
                )
            }
            pDialog.isShowCancelButton
            pDialog.cancelText = activity.resources.getString(R.string.txt_no)
            pDialog.confirmText = activity.resources.getString(R.string.txt_yes)
            pDialog.setCancelable(false)
            pDialog.show()
        }


        fun completeAppointmentConfirmationAlert(
            activity: Activity,
            alertDialogCallBack: DashboardDialogsCallBack
        ) {
            hideKeyboard(activity!!)
            val pDialog = SweetAlertDialog(activity!!, SweetAlertDialog.WARNING_TYPE)
            pDialog.titleText = activity.resources.getString(R.string.txt_confirmation)
            pDialog.contentText =
                activity.resources.getString(R.string.txt_complete_appointment_confirmation)
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            pDialog.setConfirmClickListener {
                pDialog.dismissWithAnimation()
                alertDialogCallBack.onCallBack(
                    Constants.dashboardCompleteAppointment,
                    "",
                    ArrayList()
                )
            }
            pDialog.isShowCancelButton
            pDialog.cancelText = activity.resources.getString(R.string.txt_no)
            pDialog.confirmText = activity.resources.getString(R.string.txt_yes)
            pDialog.setCancelable(false)
            pDialog.show()
        }

        fun removeMsgAlert(
            activity: Activity,
            dialogChat: DialogChat,
            messageId: String
        ) {
            hideKeyboard(activity!!)
            val pDialog = SweetAlertDialog(activity!!, SweetAlertDialog.WARNING_TYPE)
            pDialog.titleText = activity.resources.getString(R.string.txt_confirmation)
            pDialog.contentText =
                activity.resources.getString(R.string.txt_remove_msg_confirmation)
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            pDialog.setConfirmClickListener {
                pDialog.dismissWithAnimation()
                dialogChat.socketRemoveMsgEmit(messageId)
            }
            pDialog.isShowCancelButton
            pDialog.cancelText = activity.resources.getString(R.string.txt_no)
            pDialog.confirmText = activity.resources.getString(R.string.txt_yes)
            pDialog.setCancelable(false)
            pDialog.show()
        }

        fun setAllDays(activity: Activity, alertDialogCallBack: AlertDialogCallBack) {
            hideKeyboard(activity!!)
            val pDialog = SweetAlertDialog(activity!!, SweetAlertDialog.WARNING_TYPE)
            pDialog.titleText = activity.resources.getString(R.string.txt_confirmation)
            pDialog.contentText =
                activity.resources.getString(R.string.txt_settings_set_all_days_data)
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            pDialog.setConfirmClickListener {
                pDialog.dismissWithAnimation()
                alertDialogCallBack.onDialogPositiveClick()
            }
            pDialog.isShowCancelButton
            pDialog.cancelText = activity.resources.getString(R.string.txt_no)
            pDialog.confirmText = activity.resources.getString(R.string.txt_yes)
            pDialog.setCancelable(false)
            pDialog.show()
        }

        fun validateEtWithLength(et: EditText): Boolean {
            return if (isSetLength(et.text.toString())) {
                setBackground(et, R.drawable.bg_white_with_gray_border)
                true
            } else {
                setBackground(et, R.drawable.bg_white_with_red_border)
                false
            }
        }

        fun validatePhone(et: EditText): Boolean {
            return if (validatePhone(et.text.toString())) {
                setBackground(et, R.drawable.bg_white_with_gray_border)
                true
            } else {
                setBackground(et, R.drawable.bg_white_with_red_border)
                false
            }
        }

        fun validateAddress(tv: TextView, rl: RelativeLayout): Boolean {
            return if (isSet(tv.text.toString())) {
                setBackground(rl, R.drawable.bg_white_with_gray_border)
                true
            } else {
                setBackground(rl, R.drawable.bg_white_with_red_border)
                false
            }
        }


        fun validateCity(id: Int, rl: RelativeLayout): Boolean {
            return if (id == 0) {
                setBackground(rl, R.drawable.bg_white_with_red_border)
                false
            } else {
                setBackground(rl, R.drawable.bg_white_with_gray_border)
                true
            }
        }

        fun validateInput(et: EditText): Boolean {
            return if (isSet(et.text.toString())) {
                setBackground(et, R.drawable.bg_white_with_gray_border)
                true
            } else {
                setBackground(et, R.drawable.bg_white_with_red_border)
                false
            }
        }

        fun convertMilliSecondsToMinutes(milliseconds: Long): String {
            var minutes = (milliseconds / 1000) / 60
            var seconds = (milliseconds / 1000) % 60
            return "$minutes:$seconds"
        }

        fun openGallery(activity: Activity, code: Int) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            activity.startActivityForResult(galleryIntent, code)
        }

        fun openCamera(activity: Activity, code: Int): Uri {
            val values = ContentValues()
            values.put(
                MediaStore.Images.Media.TITLE,
                activity.resources.getString(R.string.txt_new_picture)
            )
            values.put(
                MediaStore.Images.Media.DESCRIPTION,
                activity.resources.getString(R.string.txt_from_camera)
            )
            var imageUri: Uri?
            imageUri = activity.contentResolver
                ?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            activity.startActivityForResult(intent, code)
            return imageUri!!
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun checkRunTimePermission(activity: Activity) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }

        fun touchScreenToHideKeyboard(activity: Activity, view: View) {

            // Set up touch listener for non-text box views to hide keyboard.
            if (view !is EditText) {
                view.setOnClickListener {
                    hideKeyboard(activity)
                    false
                }
            }

            //If a layout container, iterate over children and seed recursion.
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    touchScreenToHideKeyboard(activity, innerView)
                }
            }
        }

        fun touchScreenToHideOverlayKeyboard(activity: Activity, view: View) {

            // Set up touch listener for non-text box views to hide keyboard.
            if (view !is EditText) {
                view.setOnClickListener {
                    if (isKeyboardOpen(activity!!))
                        hideKeyboardForOverlay(activity!!)
                    false
                }
            }

            //If a layout container, iterate over children and seed recursion.
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val innerView = view.getChildAt(i)
                    touchScreenToHideOverlayKeyboard(activity, innerView)
                }
            }

        }

        fun getRecordAudioFilePath(context: Context, fileName: String): String {
            return context.externalCacheDir!!.absolutePath + "/" + fileName + ".m4a"
        }

        fun convertTimeFromSeconds(recordTime: Long): String {
            var minutes = (recordTime / 1000) / 60
            var seconds = (recordTime / 1000) % 60
            var finalMinutes = if (minutes < 10)
                "0$minutes"
            else
                "$minutes"
            var finalSeconds = if (seconds < 10)
                "0$seconds"
            else
                "$seconds"
            return "$finalMinutes:$finalSeconds"
        }

        private fun convertMilliSecondsToSeconds(milliseconds: Long): Int {
            return ((milliseconds / 1000)).toInt()
        }

        fun checkVideoLength(activity: Activity, filePath: String): Boolean {
            var retriever = MediaMetadataRetriever()
            retriever.setDataSource(
                activity,
                Uri.parse(File(filePath).toString())
            )
            var time =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var timeInMilliSec = time.toLong()
            retriever.release()


            return convertMilliSecondsToSeconds(timeInMilliSec) > Constants.chatVideoTime
        }

        fun encryptValue(value: String): String {
            return try {
                EncryptDecryptString.encrypt(value)
            } catch (e: Exception) {
                ""
            }
        }

        fun decryptValue(encryptedValue: String): String {
            return try {
                EncryptDecryptString.decrypt(encryptedValue)
            } catch (e: Exception) {
                ""
            }
        }

        private fun getCurrentDateTime(): String {
            var cal = Calendar.getInstance()
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(cal.time)
        }

        private fun getCurrentDateTimeWithoutSeconds(): String {
            var cal = Calendar.getInstance()
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            return sdf.format(cal.time)
        }

        fun changeDateFormatTo24Hour(date: String): String {
            var input = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)
            var output = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            try {
                return output.format(input.parse(date))
            } catch (e: Exception) {

            }
            return ""
        }

        fun getMinutesFromTwoTimes(serverDateTime: String): Int {

            var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var d1: Date?
            var d2: Date?

            return try {
                d1 = format.parse(serverDateTime)
                d2 = format.parse(getCurrentDateTime())

                var diff = d2.time - d1.time
                var diffMinutes = diff / (60 * 1000) % 60

                diffMinutes.toInt()

            } catch (e: Exception) {
                -1
            }
        }

        fun isEmulator(): Boolean {
            return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.PRODUCT.contains("sdk_google")
                    || Build.PRODUCT.contains("google_sdk")
                    || Build.PRODUCT.contains("sdk")
                    || Build.PRODUCT.contains("_sdk")
                    || Build.PRODUCT.contains("sdk_")
                    || Build.PRODUCT.contains("sdk_x86")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.PRODUCT.contains("emulator")
                    || Build.PRODUCT.contains("simulator")
        }

        fun callServiceDownActivity(ctx: Context) {
            if (!MualijProApplication.isServiceDown) {
                MualijProApplication.isServiceDown = true
                val intent = Intent(ctx, TemporaryServiceDownActivity::class.java)
                intent.putExtra(Constants.intentConstKey, Constants.intentConstValue)
                ctx.startActivity(intent)
            }
        }

        fun closeServiceDownActivity() {
            if (MualijProApplication.isServiceDown) {
                if (TemporaryServiceDownActivity.mActivity != null)
                    TemporaryServiceDownActivity.mActivity?.finish()
            }
        }

        fun dateFormatToDate(date: String): Date {
            var input = SimpleDateFormat(Constants.baseDateTimeFormat)
            return input.parse(date)
        }

        fun compareOnlineAppointmentDate(serverDateStr: String): String? {

            var serverDate = dateFormatToDate(serverDateStr)
            var currentDate = dateFormatToDate(getCurrentDateTimeWithoutSeconds())

            if (serverDate.after(currentDate)) {
                return "You can't complete future appointments"
            } else {
                return null
            }

        }

        fun resizeAndCompressImageBeforeSend(
            context: Context,
            filePath: String?,
            fileName: String
        ): File? {
            val MAX_IMAGE_SIZE = 700 * 1024; // max final file size in kilobytes

            // First decode with inJustDecodeBounds=true to check dimensions of image
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)

            // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
            //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
            options.inSampleSize = calculateInSampleSize(options, 800, 800)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bmpPic = BitmapFactory.decodeFile(filePath, options)
            var compressQuality = 100 // quality decreasing by 5 every loop.
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                Log.d("compressBitmap", "Quality: $compressQuality")
                bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
                Log.d("compressBitmap", "Size: " + streamLength / 1024 + " kb")
            } while (streamLength >= MAX_IMAGE_SIZE)

            try {
                //save the resized and compressed file to disk cache
                val bmpFile =
                    FileOutputStream(context.cacheDir.toString() + fileName)
                bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile)
                bmpFile.flush()
                bmpFile.close()
            } catch (e: java.lang.Exception) {
                Log.e("compressBitmap", "Error on saving file")
            }
            //return the path of resized and compressed file
            return File(context.cacheDir.toString() + fileName)
        }


        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val debugTag = "MemoryInformation"
            // Image nin islenmeden onceki genislik ve yuksekligi
            val height = options.outHeight
            val width = options.outWidth

            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }


        fun getTimeString(duration: Long): String? {
            return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                duration
                            )
                        )
            )
        }

        fun deleteUploadedCompressedVideo(path:String){
            val fdelete = File(path)
            if (fdelete.exists()) {
                fdelete.delete()
            }
        }

    }

}