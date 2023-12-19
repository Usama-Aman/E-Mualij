package com.elementary.mualijpro.models.local.settings

class SettingsStartEndTime {

    private var time: String? = ""
    private var format: String? = ""

    fun getTime(): String? {
        return time
    }

    fun setTime(time: String) {
        this.time = time
    }

    fun getFormat(): String? {
        return format
    }

    fun setFormat(format: String) {
        this.format = format
    }


}