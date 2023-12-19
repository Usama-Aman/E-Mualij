package com.elementary.mualijpro.models.local

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WebResponse<Any> {

    @SerializedName("status")
    @Expose
    var status: Boolean? = false
    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("app_token")
    @Expose
    var appToken: String? = ""
    @SerializedName("data")
    @Expose
    var data: Any? = null
}