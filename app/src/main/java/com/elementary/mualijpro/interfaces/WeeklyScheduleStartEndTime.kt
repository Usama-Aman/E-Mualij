package com.elementary.mualijpro.interfaces

import android.widget.RelativeLayout
import android.widget.TextView

interface WeeklyScheduleStartEndTime {
    fun onItemClick(startTime: Boolean, indexValue: Int, textView: TextView, rlTime: RelativeLayout, time: String?)
}