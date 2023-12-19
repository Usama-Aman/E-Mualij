package com.elementary.mualijpro.models.posts.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class WeeklyDataResponse {

    @SerializedName("weekly_schedule_list")
    @Expose
    private var weekScheduleList: ArrayList<WeeklyScheduleResponse>? = ArrayList()

    fun getWeeklyScheduleArray(): ArrayList<WeeklyScheduleResponse>? {
        if (weekScheduleList == null) return ArrayList()
        return weekScheduleList
    }

    fun setWeeklyScheduleArray(weekScheduleList: ArrayList<WeeklyScheduleResponse>?) {
        this.weekScheduleList = weekScheduleList
    }

}