package com.elementary.mualijpro.models.posts.statistics

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RevenueProgress {

    @SerializedName("Jan")
    @Expose
    private var Jan: String? = "0"
    @SerializedName("Feb")
    @Expose
    private var Feb: String? = "0"
    @SerializedName("Mar")
    @Expose
    private var Mar: String? = "0"
    @SerializedName("Apr")
    @Expose
    private var Apr: String? = "0"
    @SerializedName("May")
    @Expose
    private var May: String? = "0"
    @SerializedName("Jun")
    @Expose
    private var Jun: String? = "0"
    @SerializedName("Jul")
    @Expose
    private var Jul: String? = "0"
    @SerializedName("Aug")
    @Expose
    private var Aug: String? = "0"
    @SerializedName("Sep")
    @Expose
    private var Sep: String? = "0"
    @SerializedName("Oct")
    @Expose
    private var Oct: String? = "0"
    @SerializedName("Nov")
    @Expose
    private var Nov: String? = "0"
    @SerializedName("Dec")
    @Expose
    private var Dec: String? = "0"

    fun getJan(): String? {
        if (Jan == null) return "0"
        return Jan
    }

    fun getFeb(): String? {
        if (Feb == null) return "0"
        return Feb
    }

    fun getMar(): String? {
        if (Mar == null) return "0"
        return Mar
    }

    fun getApr(): String? {
        if (Apr == null) return "0"
        return Apr
    }

    fun getMay(): String? {
        if (May == null) return "0"
        return May
    }

    fun getJun(): String? {
        if (Jun == null) return "0"
        return Jun
    }

    fun getJul(): String? {
        if (Jul == null) return "0"
        return Jul
    }

    fun getAug(): String? {
        if (Aug == null) return "0"
        return Aug
    }

    fun getSep(): String? {
        if (Sep == null) return "0"
        return Sep
    }

    fun getOct(): String? {
        if (Oct == null) return "0"
        return Oct
    }

    fun getNov(): String? {
        if (Nov == null) return "0"
        return Nov
    }

    fun getDec(): String? {
        if (Dec == null) return "0"
        return Dec
    }

}