package com.elementary.mualijpro.models.posts.reschedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class AvailableScheduleDataResponse {

    @SerializedName("available_schedule_list")
    @Expose
    private var availableScheduleList: ArrayList<AvailableScheduleList>? = null

    fun getAvailableScheduleList(): ArrayList<AvailableScheduleList>? {
        return availableScheduleList
    }

    fun setAvailableScheduleList(availableScheduleList: ArrayList<AvailableScheduleList>?) {
        this.availableScheduleList = availableScheduleList
    }

}