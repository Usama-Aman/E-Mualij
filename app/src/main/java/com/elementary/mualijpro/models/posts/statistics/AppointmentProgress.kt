package com.elementary.mualijpro.models.posts.statistics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AppointmentProgress {

    @SerializedName("Jan")
    @Expose
    private var Jan: Int? = 0
    @SerializedName("Feb")
    @Expose
    private var Feb: Int? = 0
    @SerializedName("Mar")
    @Expose
    private var Mar: Int? = 0
    @SerializedName("Apr")
    @Expose
    private var Apr: Int? = 0
    @SerializedName("May")
    @Expose
    private var May: Int? = 0
    @SerializedName("Jun")
    @Expose
    private var Jun: Int? = 0
    @SerializedName("Jul")
    @Expose
    private var Jul: Int? = 0
    @SerializedName("Aug")
    @Expose
    private var Aug: Int? = 0
    @SerializedName("Sep")
    @Expose
    private var Sep: Int? = 0
    @SerializedName("Oct")
    @Expose
    private var Oct: Int? = 0
    @SerializedName("Nov")
    @Expose
    private var Nov: Int? = 0
    @SerializedName("Dec")
    @Expose
    private var Dec: Int? = 0

    fun getJan(): Int? {
        if (Jan == null) return 0
        return Jan
    }

    fun getFeb(): Int? {
        if (Feb == null) return 0
        return Feb
    }

    fun getMar(): Int? {
        if (Mar == null) return 0
        return Mar
    }

    fun getApr(): Int? {
        if (Apr == null) return 0
        return Apr
    }

    fun getMay(): Int? {
        if (May == null) return 0
        return May
    }

    fun getJun(): Int? {
        if (Jun == null) return 0
        return Jun
    }

    fun getJul(): Int? {
        if (Jul == null) return 0
        return Jul
    }

    fun getAug(): Int? {
        if (Aug == null) return 0
        return Aug
    }

    fun getSep(): Int? {
        if (Sep == null) return 0
        return Sep
    }

    fun getOct(): Int? {
        if (Oct == null) return 0
        return Oct
    }

    fun getNov(): Int? {
        if (Nov == null) return 0
        return Nov
    }

    fun getDec(): Int? {
        if (Dec == null) return 0
        return Dec
    }

}