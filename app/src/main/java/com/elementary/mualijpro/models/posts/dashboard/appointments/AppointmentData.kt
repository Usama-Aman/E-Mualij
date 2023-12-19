package com.elementary.mualijpro.models.posts.dashboard.appointments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class AppointmentData {

    @SerializedName("weekly_appointments")
    @Expose
    private var weeklyAppointments: ArrayList<WeeklyAppointments>? = null
    @SerializedName("right_side_ad")
    @Expose
    private var rightSideAd: String = ""
    @SerializedName("bottom_side_ad")
    @Expose
    private var bottomSideAd: String = ""

    fun getRightSideAd(): String {
        if (rightSideAd == null) return ""
        return rightSideAd
    }

    fun getBottomSideAd(): String {
        if (bottomSideAd == null) return ""
        return bottomSideAd
    }

    fun getWeeklyAppointments(): ArrayList<WeeklyAppointments>? {
        if (weeklyAppointments == null) return ArrayList()
        return weeklyAppointments
    }

    fun setWeeklyAppointments(weeklyAppointments: ArrayList<WeeklyAppointments>?) {
        this.weeklyAppointments = weeklyAppointments
    }

}