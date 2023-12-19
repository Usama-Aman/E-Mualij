package com.elementary.mualijpro.mualij.sockets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WebResponseSocket<Any> {
    @SerializedName("status")
    @Expose
    var status: Int = 0
    @SerializedName("app_token")
    @Expose
    var appToken: String? = ""
    @SerializedName("message")
    @Expose
    var message: String? = null
    var data: Any? = null
}
