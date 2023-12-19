package com.elementary.mualijpro.models.posts.dashboard.patient

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class PatientPrescriptionObject {

    @SerializedName("recheck_days")
    @Expose
    private var recheckDays: Int? = 0

    @SerializedName("prescription_medicines")
    @Expose
    private var prescriptionMedicines: ArrayList<PatientPrescriptionArray>? = null

    fun getPrescriptionArray(): ArrayList<PatientPrescriptionArray>? {
        if (prescriptionMedicines == null) return ArrayList()
        return prescriptionMedicines
    }

    fun setPrescriptionArray(prescriptionMedicines: ArrayList<PatientPrescriptionArray>?) {
        this.prescriptionMedicines = prescriptionMedicines
    }

    fun getRecheckDays(): Int? {
        if (recheckDays == null) return 0
        return recheckDays
    }

    fun setRecheckDays(recheckDays: Int?) {
        this.recheckDays = recheckDays
    }

}