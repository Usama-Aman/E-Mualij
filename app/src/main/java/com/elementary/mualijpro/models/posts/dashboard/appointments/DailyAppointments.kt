package com.elementary.mualijpro.models.posts.dashboard.appointments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DailyAppointments {

    @SerializedName("appointment_time")
    @Expose
    private var appointmentTime: String? = ""

    @SerializedName("patient_name")
    @Expose
    private var patientName: String? = "NA"

    fun getAppointmentTime(): String? {
        if (appointmentTime == null) return ""
        return appointmentTime
    }

    fun setAppointmentTime(appointmentTime: String?) {
        this.appointmentTime = appointmentTime
    }

    fun getPatientName(): String? {
        if (patientName == null) return "NA"
        return patientName
    }

    fun setPatientName(patientName: String?) {
        this.patientName = patientName
    }

}