package com.elementary.mualijpro.models.posts.dashboard.appointments

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ScheduleModel(
    @SerializedName("date")
    @Expose
    var dt: String? = null,
    @SerializedName("schedule_time_slot")
    @Expose
    var scheduleTimeSlot: String = "",
    @SerializedName("is_available")
    @Expose
    var is_available: Int? = null,
    @SerializedName("next_available_date")
    @Expose
    var next_available_date: String? = "",
    @SerializedName("next_date")
    @Expose
    var next_date: String? = ""
)
