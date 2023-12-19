package com.elementary.mualijpro.models.local.reschedule

import com.elementary.mualijpro.models.posts.reschedule.AvailableScheduleList
import java.util.ArrayList

class FinalRescheduleAvailableList {

    private var timeArray = ArrayList<AvailableScheduleList>()

    fun getTimeArray(): ArrayList<AvailableScheduleList> {
        return timeArray
    }

    fun setTimeArray(timeArray: ArrayList<AvailableScheduleList>) {
        this.timeArray = timeArray
    }
}