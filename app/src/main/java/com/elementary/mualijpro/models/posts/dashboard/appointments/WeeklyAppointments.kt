package com.elementary.mualijpro.models.posts.dashboard.appointments

import com.elementary.mualijpro.R
import com.elementary.mualijpro.MualijProApplication
import com.elementary.mualijpro.models.posts.dashboard.patient.PatientDataResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class WeeklyAppointments {

    @SerializedName("appointment_id")
    @Expose
    private var appointmentId: Int? = 0

    @SerializedName("patient_id")
    @Expose
    private var patientId: Int? = 0

    @SerializedName("appointment_time")
    @Expose
    private var appointmentTime: String? = ""

    @SerializedName("appointment_date")
    @Expose
    private var appointmentDate: String? = ""

    @SerializedName("appointment_type")
    @Expose
    private var appointmentType: String? = ""

    @SerializedName("is_expired")
    @Expose
    private var isExpired: Int? = 0

    @SerializedName("full_name")
    @Expose
    private var fullName: String? = ""

    @SerializedName("short_name")
    @Expose
    private var shortName: String? = ""

    @SerializedName("is_add_prescription")
    @Expose
    private var isAddPrescription: Int? = 0

    @SerializedName("is_reported")
    @Expose
    private var isReported: Int? = 0

    @SerializedName("current_time")
    @Expose
    private var currentTime: String? = ""

    @SerializedName("schedule")
    @Expose
    private var schedule: ArrayList<ScheduleModel> = ArrayList()

    // These below three are local variables, its not related to server
    private var timeColor: Int? =
        MualijProApplication.getAppContext()?.resources?.getColor(R.color.colorPrimary)
    private var bgColor: Int? = R.drawable.bg_white_with_green_border
    private var patientData: PatientDataResponse? = null

    fun getSchedule(): ArrayList<ScheduleModel> {
        if (schedule == null) return return ArrayList()
        return schedule
    }

    fun setSchedule(schedule: ArrayList<ScheduleModel>) {
        this.schedule = schedule
    }

    fun getAppointmentId(): Int? {
        if (appointmentId == null) return 0
        return appointmentId
    }

    fun setAppointmentId(appointmentId: Int?) {
        this.appointmentId = appointmentId
    }

    fun getIsAddPrescription(): Int? {
        if (isAddPrescription == null) return 0
        return isAddPrescription
    }

    fun setIsAddPrescription(isAddPrescription: Int?) {
        this.isAddPrescription = isAddPrescription
    }

    fun getIsExpired(): Int? {
        if (isExpired == null) return 0
        return isExpired
    }

    fun setIsExpired(isExpired: Int?) {
        this.isExpired = isExpired
    }

    fun getPatientId(): Int? {
        if (patientId == null) return 0
        return patientId
    }

    fun setPatientId(patientId: Int?) {
        this.patientId = patientId
    }

    fun getAppointmentTime(): String? {
        if (appointmentTime == null) return ""
        return appointmentTime
    }

    fun setAppointmentTime(appointmentTime: String?) {
        this.appointmentTime = appointmentTime
    }

    fun getCurrentTime(): String? {
        if (currentTime == null) return ""
        return currentTime
    }

    fun setCurrentTime(currentTime: String?) {
        this.currentTime = currentTime
    }

    fun getAppointmentDate(): String? {
        if (appointmentDate == null) return ""
        return appointmentDate
    }

    fun setAppointmentDate(appointmentDate: String?) {
        this.appointmentDate = appointmentDate
    }

    fun getAppointmentType(): String? {
        if (appointmentType == null) return ""
        return appointmentType
    }

    fun setAppointmentType(appointmentType: String?) {
        this.appointmentType = appointmentType
    }

    fun getFullName(): String? {
        if (fullName == null) return ""
        return fullName
    }

    fun setFullName(fullName: String?) {
        this.fullName = fullName
    }

    fun getShortName(): String? {
        if (shortName == null) return ""
        return shortName
    }

    fun setShortName(shortName: String?) {
        this.shortName = shortName
    }


    fun getPatientData(): PatientDataResponse? {
        return patientData
    }

    fun setPatientData(patientData: PatientDataResponse?) {
        this.patientData = patientData
    }

    fun getTimeColor(): Int? {
        if (timeColor == null) return MualijProApplication.getAppContext()
            ?.resources?.getColor(R.color.colorPrimary)
        return timeColor
    }

    fun setTimeColor(timeColor: Int?) {
        this.timeColor = timeColor
    }

    fun getBgColor(): Int? {
        if (bgColor == null) return R.drawable.bg_white_with_green_border
        return bgColor
    }

    fun setBgColor(bgColor: Int?) {
        this.bgColor = bgColor
    }


    fun getIsReported(): Int? {
        if (isReported == null) return 0
        return isReported
    }

    fun setIsReported(isReported: Int?) {
        this.isReported = isReported
    }
}