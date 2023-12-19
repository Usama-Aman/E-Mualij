package com.elementary.mualijpro.utils

import java.text.SimpleDateFormat
import java.util.*

class Calender {

    companion object {

        private var calendar: Calendar? = null
        private val format = SimpleDateFormat(Constants.baseDateFormat)

        fun getCurrentDay(): String {
            this.calendar = Calendar.getInstance()
            return format.format(this.calendar!!.time)
        }

        fun getNextDay(): String {
            this.calendar?.add(Calendar.DATE, 1)
            return format.format(this.calendar!!.time)
        }

        fun getPreviousDay(): String {
            this.calendar?.add(Calendar.DATE, -1)
            return format.format(this.calendar!!.time)
        }

    }

}