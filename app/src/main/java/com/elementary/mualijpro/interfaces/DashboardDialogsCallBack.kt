package com.elementary.mualijpro.interfaces

import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray
import java.util.ArrayList

interface DashboardDialogsCallBack {
    fun onCallBack(type: String?, value: String?, prescriptionArray: ArrayList<PatientPrescriptionArray>)
}