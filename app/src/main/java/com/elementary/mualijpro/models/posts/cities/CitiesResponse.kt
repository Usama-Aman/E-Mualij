package com.elementary.mualijpro.models.posts.cities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CitiesResponse {

    @SerializedName("status")
    @Expose
    private var status: Boolean? = false
    @SerializedName("code")
    @Expose
    private var code: Int? = 0
    @SerializedName("message")
    @Expose
    private var message: String? = ""
    @SerializedName("data")
    @Expose
    private var data: ArrayList<Cities> = ArrayList()
    @SerializedName("app_token")
    @Expose
    private var appToken: String? = ""

    fun getAppToken(): String? {
        if (appToken == null) return ""
        return appToken
    }

    fun getStatus(): Boolean? {
        if (status == null) return false
        return status
    }

    fun getCode(): Int? {
        if (code == null) return 0
        return code
    }

    fun getMessage(): String? {
        if (message == null) return ""
        return message
    }

    fun getCities(): ArrayList<Cities>? {
        if (data == null) return ArrayList()
        return data
    }

}