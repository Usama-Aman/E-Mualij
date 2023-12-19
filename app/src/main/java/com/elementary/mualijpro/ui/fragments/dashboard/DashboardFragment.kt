package com.elementary.mualijpro.ui.fragments.dashboard

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elementary.mualijpro.R
import com.elementary.mualijpro.adapters.AppointmentTimingAdapter
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.local.dashboard.FinalWeeklyAppointments
import com.elementary.mualijpro.models.posts.dashboard.appointments.AppointmentData
import com.elementary.mualijpro.networks.ApiController
import com.elementary.mualijpro.ui.activities.base.BaseFragment
import com.elementary.mualijpro.utils.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_dashboard.*
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import androidx.core.view.isVisible
import com.elementary.mualijpro.adapters.PatientPrescriptionAdapter
import com.elementary.mualijpro.interfaces.*
import com.elementary.mualijpro.models.posts.dashboard.appointments.WeeklyAppointments
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray
import com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays.*
import com.elementary.mualijpro.models.posts.dashboard.updateprescription.UpdatePrescriptionDataResponse
import com.elementary.mualijpro.networks.APIsTag
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.widget.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.elementary.mualijpro.MualijProApplication.Companion.isPhone
import com.elementary.mualijpro.dialogs.*
import com.elementary.mualijpro.ui.activities.main.MainActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.item_chat_report.*

@Suppress("DEPRECATION")
class DashboardFragment : BaseFragment(), View.OnClickListener, DashboardDialogsCallBack,
    ApiResponseEvent {

    var isComingFromNotification = false
    var notificationType = ""
    var appointmentId = ""
    private var adMobRightUnitId = ""
    private var adMobBottomUnitId = ""
    private var daysDialog: DialogDays? = null
    private var dialogPrescription: DialogPrescription? = null
    private var dialogReportPatient: DialogReportPatient? = null
    private var dialogReschedule: DialogReschedule? = null
    private var dialogChat: DialogChat? = null

    var startDateApi = ""
    var endDateApi = ""
    private var weeklyAppointments = ArrayList<WeeklyAppointments>()
    private var appointmentObj: WeeklyAppointments? = null
    private var finalWeeklyAppointments = ArrayList<FinalWeeklyAppointments>()
    private var medTypeArray = ArrayList<MedicineType>()
    private var medFrequencyArray = ArrayList<MedicineFrequency>()
    private var medDoseArray = ArrayList<MedicineDose>()
    private var medDayArray = ArrayList<MedicineDay>()
    private var startIndex = 0
    var countValue = 5
    var adapterTiming: AppointmentTimingAdapter? = null
    var mLayoutManager: LinearLayoutManager? = null
    var patientDataObj: PatientDataResponse? = null
    var selectedAppointmentDate: String? = null
    var isSwipeRefresh = false
    var mReceiver: BroadcastReceiver? = null
    var adRightView: UnifiedNativeAdView? = null
    var adBottomView: UnifiedNativeAdView? = null
    var appointmentClickedMainPos = 0
    var appointmentClickedChildPos = 0
//    var requestIsInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (arguments != null) {
            if (arguments?.containsKey("isComingFromNotification")!!)
                isComingFromNotification =
                    arguments?.getBoolean("isComingFromNotification", false)!!
            if (arguments?.containsKey("notificationType")!!)
                notificationType = arguments?.getString("notificationType")!!
            if (arguments?.containsKey("appointmentId")!!)
                appointmentId = arguments?.getString("appointmentId")!!

            if (isComingFromNotification && appointmentId != "" && !DialogChat.isChatForeground) {
                patientDataObj = PatientDataResponse()
                appointmentObj = WeeklyAppointments()
                Constants.fragmentDashboardRef = this
                dialogChat = DialogChat().newInstance(
                    isComingFromNotification,
                    appointmentId,
                    patientDataObj!!,
                    appointmentObj!!
                )
                fragmentManager?.let { dialogChat!!.show(it, "") }
            }
        }
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAndSetWeekDate("current")
        setListeners()
        registerBroadCastReceiver()
    }

    private fun loadAds() {
        if (!isPhone)
            adRightView =
                layoutInflater.inflate(R.layout.right_ad_view, null) as UnifiedNativeAdView
        adBottomView = layoutInflater.inflate(R.layout.bottom_ad_view, null) as UnifiedNativeAdView

        if (!isPhone)
            loadRightAdView()
        loadBottomAdView()
    }

    private fun setAdapter() {
        if (finalWeeklyAppointments != null && finalWeeklyAppointments.size > 0) {

            somethingWentWrong(rvAppointmentTime, tvSomethingWrong, false, "")
            // set rating listing adapter
            adapterTiming = AppointmentTimingAdapter(this, activity!!, finalWeeklyAppointments)
            rvAppointmentTime.adapter = adapterTiming
            mLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            rvAppointmentTime.layoutManager = mLayoutManager
        } else
            somethingWentWrong(
                rvAppointmentTime,
                tvSomethingWrong,
                true,
                resources.getString(R.string.txt_no_appointment_found)
            )
    }

    private fun getAndSetWeekDate(value: String) {
        var dateValue = ""
        when (value) {
            "current" -> dateValue = Calender.getCurrentDay()
            "previous" -> dateValue = Calender.getPreviousDay()
            "next" -> dateValue = Calender.getNextDay()
        }

        startDateApi = AppUtils.changeDateFormatToEng(dateValue)
        endDateApi = AppUtils.changeDateFormatToEng(dateValue)

        val currentDate = AppUtils.baseDateFormatEng(Calendar.getInstance().time)
        if (currentDate >= startDateApi) {
            ivArrowPrev.isEnabled = false
            ivArrowPrev.isClickable = false
            if (AppUtils.getDefaultLang().equals("en"))
                ivArrowPrev.setBackgroundResource(R.drawable.ic_arrow_left_gray)
            else
                ivArrowPrev.setBackgroundResource(R.drawable.ic_arrow_right_gray)
        } else {
            ivArrowPrev.isEnabled = true
            ivArrowPrev.isClickable = true
            if (AppUtils.getDefaultLang().equals("en"))
                ivArrowPrev.setBackgroundResource(R.drawable.ic_arrow_left_green)
            else
                ivArrowPrev.setBackgroundResource(R.drawable.ic_arrow_right_green)
        }
        setDate(dateValue)
        if (!isComingFromNotification)
            validateAppointmentsRequest()
    }

    private fun setDate(dateValue: String?/*, endDate: String?*/) {
        tvWeekDate.text = AppUtils.changeDateIntoDayName(activity!!, dateValue!!)
    }


    private fun setListeners() {
        ivPrint.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        rlSchedule.setOnClickListener(this)
        rlCancel.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        ivPatientCross.setOnClickListener(this)
        rlAddPrescription.setOnClickListener(this)
        rlCompleteAppointment.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        rlChat.setOnClickListener(this)
        rlDaysDropDown.setOnClickListener(this)
        ivReport.setOnClickListener(this)

        ivArrowPrev.setOnClickListener(this)
        ivArrowNext.setOnClickListener(this)

        swipeToRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeToRefresh.setOnRefreshListener {
            AppUtils.touchScreenDisable(context!!)
            isSwipeRefresh = true
            validateAppointmentsRequest()
            swipeToRefresh.isRefreshing = false
        }


    }

    private fun somethingWentWrong(
        list: View,
        tv: TextView,
        isWentWrong: Boolean,
        errorMessage: String
    ) {
        if (isWentWrong) {
            list.visibility = View.GONE
            tv.visibility = View.VISIBLE
            tv.text = errorMessage
        } else {
            list.visibility = View.VISIBLE
            tv.visibility = View.GONE
        }
    }

    private fun validateAppointmentsRequest() {


        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            somethingWentWrong(
                rvAppointmentTime,
                tvSomethingWrong,
                true,
                resources.getString(R.string.txt_alert_internet_connection)
            )
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        patientLayoutVisibilityGone()
        rvAppointmentTime.visibility = View.GONE
        tvSomethingWrong.visibility = View.GONE
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("start_date", startDateApi)
        paramObject.addProperty("end_date", endDateApi)
        callGetWeeklyAppointments(paramObject)

    }

    private fun callGetWeeklyAppointments(jsonObject: JsonObject) {
        try {
            if (!isSwipeRefresh)
                Loader.showLoader(activity!!)
        } catch (e: Exception) {
        }
        isSwipeRefresh = false
        ApiController.getInstance(activity!!, this).weeklyAppointments(jsonObject)
    }

    private fun callGetPatientData(appointmentId: Int, date: String, time: String) {

        rlMainParentPatientChild.visibility = View.GONE
        tvSomethingWrongPatient.visibility = View.GONE
        AppUtils.showProgressBar(progressBar, activity!!)

        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("date", date)
        paramObject.addProperty("time", time)
        paramObject.addProperty("appointment_id", appointmentId.toString())
        ApiController.getInstance(activity!!, this).getPatientData(paramObject)

    }

    private fun callGetPrescriptionsDropDownArray() {
        ApiController.getInstance(activity!!, this).getPrescriptionsArray()
    }

    private fun validateCancelAppointment() {

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        Loader.showLoader(activity!!)
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("appointment_id", patientDataObj?.getAppointmentId())
        ApiController.getInstance(activity!!, this).cancelAppointment(paramObject)
    }

    private fun validateCompleteAppointment() {

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        Loader.showLoader(activity!!)
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("appointment_id", patientDataObj?.getAppointmentId())
        paramObject.addProperty("patient_id", patientDataObj?.getPatientId())
        ApiController.getInstance(activity!!, this).completeAppointment(paramObject)
    }

    // API Error Response Handling
    override fun onError(requestType: Int, errorMessage: String) {
        try {

            Loader.hideLoader()
            AppUtils.touchScreenEnable(activity!!)
            when (requestType) {
                APIsTag.prescriptionsArrayData -> {
                    if (!Constants.isDeviceRegisterCalled) {
                        Constants.isDeviceRegisterCalled = true
                        deviceRegisterToken()
                    }
                }
                APIsTag.patientData -> {
                    rlMainParentPatientChild.visibility = View.GONE
                    AppUtils.hideProgressBar(progressBar, activity!!)
                    tvSomethingWrongPatient.visibility = View.VISIBLE
                    tvSomethingWrongPatient.text = errorMessage
                }
                APIsTag.updatePrescription ->
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )


                APIsTag.completeAppointment ->
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )

                APIsTag.cancelAppointment ->
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )

                APIsTag.reportPatient ->
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        errorMessage
                    )

                APIsTag.weeklyAppointments -> {
                    callGetPrescriptionsDropDownArray()
                    somethingWentWrong(
                        rvAppointmentTime,
                        tvSomethingWrong,
                        true,
                        errorMessage
                    )
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
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
            AppUtils.touchScreenEnable(activity!!)
            Loader.hideLoader()
            when (requestType) {
                APIsTag.prescriptionsArrayData -> {
                    if (!Constants.isDeviceRegisterCalled) {
                        Constants.isDeviceRegisterCalled = true
                        deviceRegisterToken()
                    }
                    medTypeArray.clear()
                    medFrequencyArray.clear()
                    medDoseArray.clear()
                    medDayArray.clear()
                    var prescriptionDialogData: PrescriptionDialogData? =
                        CommonFunctions.convertDataType(
                            objectData,
                            PrescriptionDialogData::class.java
                        )
                    medTypeArray = prescriptionDialogData?.getMedicineTypeArray()!!
                    medFrequencyArray = prescriptionDialogData?.getMedicineFrequencyArray()!!
                    medDoseArray = prescriptionDialogData?.getMedicineDoseArray()!!
                    medDayArray = prescriptionDialogData?.getMedicineDayArray()!!
                }
                APIsTag.patientData -> {
                    var patientData: PatientDataResponse? =
                        CommonFunctions.convertDataType(objectData, PatientDataResponse::class.java)
                    setPatientData(patientData)
                }
                APIsTag.updatePrescription -> {
                    var prescription: UpdatePrescriptionDataResponse? =
                        CommonFunctions.convertDataType(
                            objectData,
                            UpdatePrescriptionDataResponse::class.java
                        )
                    patientDataObj?.setPrescriptionPdfUrl(prescription?.getPrescriptionPdfUrl()!!)
                    finalWeeklyAppointments[appointmentClickedMainPos].getTimeArray()[appointmentClickedChildPos].setIsAddPrescription(
                        1
                    )
                    disableRescheduleAndCancel()
                    patientLayoutVisibilityGone()
                    CustomAlert.showDropDownNotificationSuccess(
                        activity!!,
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )
                }
                APIsTag.cancelAppointment -> {
                    CustomAlert.showDropDownNotificationSuccess(
                        activity!!,
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )
                    validateAppointmentsRequest()
                }
                APIsTag.reportPatient -> {
                    CustomAlert.showDropDownNotificationSuccess(
                        activity!!,
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )
                    validateAppointmentsRequest()
                }
                APIsTag.weeklyAppointments -> {

                    callGetPrescriptionsDropDownArray()
                    var appointmentData: AppointmentData? =
                        CommonFunctions.convertDataType(objectData, AppointmentData::class.java)
                    adMobRightUnitId = appointmentData?.getRightSideAd()!!
                    adMobBottomUnitId = appointmentData.getBottomSideAd()
                    loadAds()
                    setAppointmentData(appointmentData)
                }

                APIsTag.completeAppointment -> {
                    finalWeeklyAppointments[appointmentClickedMainPos].getTimeArray()[appointmentClickedChildPos].setIsAddPrescription(
                        1
                    )

                    disableRescheduleAndCancel()
                    patientLayoutVisibilityGone()
                    disableCompleteAppointment()

                    CustomAlert.showDropDownNotificationSuccess(
                        activity!!,
                        resources.getString(R.string.txt_alert_success_information),
                        objectData.message!!
                    )
                }
            }

        } catch (e: Exception) {

        }
    }

    private fun setPatientData(patientData: PatientDataResponse?) {

        try {
            patientDataObj = patientData
            ivGenderFeMale.visibility = View.GONE
            ivGenderMale.visibility = View.GONE
            if (patientDataObj?.getIsFamilyAppointment() == 0) {
                tvPatientName.text = patientDataObj?.getFullName()
                tvDateAndAge.text =
                    patientDataObj?.getDob() + " (" + patientDataObj?.getAge() + " " + resources.getString(
                        R.string.txt_age
                    ) + ")"
                AppUtils.loadImageWithGlide(
                    activity!!,
                    patientDataObj?.getPhoto()!!,
                    ivPatientProfile,
                    R.drawable.profile_placeholder
                )

                if (patientDataObj?.getGender().equals("Male", true))
                    ivGenderMale.visibility = View.VISIBLE
                else
                    ivGenderFeMale.visibility = View.VISIBLE
            } else {
                var familyData = patientDataObj?.getFamilyData()
                tvPatientName.text = familyData?.getFullName()
                tvDateAndAge.text =
                    "" + familyData?.getAge() + " " + resources.getString(R.string.txt_age)
                AppUtils.loadImageWithGlide(
                    activity!!,
                    familyData?.getPhoto()!!,
                    ivPatientProfile,
                    R.drawable.profile_placeholder
                )
                if (familyData?.getGender().equals("Male", true))
                    ivGenderMale.visibility = View.VISIBLE
                else
                    ivGenderFeMale.visibility = View.VISIBLE
            }

            tvDetailDesc.text = patientDataObj?.getDetail()
            if (patientDataObj?.getIsAddPrescription() == 1 || appointmentObj?.getIsExpired() == 1) {
                disableRescheduleAndCancel()
            } else {
                enableRescheduleAndCancel()
            }


            if (patientDataObj?.getIsAddPrescription() == 1)
                disableCompleteAppointment()
            else
                enableCompleteAppointment()


            if (patientDataObj?.getPatientPrescriptionObject()?.getRecheckDays()!! > 0)
                tvDays.text =
                    patientDataObj?.getPatientPrescriptionObject()?.getRecheckDays()!!.toString()

            if (patientDataObj?.getPatientPrescriptionObject()?.getPrescriptionArray()!!.size > 0) {
                rlAddPrescription.visibility = View.GONE
                rlCompleteAppointment.visibility = View.GONE
                llMedication.visibility = View.VISIBLE
                rlDaysParent.visibility = View.VISIBLE
                btnSave.visibility = View.VISIBLE
                var adapter = PatientPrescriptionAdapter(
                    activity!!,
                    patientDataObj?.getPatientPrescriptionObject()?.getPrescriptionArray()!!
                )
                lvPrescription?.adapter = adapter
                AppUtils.setListViewHeightBasedOnChildren(lvPrescription)
            } else {
                rlAddPrescription.visibility = View.VISIBLE
                rlCompleteAppointment.visibility = View.VISIBLE
                llMedication.visibility = View.GONE
                rlDaysParent.visibility = View.GONE
                btnSave.visibility = View.GONE
            }

            if (appointmentObj?.getAppointmentType().equals("online", true)) {
                enableChat()
                // enableCompleteAppointment()
            } else {
                disableChat()
                //  disableCompleteAppointment()
            }

            if (appointmentObj?.getIsAddPrescription() == 1) {
                rlAddPrescription.visibility = View.GONE
                rlCompleteAppointment.visibility = View.GONE
                llMedication.visibility = View.GONE
                rlDaysParent.visibility = View.GONE
                btnSave.visibility = View.GONE
            }

            rlMainParentPatientChild.visibility = View.VISIBLE
            AppUtils.hideProgressBar(progressBar, activity!!)
            tvSomethingWrongPatient.visibility = View.GONE

        } catch (e: Exception) {
            rlMainParentPatientChild.visibility = View.GONE
            AppUtils.hideProgressBar(progressBar, activity!!)
            tvSomethingWrongPatient.visibility = View.VISIBLE
            tvSomethingWrongPatient.text = resources.getString(R.string.txt_something_went_wrong)
        }

    }

    private fun setAppointmentData(appointmentData: AppointmentData?) {
        try {
            startIndex = 0

            countValue = if (!isPhone) 5 else 3

            finalWeeklyAppointments.clear()
            weeklyAppointments.clear()
            if (appointmentData?.getWeeklyAppointments() != null)
                weeklyAppointments.addAll(appointmentData.getWeeklyAppointments()!!)

            if (!isPhone)
                when {
                    weeklyAppointments?.size!! >= 6 -> setAppointmentDataRecursively()

                    weeklyAppointments?.size!! > 0 -> {
                        var objFinalWeeklyAppointments = FinalWeeklyAppointments()
                        for (i in 0 until weeklyAppointments?.size!!) {
                            if (weeklyAppointments[i].getIsAddPrescription() == 1)
                                weeklyAppointments[i].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                            objFinalWeeklyAppointments.getTimeArray().add(weeklyAppointments[i])
                        }

                        finalWeeklyAppointments.add(objFinalWeeklyAppointments)
                        setAdapter()
                    }
                    else -> setAdapter()
                }
            else
                when {
                    weeklyAppointments?.size!! >= 4 -> setAppointmentDataRecursively()

                    weeklyAppointments?.size!! > 0 -> {
                        var objFinalWeeklyAppointments = FinalWeeklyAppointments()
                        for (i in 0 until weeklyAppointments?.size!!) {
                            if (weeklyAppointments[i].getIsAddPrescription() == 1)
                                weeklyAppointments[i].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                            objFinalWeeklyAppointments.getTimeArray().add(weeklyAppointments[i])
                        }

                        finalWeeklyAppointments.add(objFinalWeeklyAppointments)
                        setAdapter()
                    }
                    else -> setAdapter()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setAppointmentDataRecursively() {

        var objFinalWeeklyAppointments = FinalWeeklyAppointments()

        if (!isPhone)
            for (i in startIndex until weeklyAppointments?.size!!) {
                if (weeklyAppointments[startIndex].getIsAddPrescription() == 1)
                    weeklyAppointments[startIndex].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                objFinalWeeklyAppointments.getTimeArray().add(weeklyAppointments[startIndex])
                startIndex++
                if (startIndex == countValue) {
                    countValue += 5
                    break
                }
            }
        else
            for (i in startIndex until weeklyAppointments?.size!!) {
                if (weeklyAppointments[startIndex].getIsAddPrescription() == 1)
                    weeklyAppointments[startIndex].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                objFinalWeeklyAppointments.getTimeArray().add(weeklyAppointments[startIndex])
                startIndex++
                if (startIndex == countValue) {
                    countValue += 3
                    break
                }
            }



        finalWeeklyAppointments.add(objFinalWeeklyAppointments)
        if (startIndex < weeklyAppointments?.size!!)
            setAppointmentDataRecursively()
        else
            setAdapter()
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.rlChat -> {
                Constants.fragmentDashboardRef = this
                dialogChat = DialogChat().newInstance(
                    isComingFromNotification,
                    appointmentId,
                    patientDataObj!!,
                    appointmentObj!!
                )
                fragmentManager?.let { dialogChat!!.show(it, "") }
            }

            R.id.ivShare ->
                checkPermissionForDownloadPDF()

            R.id.ivPrint ->
                checkPermissionForDownloadPDF()

            R.id.ivReport -> {


                if (appointmentObj!!.getIsReported() == 0) {
                    dialogReportPatient = DialogReportPatient(
                        patientDataObj!!, this
                    )
                    fragmentManager?.let { dialogReportPatient!!.show(it, "") }
                } else {
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        "You have already reported this patient."
                    )
                }
            }

            R.id.rlDaysDropDown
            -> {
                daysDialog = DialogDays(this)
                fragmentManager?.let { daysDialog!!.show(it, "") }
            }

            R.id.btnSave -> {
                updatePrescriptionRequest()
            }

            R.id.ivPatientCross -> {
                patientLayoutVisibilityGone()

            }

            R.id.ivArrowPrev -> {
                patientLayoutVisibilityGone()
                getAndSetWeekDate("previous")
            }

            R.id.ivArrowNext -> {
                patientLayoutVisibilityGone()
                getAndSetWeekDate("next")
            }

            R.id.rlAddPrescription -> {

                if (appointmentObj!!.getIsExpired() == 0) {
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        resources.getString(R.string.txt_chat_not_started_prescription_add)
                    )
                    return
                }


                var pref =
                    context!!.getSharedPreferences("mualijApplicationPro", Context.MODE_PRIVATE)
                val prefsEditor = pref?.edit()
                val gson = Gson()
                val json =
                    gson.toJson(
                        patientDataObj?.getPatientPrescriptionObject()?.getPrescriptionArray()
                    )
                prefsEditor?.putString("prescriptionArray", json)
                prefsEditor?.commit()
                dialogPrescription =
                    DialogPrescription(
                        patientDataObj!!,
                        this,
                        medTypeArray,
                        medFrequencyArray,
                        medDoseArray,
                        medDayArray
                    )
                fragmentManager?.let { dialogPrescription!!.show(it, "") }
            }


            R.id.rlCompleteAppointment -> {

                if (appointmentObj!!.getIsExpired() == 0) {
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        resources.getString(R.string.txt_chat_not_started_complete_add)
                    )
                    return
                }


                var appointmentTimeDate =
                    AppUtils.changeDateFormatTo24Hour(appointmentObj!!.getAppointmentDate() + " " + appointmentObj!!.getAppointmentTime())
                var toComplete = AppUtils.compareOnlineAppointmentDate(appointmentTimeDate)

                if (toComplete == null)
                    AppUtils.completeAppointmentConfirmationAlert(
                        activity!!,
                        this
                    ) else
                    CustomAlert.showDropDownNotificationError(
                        activity!!,
                        resources.getString(R.string.txt_alert_information),
                        toComplete
                    )
            }


            R.id.ivEdit -> {
                var pref =
                    context!!.getSharedPreferences("mualijApplicationPro", Context.MODE_PRIVATE)
                val prefsEditor = pref?.edit()
                val gson = Gson()
                val json =
                    gson.toJson(
                        patientDataObj?.getPatientPrescriptionObject()?.getPrescriptionArray()
                    )
                prefsEditor?.putString("prescriptionArray", json)
                prefsEditor?.commit()

                dialogPrescription =
                    DialogPrescription(
                        patientDataObj!!,
                        this,
                        medTypeArray,
                        medFrequencyArray,
                        medDoseArray,
                        medDayArray
                    )
                fragmentManager?.let { dialogPrescription!!.show(it, "") }
            }

            R.id.rlCancel -> {
                AppUtils.cancelAppointmentConfirmationAlert(
                    activity!!,
                    this
                )
            }

            R.id.rlSchedule -> {
                dialogReschedule = DialogReschedule(
                    selectedAppointmentDate!!,
                    patientDataObj?.getAppointmentId(),
                    this
                )
                fragmentManager?.let { dialogReschedule!!.show(it, "") }
            }

        }
    }

    fun onAppointmentItemClicked(mainPosition: Int, timeArrayPosition: Int) {
        appointmentClickedMainPos = mainPosition
        appointmentClickedChildPos = timeArrayPosition

        appointmentObj = finalWeeklyAppointments[mainPosition].getTimeArray()[timeArrayPosition]



        for (i in 0 until finalWeeklyAppointments.size) {
            for (f in 0 until finalWeeklyAppointments[i].getTimeArray().size) {
                finalWeeklyAppointments[i].getTimeArray()[f].setTimeColor(resources.getColor(R.color.colorPrimary))
                if (finalWeeklyAppointments[i].getTimeArray()[f].getIsAddPrescription() == 1)
                    finalWeeklyAppointments[i].getTimeArray()[f].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                else
                    finalWeeklyAppointments[i].getTimeArray()[f].setBgColor(R.drawable.bg_white_with_green_border)
            }
        }
        appointmentObj?.setTimeColor(resources.getColor(R.color.white))
        appointmentObj?.setBgColor(R.drawable.bg_green_without_border)
        adapterTiming?.notifyDataSetChanged()

        if (!rlMainParentPatient.isVisible) {
            rlMainParentPatient.visibility = View.VISIBLE

            val animation: Animation = if (AppUtils.getDefaultLang() == "en")
                AnimationUtils.loadAnimation(activity!!, R.anim.slide_in_right)
            else
                AnimationUtils.loadAnimation(activity!!, R.anim.slide_in_left)
            rlMainParentPatient.animation = animation

            if (isPhone)
                (activity as MainActivity).hideShowToolbar(View.GONE)
        }

        tvDays.text = "0"
        selectedAppointmentDate =
            finalWeeklyAppointments[mainPosition].getTimeArray()[timeArrayPosition].getAppointmentDate()!!
        callGetPatientData(
            finalWeeklyAppointments[mainPosition].getTimeArray()[timeArrayPosition].getAppointmentId()!!,
            finalWeeklyAppointments[mainPosition].getTimeArray()[timeArrayPosition].getAppointmentDate()!!,
            finalWeeklyAppointments[mainPosition].getTimeArray()[timeArrayPosition].getAppointmentTime()!!
        )


        if (appointmentObj?.getIsReported() == 1)
            ivReport.visibility = View.GONE
        else {
            ivReport.visibility = View.VISIBLE
        }

    }


    private fun updatePrescriptionRequest() {

        if (!AppUtils.isOnline(activity!!)) {
            AppUtils.touchScreenEnable(activity!!)
            CustomAlert.showDropDownNotificationError(
                activity!!,
                resources.getString(R.string.txt_alert_information),
                resources.getString(R.string.txt_alert_internet_connection)
            )
            return
        }

        var prescriptionJsonArray = JSONArray()
        try {
            if (patientDataObj?.getPatientPrescriptionObject()?.getPrescriptionArray()?.size != 0) {
                for (prescriptionModel in patientDataObj?.getPatientPrescriptionObject()
                    ?.getPrescriptionArray()!!) {

                    val prescriptionObject = JSONObject()
                    prescriptionObject.put("id", prescriptionModel.getId())
                    prescriptionObject.put("medicine_name", prescriptionModel.getMedicine())
                    prescriptionObject.put("medicine_type_id", prescriptionModel.getMedTypeId())
                    prescriptionObject.put(
                        "medicine_frequency_id",
                        prescriptionModel.getMedFrequencyId()
                    )
                    prescriptionObject.put("medicine_dosage_id", prescriptionModel.getMedDoseId())
                    prescriptionObject.put("medicine_day_id", prescriptionModel.getMedDayID())
                    prescriptionObject.put("medicine_before_after", "")
                    prescriptionJsonArray.put(prescriptionObject)

                }
            }
        } catch (e: Exception) {

        }

        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("prescriptions", prescriptionJsonArray.toString())
        paramObject.addProperty("patient_id", patientDataObj?.getPatientId())
        paramObject.addProperty("problem", "")
        paramObject.addProperty("appointment_id", patientDataObj?.getAppointmentId())
        paramObject.addProperty("recheck_days", tvDays.text.toString())

        Loader.showLoader(activity!!)
        ApiController.getInstance(activity!!, this).updatePrescription(paramObject)

    }

    override fun onCallBack(
        type: String?,
        value: String?,
        prescriptionArray: ArrayList<PatientPrescriptionArray>
    ) {
        if (type == Constants.dashboardDays) {
            daysDialog?.dismiss()
            tvDays.text = value
        } else if (type == Constants.dashboardCancelAppointment)
            validateCancelAppointment()
        else if (type == Constants.dashboardUpdatePrescription) {
            patientDataObj?.getPatientPrescriptionObject()?.setPrescriptionArray(prescriptionArray)

            if (prescriptionArray.size > 0) {
                rlAddPrescription.visibility = View.GONE
                rlCompleteAppointment.visibility = View.GONE
                llMedication.visibility = View.VISIBLE
                rlDaysParent.visibility = View.VISIBLE
                btnSave.visibility = View.VISIBLE
                var adapter = PatientPrescriptionAdapter(
                    activity!!,
                    prescriptionArray
                )
                lvPrescription?.adapter = adapter
                AppUtils.setListViewHeightBasedOnChildren(lvPrescription)
            } else {
                rlAddPrescription.visibility = View.VISIBLE
                rlCompleteAppointment.visibility = View.VISIBLE
                llMedication.visibility = View.GONE
                rlDaysParent.visibility = View.GONE
                btnSave.visibility = View.GONE
            }
        } else if (type == Constants.dashboardRescheduleAppointment)
            validateAppointmentsRequest()
        else if (type == Constants.dashboardReportPatient) {
            finalWeeklyAppointments[appointmentClickedMainPos].getTimeArray()[appointmentClickedChildPos].setIsReported(
                1
            )
            patientLayoutVisibilityGone()
        } else if (type == Constants.dashboardCompleteAppointment)
            validateCompleteAppointment()

    }

    private fun disableRescheduleAndCancel() {
        ivSchedule.setBackgroundResource(R.drawable.ic_reschedule_gray)
        tvScheduleTitle.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
        ivCancel.setBackgroundResource(R.drawable.ic_cancel_gray)
        tvCancelTitle.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
        rlCancel.isEnabled = false
        rlCancel.isClickable = false
        rlSchedule.isEnabled = false
        rlSchedule.isClickable = false
    }

    private fun disableChat() {
        ivChat.setBackgroundResource(R.drawable.ic_chat_gray)
        tvChatTitle.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
        rlChat.isEnabled = false
        rlChat.isClickable = false

    }

    private fun enableChat() {
        ivChat.setBackgroundResource(R.drawable.ic_chat)
        tvChatTitle.setTextColor(resources.getColor(R.color.colorPrimary))
        rlChat.isEnabled = true
        rlChat.isClickable = true
    }

    private fun disableCompleteAppointment() {

        rlCompleteAppointment.isEnabled = false
        rlCompleteAppointment.isClickable = false

        rlCompleteAppointment.visibility = View.GONE
    }

    private fun enableCompleteAppointment() {

        rlCompleteAppointment.isEnabled = true
        rlCompleteAppointment.isClickable = true

        rlCompleteAppointment.visibility = View.VISIBLE
    }

    private fun enableRescheduleAndCancel() {
        ivSchedule.setBackgroundResource(R.drawable.ic_reschedule_green)
        tvScheduleTitle.setTextColor(resources.getColor(R.color.colorPrimary))
        ivCancel.setBackgroundResource(R.drawable.ic_cancel_red)
        tvCancelTitle.setTextColor(resources.getColor(R.color.cancel_red))
        rlCancel.isEnabled = true
        rlCancel.isClickable = true
        rlSchedule.isEnabled = true
        rlSchedule.isClickable = true
    }

    private fun patientLayoutVisibilityGone() {
        if (isPhone)
            (activity as MainActivity).hideShowToolbar(View.VISIBLE)

        for (i in 0 until finalWeeklyAppointments.size)
            for (f in 0 until finalWeeklyAppointments[i].getTimeArray().size) {
                finalWeeklyAppointments[i].getTimeArray()[f].setTimeColor(resources.getColor(R.color.colorPrimary))
                if (finalWeeklyAppointments[i].getTimeArray()[f].getIsAddPrescription() == 1)
                    finalWeeklyAppointments[i].getTimeArray()[f].setBgColor(R.drawable.bg_gray_with_dark_gray_border)
                else
                    finalWeeklyAppointments[i].getTimeArray()[f].setBgColor(R.drawable.bg_white_with_green_border)
            }
        if (adapterTiming != null)
            adapterTiming?.notifyDataSetChanged()

        if (rlMainParentPatient.isVisible) {
            rlMainParentPatient.visibility = View.GONE

            val animation: Animation = if (AppUtils.getDefaultLang() == "en")
                AnimationUtils.loadAnimation(activity!!, R.anim.slide_out_right)
            else
                AnimationUtils.loadAnimation(activity!!, R.anim.slide_out_left)

            rlMainParentPatient.animation = animation
        }
    }

    private fun checkPermissionForDownloadPDF() {
        takeRunTimePermission()
    }

    private fun takeRunTimePermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    if (patientDataObj?.getPrescriptionPdfUrl() == "")
                        CustomAlert.showDropDownNotificationError(
                            activity!!,
                            resources.getString(R.string.txt_alert_information),
                            resources.getString(R.string.txt_prescription_pdf_error)
                        )
                    else
                        DownloadPrescriptionPDF(activity!!).execute(patientDataObj?.getPrescriptionPdfUrl())
                else
                    AppUtils.showPermissionAlertDialog(activity!!)
                return

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mReceiver!!)
    }

    override fun onStop() {
        super.onStop()
        if (mReceiver != null)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mReceiver!!)
    }

    private fun loadRightAdView() {

        try {
            if (rightAdFrame != null) {
                rightAdFrame.visibility = View.GONE

                if (adMobRightUnitId.isNullOrEmpty()) {
                    rlRightAdFrame.visibility = View.GONE
                    return
                }
            }

            rlRightAdFrame.visibility = View.VISIBLE
            val builder = AdLoader.Builder(activity!!, adMobRightUnitId)
            builder.forUnifiedNativeAd { nativeAd ->

                // Set the media view.
                if (adRightView != null) {
                    adRightView?.mediaView = adRightView?.findViewById(R.id.ad_media)
                    adRightView?.mediaView?.removeAllViews()
                    adRightView?.mediaView?.setMediaContent(nativeAd.mediaContent)
                    adRightView?.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    adRightView?.setNativeAd(nativeAd)
                }

                if (rightAdFrame != null) {
                    rightAdFrame.removeAllViews()
                    rightAdFrame.addView(adRightView)
                }
            }

            val videoOptions = VideoOptions.Builder()
                .build()
            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    if (rightAdFrame != null)
                        rightAdFrame.visibility = View.VISIBLE
                    super.onAdLoaded()
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                }
            }).build()
            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {

        }

    }

    private fun loadBottomAdView() {

        try {
            if (bottomAdFrame != null) {
                bottomAdFrame.visibility = View.GONE
                if (adMobBottomUnitId.isNullOrEmpty()) {
                    rlBottomAdd.visibility = View.GONE
                    return
                }
            }


            rlBottomAdd.visibility = View.VISIBLE
            val builder = AdLoader.Builder(activity!!, adMobBottomUnitId)
            builder.forUnifiedNativeAd { nativeAd ->

                // Set the media view.
                if (adBottomView != null) {
                    adBottomView?.mediaView = adBottomView?.findViewById(R.id.adBottom)
                    adBottomView?.mediaView?.removeAllViews()
                    adBottomView?.mediaView?.setMediaContent(nativeAd.mediaContent)
                    adBottomView?.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    adBottomView?.setNativeAd(nativeAd)
                }

                if (bottomAdFrame != null) {
                    bottomAdFrame.removeAllViews()
                    bottomAdFrame.addView(adBottomView)
                }
            }

            val videoOptions = VideoOptions.Builder()
                .build()
            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()
            builder.withNativeAdOptions(adOptions)
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                }

                override fun onAdLoaded() {
                    if (bottomAdFrame != null)
                        bottomAdFrame.visibility = View.VISIBLE
                    super.onAdLoaded()
                }
            }).build()
            adLoader.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {

        }
    }

    private fun registerBroadCastReceiver() {
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getStringExtra("type").equals("refresh_screen", true)) {
                    isComingFromNotification = false
                    getAndSetWeekDate("current")
                } else {
                    val currentDate = AppUtils.baseDateFormatEng(Calendar.getInstance().time)
                    if (currentDate != startDateApi) {
                        patientLayoutVisibilityGone()
                        getAndSetWeekDate("current")
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(mReceiver!!, IntentFilter("dashBoard"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (dialogChat != null)
            dialogChat?.onActivityResult(requestCode, resultCode, data)
    }

    private fun deviceRegisterToken() {

        if (!AppUtils.isOnline(activity!!))
            return

        var token = AppSP.getInstance(activity!!).readString(Constants.FireBaseToken)
        val paramObject = AppUtils.getGenericParams()
        paramObject.addProperty("device_id", AppUtils.getDeviceUniqueId())
        paramObject.addProperty("token", token)
        paramObject.addProperty("device_type", "android")
        paramObject.addProperty("app_mode", "development")
        paramObject.addProperty("user_type", "doctor")
        ApiController.getInstance(activity!!, this).deviceRegister(paramObject)

    }

    data class Clock(var minute: Int = 0, var hour: Int = 12) {
        fun show() = println("hour : $hour, minute : $minute")
    }
}