package com.elementary.mualijpro.models.posts.reschedule

import com.elementary.mualijpro.R
import com.elementary.mualijpro.MualijProApplication
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Suppress("DEPRECATION")
class AvailableScheduleList {
    @SerializedName("appointment_time")
    @Expose
    private var appointmentTime: String? = ""

    // These below two are local variables, its not related to server
    private var timeColor: Int? = MualijProApplication.getAppContext()?.resources?.getColor(R.color.colorPrimary)
    private var bgColor: Int? = R.drawable.bg_white_with_green_border

    fun getAppointmentTime(): String? {
        if (appointmentTime == null) return ""
        return appointmentTime
    }

    fun setAppointmentTime(appointmentTime: String) {
        this.appointmentTime = appointmentTime
    }

    fun getTimeColor(): Int? {
        if (timeColor == null) return MualijProApplication.getAppContext()?.resources?.getColor(R.color.colorPrimary)
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


}