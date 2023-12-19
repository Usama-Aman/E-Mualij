package com.elementary.mualijpro.models.posts.settings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeeklyScheduleResponse {

    @SerializedName("id")
    @Expose
    private var id: Int? = 0
    @SerializedName("schedule_weekday")
    @Expose
    private var scheduleWeekDay: String? = ""
    @SerializedName("schedule_start")
    @Expose
    private var scheduleStart: String? = ""
    @SerializedName("schedule_end")
    @Expose
    private var scheduleEnd: String? = ""
    @SerializedName("schedule_is_off")
    @Expose
    private var scheduleIsOff: Int? = 0
    @SerializedName("schedule_time_slot")
    @Expose
    private var scheduleTimeSlot: String? = ""
    @SerializedName("schedule_time_gap")
    @Expose
    private var scheduleTimeGap: String? = ""
    @SerializedName("doctor_id")
    @Expose
    private var doctorId: String? = ""


    fun getId(): Int? {
        if (id == null) return 0
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getDay(): String? {
        if (scheduleWeekDay == null) return ""
        return scheduleWeekDay
    }

    fun setDay(scheduleWeekDay: String) {
        this.scheduleWeekDay = scheduleWeekDay
    }

    fun getStartTime(): String? {
        if (scheduleStart == null) return ""
        return scheduleStart
    }

    fun setStartTime(scheduleStart: String?) {
        this.scheduleStart = scheduleStart
    }

    fun getEndTime(): String? {
        if (scheduleEnd == null) return ""
        return scheduleEnd
    }

    fun setEndTime(scheduleEnd: String?) {
        this.scheduleEnd = scheduleEnd
    }

    fun getIsOn(): Boolean {
        return scheduleIsOff != 0
    }

    fun setIsOn(scheduleIsOff: Int) {
        this.scheduleIsOff = scheduleIsOff
    }

    fun getTimePerSection(): String? {
        if (scheduleTimeSlot == null) return ""
        return scheduleTimeSlot
    }

    fun setTimePerSection(scheduleTimeSlot: String) {
        this.scheduleTimeSlot = scheduleTimeSlot
    }

    fun getLapPerSection(): String? {
        if (scheduleTimeGap == null) return ""
        return scheduleTimeGap
    }

    fun setLapPerSection(scheduleTimeGap: String) {
        this.scheduleTimeGap = scheduleTimeGap
    }


    fun getDoctorId(): String? {
        if (doctorId == null) return ""
        return doctorId
    }

    fun setDoctorId(doctorId: String) {
        this.doctorId = doctorId
    }


}