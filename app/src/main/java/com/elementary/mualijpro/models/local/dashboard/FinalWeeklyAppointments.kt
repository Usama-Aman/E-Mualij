package com.elementary.mualijpro.models.local.dashboard

import com.elementary.mualijpro.models.posts.dashboard.appointments.WeeklyAppointments
import java.util.ArrayList

class FinalWeeklyAppointments {

    private var timeArray = ArrayList<WeeklyAppointments>()

    fun getTimeArray(): ArrayList<WeeklyAppointments> {
        return timeArray
    }

    fun setTimeArray(timeArray: ArrayList<WeeklyAppointments>) {
        this.timeArray = timeArray
    }
}