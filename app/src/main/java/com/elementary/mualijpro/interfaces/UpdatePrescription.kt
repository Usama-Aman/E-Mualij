package com.elementary.mualijpro.interfaces

import com.elementary.mualijpro.models.posts.dashboard.patient.PatientPrescriptionArray
import java.util.ArrayList

interface UpdatePrescription {
    fun onUpdatePrescription(prescriptionArray: ArrayList<PatientPrescriptionArray>)
}