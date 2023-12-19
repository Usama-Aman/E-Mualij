package com.elementary.mualijpro.dialogs

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.composer.Mp4Composer
import com.devlomi.record_view.OnRecordListener
import com.devlomi.record_view.RecordButton
import com.devlomi.record_view.RecordView
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.R
import com.elementary.mualijpro.adapters.ChatAdapter
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.elementary.mualijpro.interfaces.AsyncTaskListener
import com.elementary.mualijpro.interfaces.DashboardDialogsCallBack
import com.elementary.mualijpro.interfaces.updateFromNotifyListener
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.posts.dashboard.appointments.WeeklyAppointments
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.models.sockets.AllMessages
import com.elementary.mualijpro.models.sockets.MediaFileResponse
import com.elementary.mualijpro.mualij.sockets.SocketKeys
import com.elementary.mualijpro.mualij.sockets.interfaces.SocketConCallBack
import com.elementary.mualijpro.networks.APIsTag
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.sockets.interfaces.SocketCallback
import com.elementary.mualijpro.utils.*
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.config.Configurations
import com.jaiselrahman.filepicker.model.MediaFile
import com.kaopiz.kprogresshud.KProgressHUD
import com.taxi.bravoapp.utils.SocketSingleton
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class DialogChat : DialogFragment(), SocketCallback,
    SocketConCallBack, ApiResponseEvent, DashboardDialogsCallBack,
    ConnectivityReceiver.ConnectivityReceiverListener,
    updateFromNotifyListener, ProgressRequestBody.UploadCallbacks, AsyncTaskListener {

    private var isComingFromNotification = false
    private var appointmentId = ""
    private var patientObj = PatientDataResponse()
    private val videoTrim = 1004
    private var appointmentObj = WeeklyAppointments()

    companion object {
        var isChatForeground = false
        var appointmentID = ""
        var videoPath = ""
    }

    private var progressKHUD: KProgressHUD? = null
    private var permissionToRecordAccepted = false
    private val min = 20
    private val max = 1000
    var fileName = ""
    var mediaPlayerAudioClickedPos = 0
    private var recorder: MediaRecorder? = null
    private var audioTime = ""

    var messageValue = ""
    var apiTimeSlotMins = ""
    var splittedTimeSlotMins = ""
    var splittedTimeSlotSecs = ""
    var finalMinsTimer = 0
    var finalsecondsTimer = 0
    var isFirstTimeDataLoad = 0

    private var dialogReportPatient: DialogReportPatient? = null


    var tvTimer: TextView? = null
    private var dialogChatGallery: DialogChatGallery? = null
    var filePath = ""
    var llMain: LinearLayout? = null
    var rlCross: RelativeLayout? = null
    private var mContext: FragmentActivity? = null
    var btnRecord: RecordButton? = null
    var recordView: RecordView? = null
    var rlSendMsg: RelativeLayout? = null
    var rlMessage: LinearLayout? = null
    var etMessage: EditText? = null
    var rlGalley: RelativeLayout? = null
    var imgPatient: CircleImageView? = null
    var tvPatientName: TextView? = null
    var chatEnded: View? = null
    var ivGenderMale: ImageView? = null
    var ivGenderFeMale: ImageView? = null
    var tvDateAndAge: TextView? = null
    var rvMessages: RecyclerView? = null
    var tvTyping: TextView? = null
    var rlNotification: RelativeLayout? = null
    var rlMainParent: RelativeLayout? = null
    var rlFooter: RelativeLayout? = null
    var userdata: User? = null
    private val takePhotoCode = 100
    private val galleryPhotoCode = 200
    private var imagePath: String? = ""
    private var imageUri: Uri? = null
    private var messagesAdapter: ChatAdapter? = null
    var mLayoutManager: LinearLayoutManager? = null
    var allMessagesArray: ArrayList<AllMessages> = ArrayList()
    var progressBar: FrameLayout? = null
    var progress: ProgressBar? = null
    var progressBarStyle: ProgressBar? = null
    var llProgressBarStyle: LinearLayout? = null
    var tvProgress: TextView? = null

    private lateinit var mHandler: Handler
    private lateinit var mHandlerEmit: Handler
    private lateinit var mHandlerNoti: Handler
    private lateinit var mHandlerConnect: Handler
    private var isInitSocket = false
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var apiController: ApiController? = null

    fun newInstance(
        isComingFromNotification: Boolean,
        appointmentId: String,
        patientObj: PatientDataResponse,
        appointmentObj: WeeklyAppointments
    ): DialogChat {
        val fragment = DialogChat()
        var bundle = Bundle()
        var gson = Gson()
        var patientObjString: String
        var appointmentObjString: String
        bundle.putBoolean("isComingFromNotification", isComingFromNotification)
        bundle.putString("appointmentId", appointmentId)
        patientObjString = gson.toJson(patientObj)
        bundle.putString("patientObj", patientObjString)
        appointmentObjString = gson.toJson(appointmentObj)
        bundle.putString("appointmentObj", appointmentObjString)
        fragment.arguments = bundle
        return fragment
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mContext = activity
        if (arguments != null) {
            if (arguments?.containsKey("isComingFromNotification")!!)
                isComingFromNotification =
                    arguments?.getBoolean("isComingFromNotification", false)!!
            if (arguments?.containsKey("appointmentId")!!) {
                appointmentId = arguments?.getString("appointmentId")!!

            }

            if (arguments?.containsKey("patientObj")!!) {
                var json = arguments?.getString("patientObj")
                patientObj =
                    Gson().fromJson(json, PatientDataResponse::class.java)
            }
            if (arguments?.containsKey("appointmentObj")!!) {
                var json1 = arguments?.getString("appointmentObj")
                appointmentObj =
                    Gson().fromJson(json1, WeeklyAppointments::class.java)
                appointmentID = appointmentObj?.getAppointmentId().toString()
            }
        }

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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_chat, parent, false)

        connectivityReceiver = ConnectivityReceiver()
        activity!!.registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        ConnectivityReceiver.connectivityReceiverListener = this
        viewInitialize(view)
        checkRunTimePermissionAudio()
        setListeners()
        getData()
        if (isComingFromNotification) {
            callGetAppointmentData()
        } else {
            setData()
            socketInit()
        }

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        return view
    }

    private fun viewInitialize(v: View) {

        isChatForeground = true
        llMain = v.findViewById(R.id.llMain)
        rlCross = v.findViewById(R.id.rlCross)
        btnRecord = v.findViewById(R.id.btnRecord)
        recordView = v.findViewById(R.id.recordView)
        rlSendMsg = v.findViewById(R.id.rlSendMsg)
        rlMessage = v.findViewById(R.id.rlMessage)
        etMessage = v.findViewById(R.id.etMessage)
        rlGalley = v.findViewById(R.id.rlGalley)
        imgPatient = v.findViewById(R.id.imgPatient)
        tvPatientName = v.findViewById(R.id.tvPatientName)
        chatEnded = v.findViewById(R.id.chatEnded)
        ivGenderMale = v.findViewById(R.id.ivGenderMale)
        ivGenderFeMale = v.findViewById(R.id.ivGenderFeMale)
        tvDateAndAge = v.findViewById(R.id.tvDateAndAge)
        rvMessages = v.findViewById(R.id.rvMessages)

        rvMessages?.setHasFixedSize(true);
        rvMessages?.setItemViewCacheSize(20);
        rvMessages?.isDrawingCacheEnabled = true;
        rvMessages?.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH;


        rlNotification = v.findViewById(R.id.rlNotification)
        tvTyping = v.findViewById(R.id.tvTyping)
        progressBar = v.findViewById(R.id.progressBar)
        progress = v.findViewById(R.id.progress)
        progressBarStyle = v.findViewById(R.id.progressBarStyle)
        llProgressBarStyle = v.findViewById(R.id.llProgressBarStyle)
        tvProgress = v.findViewById(R.id.tvProgress)
        rlMainParent = v.findViewById(R.id.rlMainParent)
        rlFooter = v.findViewById(R.id.rlFooter)
        tvTimer = v.findViewById(R.id.tvTimer)

        progressKHUD = KProgressHUD.create(activity!!)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(false)
            .setAnimationSpeed(2)
            .setDimAmount(10f)
        hideMediaProgress()

        etMessage?.addTextChangedListener(etMessageTextWatcher)
        recordView?.cancelBounds = 4f


        KeyboardUtils.addKeyboardToggleListener(
            activity!!,
            object : KeyboardUtils.SoftKeyboardToggleListener {
                override fun onToggleSoftKeyboard(isVisible: Boolean) {
                    try {
                        if (isVisible)

                            if (isPhone)
                                rlMainParent?.setPadding(
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_left)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_top_bottom_phone)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_right)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_top_bottom_phone)!!
                                        .toInt()
                                )
                            else
                                rlMainParent?.setPadding(
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_left)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_top)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_right)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_open_padding_bottom)!!
                                        .toInt()
                                )
                        else
                                rlMainParent?.setPadding(
                                    activity?.resources?.getDimension(R.dimen.keyboard_close_padding_left)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_close_padding_top)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_close_padding_right)!!
                                        .toInt(),
                                    activity?.resources?.getDimension(R.dimen.keyboard_close_padding_bottom)!!
                                        .toInt()
                                )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (!isVisible)
                        socketTypingEndEmit()
                    moveToBottom(1)
                }
            })

        // Set recorder view
        btnRecord?.setRecordView(recordView)

        recordView?.setOnRecordListener(object : OnRecordListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onStart() {

                handler.postDelayed(mLongPressed, 1000);

                rlMessage?.visibility = View.GONE

                try {
                    if (allMessagesArray != null && allMessagesArray.size > 0 && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek() != null
                        && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek()!!.isPlaying
                    ) {
                        updateAudioPause(mediaPlayerAudioClickedPos)
                    }
                } catch (e: Exception) {

                }
            }

            override fun onCancel() {
                handler.removeCallbacks(mLongPressed);
                onRecord(false)
                if (AppUtils.isKeyboardOpen(activity!!))
                    etMessage?.requestFocus()

                rlMessage?.visibility = View.VISIBLE
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFinish(recordTime: Long) {
                handler.removeCallbacks(mLongPressed);
                onRecord(false)
                rlMessage?.visibility = View.VISIBLE
                onRecord(false)

                audioTime = AppUtils.convertTimeFromSeconds(recordTime)

                if (recordTime > 120000) {

                    if (!activity?.let { AppUtils.isOnline(it) }!!) {
                        activity?.let {
                            CustomAlert.showDropDownNotificationError(
                                it,
                                resources.getString(R.string.txt_alert_information),
                                resources.getString(R.string.txt_audio_lenght_error)
                            )
                        }
                    }
                    return
                }




                if (!activity?.let { AppUtils.isOnline(it) }!!) {
                    activity?.let {
                        CustomAlert.showDropDownNotificationError(
                            it,
                            resources.getString(R.string.txt_alert_information),
                            resources.getString(R.string.txt_alert_internet_connection)
                        )
                    }
                    return
                }
                validateAndSubmitAudioReq()
            }

            override fun onLessThanSecond() {
                handler.removeCallbacks(mLongPressed);
                recorder = null;
                if (AppUtils.isKeyboardOpen(activity!!))
                    etMessage?.requestFocus()

                rlMessage?.visibility = View.VISIBLE
            }
        })
    }

    //Put this into the class
    val handler = Handler()
    var mLongPressed = Runnable {
        onRecord(true)
        //Code for long click
    }


    private fun getData() {
        val userSP: UserDataPref = UserDataPref.getInstance(context!!)
        userdata = userSP.getUserData()!!
    }

    private fun setData() {

        // Doctor Info
        Glide.with(this)
            .load(patientObj?.getPhoto()!!)
            .placeholder(R.drawable.profile_placeholder)
            .error(R.drawable.profile_placeholder)
            .into(imgPatient!!)
        tvPatientName?.text =
            patientObj?.getFullName()
        Constants.isExpired = appointmentObj?.getIsExpired()!!
        ivGenderMale?.visibility = View.GONE
        ivGenderFeMale?.visibility = View.GONE
        if (patientObj?.getGender().equals("male", true))
            ivGenderMale?.visibility = View.VISIBLE
        else
            ivGenderFeMale?.visibility = View.VISIBLE
        tvDateAndAge?.text = patientObj?.getDob() + " (" + patientObj?.getAge() + ")"
        if (appointmentObj?.getIsExpired() == 1)
            rlFooter?.visibility = View.GONE
        else
            rlFooter?.visibility = View.VISIBLE

        // Set Adapter
        allMessagesArray.clear()
        setAdapter()
    }

    private fun setAdapter() {
        messagesAdapter = ChatAdapter(activity!!, this, allMessagesArray, patientObj)
        rvMessages?.adapter = messagesAdapter
        mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mLayoutManager!!.isItemPrefetchEnabled

        rvMessages?.layoutManager = mLayoutManager
    }

    private val mRunnable = Runnable { countdownTimerStart() }

    private fun setTimer() {

        try {


            var minutes = finalMinsTimer.toString()
            var seconds = finalsecondsTimer.toString()
            if (minutes.toInt() < 10)
                minutes = "0$minutes"
            if (seconds.toInt() < 10)
                seconds = "0$seconds"

            tvTimer?.text = "$minutes:$seconds"


            mHandler.postDelayed(
                mRunnable, 1000
            )

        } catch (e: Exception) {

        }
    }


    private fun countdownTimerStart() {
        try {
            // Do something here
            finalsecondsTimer -= 1
            if (finalsecondsTimer <= 0) {
                finalsecondsTimer = 59
                finalMinsTimer -= 1
            }

            if (finalMinsTimer <= -1) {
                stopAppointment()
            } else {
                setTimer()
            }
        } catch (e: Exception) {

        }
    }

    private fun stopAppointment() {

        try {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            appointmentObj?.setIsExpired(1)

            Constants.isExpired = appointmentObj?.getIsExpired()!!
            finalMinsTimer = 0
            tvTimer?.text = "00:00"
            socketChatOffEmit()
            hideMediaProgress()
            setListenerToETMessage(false)
            showLoader()
            Handler().postDelayed(
                {
                    if (activity != null)
                        if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                            setListenerToETMessage(true)
                            hideLoader()
                            socketGetAllMessagesEmit()
                        }
                }, 1000

            )
            rlFooter?.visibility = View.GONE

            if (apiController != null) {
                apiController?.cancelRunningTask()
            }
            appointmentEnd = true
        } catch (e: Exception) {
            appointmentEnd = true
            e.message
        }
    }


    private fun socketInit() {
        try {
            // setCallBackRef must for socket connections listeners error/success
            SocketSingleton.setCallBackRef(this)
            if (SocketSingleton.getSocketObj().connected()) {
                socketOnListeners()
                socketInitialEmits()
            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    private var appShouldStart: Boolean = true
    private var appointmentEnd: Boolean = false
    private var setReverse: Boolean = false

    // ---------------- Socket Listeners -----------------
    private val sendMessage = Emitter.Listener { args ->

        try {
            activity?.runOnUiThread {
                val data = args[0] as JSONObject


                if (data.optString("status").equals("503", true)) {
                    rlFooter?.visibility = View.GONE
                } else if (data.optString("status").equals("500", true)) {

                    appShouldStart = false
                    if (::mHandler.isInitialized) {
                        mHandler?.removeCallbacks(mRunnable)
                    }

                    tvTimer?.text = "00:00"

                    rlFooter?.visibility = View.GONE
                    CustomDialogAlert.showDropDownNotificationError(
                        activity!!,
                        rlNotification!!,
                        resources.getColor(R.color.alert_dialog_error),
                        resources.getString(R.string.txt_alert_information),
                        data.optString("message")
                    )

                    //   socketAllOffListeners()
                    Handler().postDelayed({
                        try {

                            if (activity != null)
                                if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                                    val intent = Intent("dashBoard")
                                    intent.putExtra("type", "refresh_screen")
                                    LocalBroadcastManager.getInstance(activity!!)
                                        .sendBroadcast(intent)

                                }

                        } catch (e: Exception) {

                        }

                        this.dismiss()
                    }, 3000)
                } else if (data.optString("status").equals("502", true)) {


                    if (appointmentObj?.getIsExpired() == 1)
                        rlFooter?.visibility = View.GONE
                    if (!appointmentEnd) {

                        if (data.optString("message")
                                .contains("Your appointment time has been expired.")
                        ) {
                            stopAppointment()

                        }


                    }
                } else {

                    if (data.optString("from_type").equals("doctor")) {

                        /*   if (!mLayoutManager!!.reverseLayout)
                               mLayoutManager!!.reverseLayout = true
                           setReverse = true*/

                        insertDataInArray(data, true)
                        rvMessages?.itemAnimator = null
                        messagesAdapter?.notifyItemInserted(allMessagesArray.size)
                        //  messagesAdapter?.notifyItemRangeChanged(0, 0)
                        moveToBottom(2)
                    }
                }
            }
        } catch (e: Exception) {

        }
    }

    private val receiveMessage = Emitter.Listener { args ->

        try {
            activity?.runOnUiThread {
                val data = args[0] as JSONObject

                if (data.optString("from_type").equals("patient")) {


                    /* if (!mLayoutManager!!.reverseLayout)
                         mLayoutManager!!.reverseLayout = true
                     setReverse = true*/

                    insertDataInArray(data, true)
                    rvMessages?.itemAnimator = null
                    messagesAdapter?.notifyItemInserted(allMessagesArray.size)
                    //  messagesAdapter?.notifyItemRangeChanged(0, 0)
                    moveToBottom(2)
                }

            }
        } catch (e: Exception) {

        }
    }

    var isTimerRunning: Boolean? = false

    private val allMessages = Emitter.Listener { args ->


        try {
            activity?.runOnUiThread {


                val dataObject = args[0] as JSONObject

                if (appShouldStart) {
                    if (appointmentObj?.getIsExpired() == 0) {

                        try {

                            if (dataObject.has("counter_time_ios")) {


                                if (dataObject.getLong("counter_time_ios") < 0) {
                                    tvTimer?.text = "00:00"
                                    isExpiredBody(dataObject)
                                } else {

                                    var secondsFromServer: Long =
                                        Math.abs(dataObject.getLong("counter_time_ios"))

                                    var minute: Long =
                                        TimeUnit.SECONDS.toMinutes((secondsFromServer))


                                    var seconds =
                                        TimeUnit.SECONDS.toSeconds((secondsFromServer) - (minute * 60))

                                    finalMinsTimer = minute.toInt()
                                    finalsecondsTimer = seconds.toInt()

                                    if (finalMinsTimer >= 0) {

                                        isTimerRunning = true

                                        if (::mHandler.isInitialized) {
                                            mHandler?.removeCallbacks(mRunnable)
                                        } else {
                                            // Initialize the handler instance
                                            mHandler = Handler()
                                        }

                                        setTimer()
                                    }

                                }

                            }


                        } catch (e: Exception) {

                        }
                    }


                    if (dataObject.has("is_expired")) {
                        isExpiredBody(dataObject)
                    }

                    setAllMessages(dataObject.getJSONArray("all_message"))

                }


                /*  if (allMessagesArray != null && allMessagesArray.size > 0)
                      Handler().postDelayed({
                          socketMsgSeenEmit()
                      }, 700)*/


            }
        } catch (e: Exception) {

        }
    }

    private fun isExpiredBody(dataObject: JSONObject) {
        try {
            appointmentObj?.setIsExpired(dataObject.getInt("is_expired"))
            Constants.isExpired = appointmentObj?.getIsExpired()!!
            if (appointmentObj?.getIsExpired() == 1) {

                if (dataObject.getJSONArray("all_message").length() == 0)
                    chatEnded?.visibility = View.VISIBLE

                if (::mHandler.isInitialized) {
                    mHandler?.removeCallbacks(mRunnable)
                }
                tvTimer?.text = "00:00"

                rlFooter?.visibility = View.GONE
            } else {
                rlFooter?.visibility = View.VISIBLE

            }
        } catch (e: Exception) {

        }
    }


    private fun setAllMessages(dataArray: JSONArray) {
        try {
            if (dataArray != null && dataArray.length() > 0) {
                var data = dataArray.getJSONObject(0)
                if (appointmentObj?.getAppointmentId()
                        ?.toString() == data.optString("appointment_id")
                )
                    allMessagesArray.clear()
            }
            var isPatientJoined = false
            var isDocJoined = false
            var isChatEnded = false

            for (i in 0 until dataArray.length()) {
                var data = dataArray.getJSONObject(i)
                if (data.optString("type").equals("patient_join", true))
                    isPatientJoined = true
                else if (data.optString("type").equals("doctor_join", true))
                    isDocJoined = true
                insertDataInArray(data, false)
            }

            var isChatEndMsgDuplicate = false
            for (i in 0 until allMessagesArray.size) {
                if (allMessagesArray!![i].getType().equals(
                        Constants.patientOfflineType,
                        true
                    ) || allMessagesArray!![i].getType().equals(
                        Constants.doctorOfflineType,
                        true
                    )
                )
                    isChatEndMsgDuplicate = true
            }

            if (!isChatEndMsgDuplicate && appointmentObj?.getIsExpired() == 1) {
                if (dataArray != null && dataArray.length() > 0) {
                    var data = dataArray.getJSONObject(0)
                    if (appointmentObj?.getAppointmentId()
                            ?.toString() == data.optString("appointment_id")
                    ) {
                        var allMsgObj = AllMessages()
                        isChatEnded = true
                        allMsgObj.setType(Constants.doctorOfflineType)
                        allMessagesArray.add(allMsgObj)
                    }
                }
            }

            if (!isPatientJoined && isDocJoined && isChatEnded && appointmentObj.getIsReported() == 0) {
                if (dataArray != null && dataArray.length() > 0) {
                    var data = dataArray.getJSONObject(0)
                    if (appointmentObj?.getAppointmentId()
                            ?.toString() == data.optString("appointment_id")
                    ) {
                        var allMsgObj = AllMessages()
                        allMsgObj.setType("report")
                        allMessagesArray.add(allMsgObj)
                    }
                }
            }


            notifiyChatAdapter(2)
            moveToBottom(3)

            if (isChatEnded) {
                socketTypingEndEmit()
                socketAllOffListeners()
            }
        } catch (e: Exception) {

        }
    }

    private val typingStart = Emitter.Listener { args ->
        activity?.runOnUiThread {
            tvTyping?.visibility = View.VISIBLE
        }
    }

    private val typingEnd = Emitter.Listener { args ->
        activity?.runOnUiThread {
            tvTyping?.visibility = View.GONE
        }
    }

    private val removeMessage = Emitter.Listener { args ->

        try {
            activity?.runOnUiThread {
                val data = args[0] as JSONObject
                if (allMessagesArray != null)
                    for (i in 0 until allMessagesArray.size)
                        if (data.optString("message_id") == allMessagesArray!![i].getTimeStamp()) {
                            if (allMessagesArray!![i].getMediaPlayerSeek() != null && allMessagesArray!![i].getMediaPlayerSeek()?.isPlaying!!) {
                                allMessagesArray!![i].getMediaPlayerSeek()?.pause()
                                allMessagesArray!![i].getMediaPlayerSeek()?.stop()
                            }
                            allMessagesArray.removeAt(i)
                            break
                        }
                notifiyChatAdapter(3)
            }
        } catch (e: Exception) {

        }
    }

    private val messageSeen = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val dataArray = args[0] as JSONArray
            setAllMessages(dataArray)
        }
    }

    private val messageDelivered = Emitter.Listener { args ->
        activity?.runOnUiThread {
            val dataArray = args[0] as JSONArray

            setAllMessages(dataArray)
        }
    }

    private fun socketOnListeners() {

        try {
            SocketSingleton.getSocketObj().off(SocketKeys.keyMsgDelivered)
            SocketSingleton.getSocketObj().off(SocketKeys.keySendMessage)

            SocketSingleton.getSocketObj().on(SocketKeys.keySendMessage, sendMessage)
            SocketSingleton.getSocketObj().on(SocketKeys.keySendMessage, receiveMessage)
            SocketSingleton.getSocketObj().on(SocketKeys.keyAllMessages, allMessages)
            SocketSingleton.getSocketObj().on(SocketKeys.keyTypingStart, typingStart)
            SocketSingleton.getSocketObj().on(SocketKeys.keyTypingEnd, typingEnd)
            SocketSingleton.getSocketObj().on(SocketKeys.keyRemoveMsg, removeMessage)
        } catch (e: Exception) {

        }
    }

    private fun socketOffListeners() {

        try {
            SocketSingleton.getSocketObj().off(SocketKeys.keyAllMessages)
            SocketSingleton.getSocketObj().off(SocketKeys.keyTypingStart)
            SocketSingleton.getSocketObj().off(SocketKeys.keyTypingEnd)
            SocketSingleton.getSocketObj().off(SocketKeys.keyRemoveMsg)
        } catch (e: Exception) {

        }
    }

    private fun socketAllOffListeners() {

        try {
            SocketSingleton.getSocketObj().off(SocketKeys.keySendMessage)
            SocketSingleton.getSocketObj().off(SocketKeys.keyAllMessages)
            SocketSingleton.getSocketObj().off(SocketKeys.keyTypingStart)
            SocketSingleton.getSocketObj().off(SocketKeys.keyTypingEnd)
            SocketSingleton.getSocketObj().off(SocketKeys.keyRemoveMsg)
        } catch (e: Exception) {

        }
    }

    private fun socketInitialEmits() {

        try {
            if (activity != null) {
                activity!!.runOnUiThread {
                    socketOnlineEmit()
                    setListenerToETMessage(false)
                    // AppUtils.showProgressBarChat(progressBar!!, activity!!)
                    showLoader()


                    if (!::mHandlerEmit.isInitialized)
                        mHandlerEmit = Handler()

                    if (::mHandlerEmit.isInitialized)
                        mHandlerEmit.removeCallbacksAndMessages(null)


                    mHandlerEmit?.postDelayed(
                        {
                            try {
                                if (activity != null)
                                    if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {

                                        setListenerToETMessage(true)
                                        hideLoader()
                                        socketGetAllMessagesEmit()
                                    }

                            } catch (e: Exception) {

                            }
                        }, 600
                    )

                }
            }
        } catch (e: Exception) {

        }
    }

    private fun socketOnlineEmit() {
        try {
            var paramObject = AppUtils.socketGenericParams(context!! as Activity)
            paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
            paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
            paramObject.put(
                Constants.socketParamMessage,
                resources.getString(R.string.txt_doctor_online)
            )
            paramObject.put(Constants.socketParamType, Constants.doctorJoin)
            paramObject.put(Constants.socketParamVideoThumb, "")
            paramObject.put(Constants.socketParamAudioTime, "")

            SocketSingleton.getSocketObj()?.emit(SocketKeys.keyOnline, paramObject.toString())
        } catch (e: Exception) {

        }

    }


    private fun socketGetAllMessagesEmit() {
        try {
            if (activity != null)
                if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                    var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                    paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                    paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())

                    SocketSingleton.getSocketObj()
                        ?.emit(SocketKeys.keyAllMessages, paramObject.toString())
                }
        } catch (e: Exception) {
        }
    }

    var task: sendMessageTask? = null
    private fun socketSendMsgEmit() {

        try {
            if (SocketSingleton.getSocketObj().connected()) {

                /*  var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                  paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                  paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
                  paramObject.put(Constants.socketParamMessage, messageValue)
                  paramObject.put(Constants.socketParamType, Constants.messageType)
                  paramObject.put(Constants.socketParamVideoThumb, "")
                  paramObject.put(Constants.socketParamAudioTime, "")

                  SocketSingleton.getSocketObj()
                      ?.emit(SocketKeys.keySendMessage, paramObject.toString())*/

                sendMessageTask(
                    activity!!,
                    patientObj,
                    messageValue
                ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {
        }
    }

    private fun socketTypingStartEmit() {

        try {
            if (SocketSingleton.getSocketObj().connected()) {
                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keyTypingStart, paramObject.toString())
                //   typingStartTask(activity!!, patientObj, 1).execute()

            } else {
                CustomDialogAlert.showDropDownNotificationErrorType(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                if (!isInitSocket)
                    onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    private fun socketTypingEndEmit() {
        try {
            if (SocketSingleton.getSocketObj().connected()) {

                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())

                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keyTypingEnd, paramObject.toString())


                //  typingStartTask(activity!!, patientObj, 2).execute()
            } else {
                /*   CustomDialogAlert.showDropDownNotificationErrorType(
                       activity!!,
                       rlNotification!!,
                       resources.getColor(R.color.alert_dialog_error),
                       resources.getString(R.string.txt_alert_information),
                       resources.getString(R.string.socket_connection_error)
                   )*/

                if (!isInitSocket)
                    onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    fun socketRemoveMsgEmit(messageId: String) {

        try {
            if (SocketSingleton.getSocketObj().connected()) {
                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj?.getPatientId())
                paramObject.put(Constants.socketParamMessageId, messageId)
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())

                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keyRemoveMsg, paramObject.toString())
            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    private fun socketMsgSeenEmit() {

        try {
            if (SocketSingleton.getSocketObj().connected()) {
                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())

                SocketSingleton.getSocketObj()?.emit(SocketKeys.keyMsgSeen, paramObject.toString())
            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {
        }

    }

    private fun socketMsgDeliveredEmit() {

        if (SocketSingleton.getSocketObj().connected()) {

            var paramObject = AppUtils.socketGenericParams(context!! as Activity)
            paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
            paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())

            SocketSingleton.getSocketObj()?.emit(SocketKeys.keyMsgDelivered, paramObject.toString())
        } else {

            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.socket_connection_error)
            )

            onSocketConReConnect()
        }
    }

    private fun socketChatMediaFileEmit(type: String, mediaFile: MediaFileResponse) {
        try {
            if (SocketSingleton.getSocketObj().connected()) {

                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
                paramObject.put(Constants.socketParamMessage, mediaFile.getFilePath())
                when (type) {
                    "image" -> paramObject.put(Constants.socketParamType, Constants.imageType)
                    "audio" ->
                        paramObject.put(Constants.socketParamType, Constants.audioType)
                    "video" -> paramObject.put(Constants.socketParamType, Constants.videoType)
                }
                paramObject.put(Constants.socketParamAudioTime, audioTime)
                paramObject.put(Constants.socketParamVideoThumb, mediaFile.getThumbnailPath())

                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keyChatMediaFile, paramObject.toString())

                audioTime = ""
            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    private fun socketChatOffEmit() {
        try {
            if (SocketSingleton.getSocketObj().connected()) {
                var paramObject = AppUtils.socketGenericParams(context!! as Activity)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
                paramObject.put(Constants.socketParamMessage, Constants.doctorOfflineMsg)
                paramObject.put(Constants.socketParamType, Constants.doctorOfflineType)
                paramObject.put(Constants.socketParamVideoThumb, "")

                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keyChatOffline, paramObject.toString())
            } else {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )

                onSocketConReConnect()
            }
        } catch (e: Exception) {

        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun checkRunTimePermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkRunTimePermissionAudio() {
        requestPermissions(
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            2
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setListeners() {

        rlGalley?.setOnClickListener {

            if (!SocketSingleton.getSocketObj().connected()) {
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.socket_connection_error)
                )
                onSocketConReConnect()
                return@setOnClickListener
            }

            checkRunTimePermission()
        }

        setListenerToETMessage(true)

        rlCross?.setOnClickListener {
            if (AppUtils.isKeyboardOpen(activity!!))
                AppUtils.hideKeyboardForOverlay(activity!!)
            dismissDialog()
        }

        rlSendMsg?.setOnClickListener {
            messageValue = etMessage?.text.toString().trim()
            etMessage?.setText("")
            rlSendMsg?.visibility = View.GONE
            btnRecord?.visibility = View.VISIBLE
            socketSendMsgEmit()


        }
    }


    private val etMessageTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

            try {
                if (AppUtils.isSet(p0.toString().trim())) {

                    socketTypingStartEmit()


                    rlSendMsg?.visibility = View.VISIBLE
                    btnRecord?.visibility = View.GONE
                } else {
                    socketTypingEndEmit()

                    rlSendMsg?.visibility = View.GONE
                    btnRecord?.visibility = View.VISIBLE
                }
            } catch (e: Exception) {

            }


        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


        }
    }

    var toEnableEditText: Boolean = true

    private fun setListenerToETMessage(boolean: Boolean) {

        if (etMessage == null)
            return
        toEnableEditText = boolean
        /*    if (boolean) *//*etMessage?.addTextChangedListener(etMessageTextWatcher)*//* toEnableEditText =
            true
        else *//*etMessage?.removeTextChangedListener(etMessageTextWatcher)*//* toEnableEditText = false*/
    }


    // <------------- Http Success/Fail CallBack ------->
    override fun onSuccess(requestType: Int, webResponse: Any) {
        try {

            stopRecording()

            setListenerToETMessage(true)
            AppUtils.touchScreenEnable(activity!!)

            hideLoader()
            hideMediaProgress()
            var objectData = webResponse as WebResponse<*>
            var mediaFile: MediaFileResponse? =
                CommonFunctions.convertDataType(objectData, MediaFileResponse::class.java)
            when (requestType) {
                APIsTag.uploadChatImage -> socketChatMediaFileEmit(
                    "image",
                    mediaFile!!
                )
                APIsTag.uploadChatAudio -> {

                    socketChatMediaFileEmit(
                        "audio",
                        mediaFile!!
                    )

                    try {
                        var audioFile =
                            File(AppUtils.getRecordAudioFilePath(activity!!, fileName))
                        if (audioFile.exists())
                            audioFile.delete()
                    } catch (e: Exception) {
                    }

                }
                APIsTag.uploadChatVideo -> {
                    socketChatMediaFileEmit(
                        "video",
                        mediaFile!!
                    )
                    AppUtils.deleteUploadedCompressedVideo(filePath)
                }
                APIsTag.appointmentDetail -> {
                    var appoitnmentData: PatientDataResponse? =
                        CommonFunctions.convertDataType(objectData, PatientDataResponse::class.java)

                    appointmentObj?.setAppointmentId(appoitnmentData?.getAppointmentId())
                    appointmentObj?.setPatientId(appoitnmentData?.getPatientId())
                    appointmentObj?.setAppointmentTime(appoitnmentData?.getAppointmentTime())
                    appointmentObj?.setAppointmentDate(appoitnmentData?.getAppointmentDate())
                    appointmentObj?.setAppointmentType(appoitnmentData?.getAppointmentType())
                    appointmentObj?.setIsExpired(appoitnmentData?.getIsExpired())
                    appointmentObj?.setFullName(appoitnmentData?.getFullName())
                    appointmentObj?.setIsAddPrescription(appoitnmentData?.getIsAddPrescription())
                    patientObj = appoitnmentData!!

                    setData()
                    socketInit()

                }
            }
        } catch (e: Exception) {

        }
    }

    override fun onError(requestType: Int, errorMessage: String) {
        try {

            stopRecording()
            setListenerToETMessage(true)
            AppUtils.touchScreenEnable(activity!!)
            hideLoader()
            hideMediaProgress()

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

    // <------------- Sockets Success/Fail Callback ------------->

    override fun onAckReqSuccess(responseObject: Any, tag: Any) {
        try {
            setListenerToETMessage(true)
            hideLoader()
        } catch (e: Exception) {

        }
    }

    override fun onAckReqFailure(message: String, tag: Any) {
        try {
            setListenerToETMessage(true)
            hideLoader()
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                message
            )

        } catch (e: Exception) {

        }
    }

    var socketConnected = false
    override fun onSocketConSuccess() {

        try {

            if (SocketSingleton.getSocketObj().connected()) {

                /* if (::mHandlerConnect.isInitialized)
                     mHandlerConnect.removeCallbacksAndMessages(null)*/

                btnRecord?.isEnabled = true
                rlGalley?.isEnabled = true
                isInitSocket = false
                socketOnListeners()
                socketInitialEmits()
                socketConnected = true
            } else {


                if (activity != null) {
                    activity!!.runOnUiThread {
                        CustomDialogAlert.showDropDownNotificationError(
                            activity!!,
                            rlNotification!!,
                            resources.getColor(R.color.alert_dialog_error),
                            resources.getString(R.string.txt_alert_information),
                            resources.getString(R.string.socket_connection_error)
                        )
                    }
                }

                onSocketConReConnect()
            }
        } catch (e: Exception) {

        }
    }

    override fun onSocketConReConnect() {

        try {
            if (!AppUtils.isOnline(activity!!))
                return
/*
        if (!::mHandlerConnect.isInitialized)
            mHandlerConnect = Handler()

        if (::mHandlerConnect.isInitialized)
            mHandlerConnect.removeCallbacksAndMessages(null)*/

            if (!SocketSingleton.getSocketObj().connected()) {

                try {
                    if (activity != null)
                        if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                            try {
                                isInitSocket = true

                                //  if (!isInitSocket) {
                                SocketSingleton.initSocket()

                                if (AppUtils.isOnline(activity!!)) {
                                    if (activity != null) {
                                        activity!!.runOnUiThread {
                                            if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                                                if (etMessage != null)
                                                    etMessage?.clearFocus()
                                                /* if (AppUtils.isKeyboardOpen(activity!!))
                                                 AppUtils.hideKeyboardForOverlay(activity!!)*/
                                                /*btnRecord?.isEnabled = false
                                                rlGalley?.isEnabled = false*/
                                                showLoader()
                                            }
                                        }
                                    }

                                }
                                //  }
                            } catch (e: Exception) {

                            }
                        }

                } catch (e: Exception) {

                }
            } else {
                hideLoader()
                /* if (::mHandlerConnect.isInitialized)
                     mHandlerConnect.removeCallbacksAndMessages(null)*/
            }
        } catch (e: Exception) {

        }
    }

    override fun onSocketError(message: String) {
        try {

            /* if (::mHandlerConnect.isInitialized)
                 mHandlerConnect.removeCallbacksAndMessages(null)*/

            isInitSocket = false
            setListenerToETMessage(true)
            hideLoader()

            if (AppUtils.isOnline(activity!!) && isInitSocket)
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    message
                )

            if (AppUtils.isOnline(activity!!) && isInitSocket)
                onSocketConReConnect()

        } catch (e: Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (Constants.isExpired == 1)
            return

        when (requestCode) {
            videoTrim -> {
                filePath = videoPath
                validateAndSubmitVideoReq()
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {


                var resultUri: Uri?
                var result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    resultUri = result!!.uri
                    filePath = resultUri!!.path!!
                    validateAndSubmitImageReq()
                }
            }

            3 -> {


                try {
                    if (data != null) {

                        var files: ArrayList<MediaFile> =
                            data?.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!

                        if (files != null && files.size > 0) {

                            filePath = files[0].path

                            if (filePath.contains("jpeg") || filePath.contains("png") || filePath.contains(
                                    "jpg"
                                )
                            ) {
                                try {
                                    val viewUri = files[0].uri
                                    CropImage.activity(viewUri).start(
                                        activity!!,
                                        Constants.fragmentDashboardRef!!
                                    )
                                } catch (e: Exception) {

                                }

                            } else if (filePath.contains("mp4") || filePath.contains("m4a") || filePath.contains(
                                    "mov"
                                )
                            ) {


                                if (activity?.let { AppUtils.checkVideoLength(it, filePath) }!!) {
                                    videoPath = filePath
                                    val intent = Intent(
                                        activity,
                                        TrimmerActivity::class.java
                                    )
                                    startActivityForResult(intent, videoTrim)

                                } else {

                                    compressFilePath = compress()
                                    showMediaProgress(activity!!.resources.getString(R.string.txt_please_wait_file_is_prepared))
                                    Mp4Composer(filePath, compressFilePath)
                                        .size(540, 540)
                                        .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                                        .listener(object : Mp4Composer.Listener {
                                            override fun onProgress(progress: Double) {
                                                progressBarStyle!!.progress =
                                                    (progress * 100).toInt()
                                            }

                                            override fun onCompleted() {

                                                activity!!.runOnUiThread {
                                                    hideLoader()
                                                    filePath = compressFilePath
                                                    validateAndSubmitVideoReq()
                                                }
                                            }

                                            override fun onCanceled() {
                                                hideLoader()
                                            }

                                            override fun onFailed(exception: java.lang.Exception?) {
                                                hideLoader()
                                            }
                                        })
                                        .start()

                                }
                            }
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }


    var compressFilePath = ""
    val APP_DIR = "VideoCompressor"

    val COMPRESSED_VIDEOS_DIR = "/Compressed_Videos/"

    val TEMP_DIR = "/Temp/"


    fun try2CreateCompressDir() {
        var f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR
        )
        f.mkdirs()
        f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR
        )
        f.mkdirs()
        f = File(
            Environment.getExternalStorageDirectory(),
            File.separator + APP_DIR + TEMP_DIR
        )
        f.mkdirs()
    }


    fun compress(/*path: String*/): String {
        try2CreateCompressDir()
        return (Environment.getExternalStorageDirectory()
            .toString() + File.separator
                + APP_DIR
                + COMPRESSED_VIDEOS_DIR + "VIDEO_" + SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.US
        ).format(Date()) + ".mp4")
        // VideoCompressor(this, activity!!).execute(path, filePath)

    }


    class VideoCompreAsyn(activity: DialogChat, ctx: Context) :
        AsyncTask<String, String, String>() {

        private var listener: AsyncTaskListener = activity as AsyncTaskListener
        var context = ctx

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg paths: String?): String? {
            var filePath: String? = null
            try {
                //  filePath = SiliCompressor.with(context).compressVideo(paths[0], paths[1])
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            return filePath
        }

        override fun onPostExecute(compressed: String) {
            super.onPostExecute(compressed)
            listener?.updateResult(compressed)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    showPictureDialog()
                } else
                    AppUtils.showPermissionAlertDialog(
                        activity!!
                    )
                return
            }
            2 -> {
                if (grantResults.size > 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
                    permissionToRecordAccepted =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                } else
                    AppUtils.showPermissionAlertDialog(
                        activity!!
                    )
                return
            }
        }
    }

    private fun showPictureDialog() {

        var intent = Intent(activity!!, FilePickerActivity::class.java)
        intent.putExtra(
            FilePickerActivity.CONFIGS, Configurations.Builder()
                .setShowImages(true)
                .setShowVideos(true)
                .enableImageCapture(true)
                .enableVideoCapture(true)
                .setMaxSelection(1)
                .build()
        )
        startActivityForResult(intent, 3)

    }

    private fun insertDataInArray(data: JSONObject, fromSend: Boolean) {
        try {
            var allMsgObj = AllMessages()
            if (appointmentObj?.getAppointmentId().toString() == data.optString("appointment_id")) {
                allMsgObj.setTimeStamp(data.optString("timestamp"))
                allMsgObj.setChatId(data.optString("chat_id"))
                allMsgObj.setFromId(data.optString("from_id"))
                allMsgObj.setFromType(data.optString("from_type"))
                allMsgObj.setToId(data.optString("to_id"))
                allMsgObj.setToType(data.optString("to_type"))
                /*  allMsgObj.setIsSeen(data.optString("is_seen"))*/
                allMsgObj.setAudioTotalTime(data.optString("media_time"))
                allMsgObj.setIsDeleted(data.optString("is_deleted"))
                allMsgObj.setType(data.optString("type"))
                allMsgObj.setDate(data.optString("date"))
                allMsgObj.setMessage(data.optString("message"))
                allMsgObj.setMediaUrl(data.optString("media_url"))
                allMsgObj.setMediaThumbUrl(data.optString("media_thumb_url"))

                var isChatEndMsgDuplicate = false
                if (allMessagesArray != null && allMessagesArray.size > 1) {
                    if (allMessagesArray!![allMessagesArray.size - 1].getMessage().equals(
                            "CHAT HAS ENDED",
                            true
                        )
                    )
                        isChatEndMsgDuplicate = true
                }

                if (allMsgObj.getIsDeleted().equals("false", true) && !isChatEndMsgDuplicate) {
                    /*  if (fromSend)
                          allMessagesArray.add(0, allMsgObj)
                      else*/
                    allMessagesArray.add(allMsgObj)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun removeMessage(position: Int) {
        try {
            if (recorder == null)
                removeMsgConfirmation(position)
        } catch (e: Exception) {

        }
    }

    fun chatGallery(imageUrl: String) {
        try {
            dialogChatGallery = DialogChatGallery(imageUrl)
            fragmentManager?.let { dialogChatGallery!!.show(it, "") }
        } catch (e: Exception) {

        }
    }

    private fun removeMsgConfirmation(position: Int) {
        AppUtils.removeMsgAlert(activity!!, this, allMessagesArray!![position].getTimeStamp())
    }


    private fun validateAndSubmitImageReq() {

        if (!activity?.let { AppUtils.isOnline(it) }!!) {
            activity?.let {
                CustomAlert.showDropDownNotificationError(
                    it,
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.txt_alert_internet_connection)
                )
            }
            return
        }

        if (!SocketSingleton.getSocketObj().connected()) {
            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.socket_connection_error)
            )
            onSocketConReConnect()
            return
        }

        try {

            var parseValue = "text/plain"
            val type: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), "image")

            var file: MultipartBody.Part? = null
            if (filePath != "") {

                val imageFile = AppUtils.resizeAndCompressImageBeforeSend(
                    activity!!,
                    filePath,
                    "fileNAmeSet"
                )
                val imageBody = ProgressRequestBody(imageFile, "image", this)
                file = MultipartBody.Part.createFormData(
                    "file",
                    imageFile?.name,
                    imageBody
                )
            }


            setListenerToETMessage(false)
            progressBarStyle?.progress = 0
            showMediaProgress(activity!!.resources.getString(R.string.txt_please_wait_file_uploading))

            val executor = ThreadPoolExecutor(
                5,
                10,
                30,
                TimeUnit.SECONDS,
                LinkedBlockingQueue()
            )
            executor.allowCoreThreadTimeOut(true)
            executor.execute(Runnable {
                try {

                    ApiController.getInstance(activity!!, this).uploadChatImage(
                        type,
                        file
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            })


        } catch (e: Exception) {

        }

    }

    private fun validateAndSubmitVideoReq() {

        if (!AppUtils.isOnline(activity!!)) {
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }


        if (!SocketSingleton.getSocketObj().connected()) {

            CustomDialogAlert.showDropDownNotificationError(
                activity!!,
                rlNotification!!,
                resources.getColor(R.color.alert_dialog_error),
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.socket_connection_error)
            )
            onSocketConReConnect()
            return
        }

        try {

            var parseValue = "text/plain"
            val type: RequestBody? =
                RequestBody.create(MediaType.parse(parseValue), "video")

            var file: MultipartBody.Part? = null


            if (filePath != "") {

                val videoFile = File(filePath!!)
                val videoRequestBody = ProgressRequestBody(videoFile, "video", this)

                var length: Long = videoFile.length()
                length /= 1024

                file = MultipartBody.Part.createFormData(
                    "file",
                    File(filePath!!).name,
                    videoRequestBody
                )
            }

            setListenerToETMessage(false)
            progressBarStyle?.progress = 0
            showMediaProgress(activity!!.resources.getString(R.string.txt_please_wait_file_uploading))

            val executor = ThreadPoolExecutor(
                5,
                10,
                30,
                TimeUnit.SECONDS,
                LinkedBlockingQueue()
            )

            executor.allowCoreThreadTimeOut(true)
            executor.execute(Runnable {
                try {

                    ApiController.getInstance(activity!!, this).uploadChatVideo(
                        type,
                        file
                    )

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            })


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun validateAndSubmitAudioReq() {

        if (!activity?.let { AppUtils.isOnline(it) }!!) {
            activity?.let {
                CustomAlert.showDropDownNotificationError(
                    it,
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.txt_alert_internet_connection)
                )
            }
            return
        }

        try {

            var parseValue = "text/plain"
            val type = RequestBody.create(MediaType.parse(parseValue), "audio")
            var file: MultipartBody.Part? = null
            val audioFile: RequestBody?

            if (AppUtils.getRecordAudioFilePath(activity!!, fileName) != "") {

                var fileUpload = File(AppUtils.getRecordAudioFilePath(activity!!, fileName))


                if (fileUpload.exists()) {


                    val audioFile = ProgressRequestBody(fileUpload, "audio/*", this)


                    file = MultipartBody.Part.createFormData(
                        "file",
                        fileUpload.name,
                        audioFile!!
                    )
                } else {

                    CustomDialogAlert.showDropDownNotificationError(
                        activity!!,
                        rlNotification!!,
                        resources.getColor(R.color.alert_dialog_error),
                        resources.getString(R.string.txt_alert_information),
                        "File type is not valid. Please upload valid audio."
                    )
                    return
                }

            }

            setListenerToETMessage(false)
            AppUtils.touchScreenDisable(activity!!)
            showLoader()

            if (file != null) {

                val executor = ThreadPoolExecutor(
                    5,
                    10,
                    30,
                    TimeUnit.SECONDS,
                    LinkedBlockingQueue()
                )
                executor.allowCoreThreadTimeOut(true)
                executor.execute(Runnable {
                    try {

                        ApiController.getInstance(activity!!, this).uploadChatAudio(
                            type,
                            file
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                })

            } else
                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    "File type is not valid. Please upload valid audio."
                )

        } catch (e: Exception) {
            e.message
        }

    }


    var isRecorderStart = false
    var isRecorderPrepare = false

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startRecording() {
        try {
            if (permissionToRecordAccepted) {

                val audioTempName = RandomString()
                fileName = "Mualij" + audioTempName.nextString()
                recorder = MediaRecorder()
                recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                recorder?.setOutputFile(AppUtils.getRecordAudioFilePath(activity!!, fileName))
                recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                try {
                    recorder?.prepare()
                    isRecorderPrepare = true

                } catch (e: IllegalStateException) {
                    // TODO Auto-generated catch block
                    isRecorderPrepare = false
                } catch (e: java.lang.Exception) {
                    // TODO Auto-generated catch block
                    //Log.e("ERROR ", "IOException")
                    e.printStackTrace()
                    isRecorderPrepare = false
                }


                if (isRecorderPrepare)
                    isRecorderStart = try {
                        recorder?.start()
                        true
                    } catch (e: Exception) {
                        false
                    }

            } else
                checkRunTimePermissionAudio()
        } catch (e: Exception) {

        }
    }

    private fun stopRecording() {
        try {
            if (recorder != null && isRecorderStart) {
                recorder?.stop()
                recorder?.reset()
                recorder?.release()
                recorder = null
                isRecorderStart = false
            }
        } catch (e: Exception) {
            recorder = null
            isRecorderStart = false
        }
        recorder = null
        isRecorderStart = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onRecord(start: Boolean) {

        try {
            if (start)
                startRecording()
            else
                stopRecording()
        } catch (e: Exception) {

        }
    }


    fun updateAudioPause(position: Int) {
        try {
            try {
                mediaPlayerAudioClickedPos = position
                allMessagesArray[position].setAudioSetForPlay(false)
                allMessagesArray[position].pauseAudio()
                allMessagesArray[position].setAudioPlaying(false)
                allMessagesArray[position].setAudioPaused(true)
                allMessagesArray[position]
                    .setAudioProgressBar(allMessagesArray[position].getAudioProgressBar())
                stopUpdateProgress()
                notifiyChatAdapter(4)
            } catch (e: Exception) {

            }
        } catch (e: Exception) {

        }
    }


    fun updateAudioPlay(position: Int, player: MediaPlayer?, seekBar: SeekBar) {

        try {
            if (recorder != null) {


                CustomDialogAlert.showDropDownNotificationError(
                    activity!!,
                    rlNotification!!,
                    resources.getColor(R.color.alert_dialog_error),
                    resources.getString(R.string.txt_alert_information),
                    resources.getString(R.string.txt_recording_alert)
                )

                updateAudioPause(position)
            } else {

                if (player == null)
                    allMessagesArray[position].initMediaPlayer(allMessagesArray[position].getMediaUrl())

                mediaPlayerAudioClickedPos = position
                for (i in allMessagesArray.indices) {
                    if (i != position) {
                        allMessagesArray[i].setAudioSetForPlay(false)
                        allMessagesArray[i].setAudioPlaying(false)
                        allMessagesArray[i].setAudioPaused(false)
                        allMessagesArray[i].setAudioBuffer(false)
                        allMessagesArray[i].pauseAudio()

                    } else {
                        allMessagesArray[i].setAudioSetForPlay(true)
                        allMessagesArray[i].setAudioBuffer(true)
                    }
                }
                notifiyChatAdapter(5)

                if (!allMessagesArray[position].getAudioStarted()!!) {
                    allMessagesArray[position].getMediaPlayerSeek()!!.setOnPreparedListener {
                        if (allMessagesArray[position].getAudioSetForPlay()!!) {
                            playAudio(position, player, seekBar)
                        }
                    }
                } else {
                    playAudio(position, player, seekBar)
                }


            }
        } catch (e: Exception) {

        }

    }

    fun playAudio(position: Int, player: MediaPlayer?, seekBar: SeekBar) {

        if (allMessagesArray[position].getAudioSetForPlay()!!) {
            allMessagesArray[position].getMediaPlayerSeek()!!.seekTo(seekBar.progress)
            allMessagesArray[position].getMediaPlayerSeek()!!.start()
            seekBar.max = allMessagesArray[position].getMediaPlayerSeek()!!.duration
            allMessagesArray[position].setAudioTotalLength(allMessagesArray[position].getMediaPlayerSeek()!!.duration)

            allMessagesArray[position].setAudioBuffer(false)
            allMessagesArray[position].setAudioPlaying(true)
            allMessagesArray[position].setAudioStarted(true)

            updatePlayerProgress(position, allMessagesArray[position].getMediaPlayerSeek())
        }


    }

    private val handlerUpdateMediaProgress = Handler()
    var r: Runnable? = null

    private fun stopUpdateProgress() {
        if (r != null)
            handlerUpdateMediaProgress.removeCallbacks(r)
    }

    private fun updatePlayerProgress(position: Int, player: MediaPlayer?) {
        try {
            if (allMessagesArray[position].getAudioPlaying() === true) {

                if (player != null) {
                    allMessagesArray[position].setAudioProgressBar(player.currentPosition)
                }

                if (player!!.currentPosition < player.duration && player.isPlaying) {
                    r = Runnable {

                        if (activity != null)
                            if (!activity?.isDestroyed!! && !activity?.isFinishing!! && isVisible) {
                                AppUtils.getTimeString(player.currentPosition.toLong())?.let {
                                    allMessagesArray[position].setAudioTotalTimeCurrent(
                                        it
                                    )
                                }
                                notifiyChatAdapter(6)
                                updatePlayerProgress(position, player)
                            }
                    }


                    handlerUpdateMediaProgress.postDelayed(r, 300)
                    // Handler().postDelayed(r, 500)
                } else {
                    allMessagesArray[position].setAudioTotalTimeCurrent(
                        allMessagesArray[position].getAudioTotalTime()
                    )
                    allMessagesArray[position].setAudioPlaying(false)
                    allMessagesArray[position].setAudioPaused(false)
                    allMessagesArray[position].setAudioProgressBar(0)
                    allMessagesArray[position].setAudioTotalLength(0)
                    notifiyChatAdapter(7)
                }
            }
        } catch (e: Exception) {

        }
    }


    fun reportPatient() {
        try {
            dialogReportPatient = DialogReportPatient(
                patientObj!!, this
            )
            fragmentManager?.let { dialogReportPatient!!.show(it, "") }
        } catch (e: Exception) {

        }
    }


    override fun onCallBack(
        type: String?,
        value: String?,
        prescriptionArray: ArrayList<PatientPrescriptionArray>
    ) {
        dismissDialog()
    }

    override fun onDestroy() {
        try {
            if (allMessagesArray != null && allMessagesArray.size > 0 && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek() != null && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek()!!.isPlaying
            )
                allMessagesArray[mediaPlayerAudioClickedPos].stopAudio()

            stopRecording()

            if (::mHandlerEmit.isInitialized) {
                mHandlerEmit.removeCallbacksAndMessages(null);
            }

            /* if (::mHandlerConnect.isInitialized)
                 mHandlerConnect.removeCallbacksAndMessages(null)*/

            if (::mHandlerNoti.isInitialized)
                mHandlerNoti.removeCallbacksAndMessages(null)

            if (::mHandler.isInitialized) {
                if (mRunnable != null) {
                    mHandler.removeCallbacks(mRunnable)
                }
            }

            super.onDestroy()
        } catch (e: Exception) {

        }

        isChatForeground = false
        socketTypingEndEmit()
        socketAllOffListeners()
    }

    private fun dismissDialog() {
        try {
            if (allMessagesArray != null && allMessagesArray.size > 0 && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek() != null && allMessagesArray[mediaPlayerAudioClickedPos].getMediaPlayerSeek()!!.isPlaying
            )
                allMessagesArray[mediaPlayerAudioClickedPos].stopAudio()
        } catch (e: Exception) {

        }
        stopRecording()


        try {
            val intent = Intent("dashBoard")
            intent.putExtra("type", "refresh_screen")
            LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
        } catch (e: Exception) {

        }
        isChatForeground = false
        socketTypingEndEmit()
        socketOffListeners()
        this.dismiss()
    }

    override fun onStop() {
        super.onStop()
        isChatForeground = false

        try {
            if (connectivityReceiver != null) {
                activity!!.unregisterReceiver(
                    connectivityReceiver
                )
                connectivityReceiver = null
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        isChatForeground = true
    }


    private fun callGetAppointmentData() {
        try {
            setListenerToETMessage(false)
            showLoader()
            val paramObject = AppUtils.getGenericParams()
            paramObject.addProperty("appointment_id", appointmentId)
            ApiController.getInstance(activity!!, this).getAppointmentDetail(paramObject)
        } catch (e: Exception) {

        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {
            onSocketConReConnect()
        }
    }


    private fun showLoader() {
        try {
            AppUtils.touchScreenDisable(activity!!)
            progress?.visibility = View.VISIBLE
            progressBar?.visibility = View.VISIBLE
        } catch (e: java.lang.Exception) {
        }

    }

    private fun showMediaProgress(msg: String) {
        llProgressBarStyle?.visibility = View.VISIBLE
        progress?.visibility = View.GONE
        progressBar?.visibility = View.VISIBLE
        progressBarStyle?.visibility = View.VISIBLE
        progressBarStyle?.progress = 0
        tvProgress?.text = msg
    }


    private fun hideMediaProgress() {
        progressBarStyle?.progress = 0
        llProgressBarStyle?.visibility = View.GONE
        progressBarStyle?.visibility = View.GONE
        progressBar?.visibility = View.GONE
    }

    fun hideLoader() {
        try {
            AppUtils.touchScreenEnable(activity!!)
            progress?.visibility = View.GONE
            progressBar?.visibility = View.GONE
        } catch (e: java.lang.Exception) {
        }
    }

    var setNotReverse = false
    private fun notifiyChatAdapter(i: Int) {
        /* if (mLayoutManager!!.reverseLayout)
             mLayoutManager!!.reverseLayout = false*/
        try {
            messagesAdapter?.notifyDataSetChanged()
        } catch (e: Exception) {

        }
    }

    private fun rvScrollToBottom() {
        if (messagesAdapter != null)
            rvMessages?.scrollToPosition(messagesAdapter?.itemCount?.minus(1)!!)

        /*  try {
              messagesAdapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
                  override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                      mLayoutManager?.smoothScrollToPosition(
                          rvMessages,
                          null,
                          messagesAdapter?.itemCount?.minus(1)!!
                      )
                  }
              })
          } catch (e: Exception) {

          }*/
    }

    private fun moveToBottom(i: Int) {
        try {
            if (!isLastVisible()) {
                rvScrollToBottom()
            }
        } catch (e: Exception) {

        }
    }

    fun isLastVisible(): Boolean {
        try {
            val pos = mLayoutManager?.findLastCompletelyVisibleItemPosition()
            val numItems: Int = messagesAdapter?.itemCount!!
            if (pos != null) {
                return pos >= numItems
            }
        } catch (e: Exception) {

        }
        return false
    }


    override fun onProgressUpdate(percentage: Int) {
        progressBarStyle!!.progress = percentage
    }


    class sendMessageTask(
        ctx: Activity,
        patientObj: PatientDataResponse,
        messageValue: String
    ) :
        AsyncTask<Void, Void, String>() {

        private var patientObj = patientObj
        private var ctx = ctx
        private var messageValue = messageValue


        override fun doInBackground(vararg params: Void?): String? {
            try {


                var paramObject = AppUtils.socketGenericParams(ctx)
                paramObject.put(Constants.socketParamToId, patientObj.getPatientId())
                paramObject.put(Constants.socketParamAppnmtId, patientObj.getAppointmentId())
                paramObject.put(Constants.socketParamMessage, messageValue)
                paramObject.put(Constants.socketParamType, Constants.messageType)
                paramObject.put(Constants.socketParamVideoThumb, "")
                paramObject.put(Constants.socketParamAudioTime, "")

                SocketSingleton.getSocketObj()
                    ?.emit(SocketKeys.keySendMessage, paramObject.toString())


            } catch (e: Exception) {
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }
    }

    override fun updateFromNotify(list: ArrayList<Int>) {
    }

    override fun updateResult(path: String) {
        hideLoader()
        filePath = path
        validateAndSubmitVideoReq()
    }

    override fun showProgress() {
        showLoader()
    }


}