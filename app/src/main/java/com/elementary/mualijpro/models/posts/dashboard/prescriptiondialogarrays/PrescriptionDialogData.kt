package com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class PrescriptionDialogData {

    @SerializedName("medicine_name")
    @Expose
    private var medicineName: String? = ""
    @SerializedName("medicine_type")
    @Expose
    private var medTypeArray: ArrayList<MedicineType>? = ArrayList()
    @SerializedName("medicine_frequency")
    @Expose
    private var medFrequencyArray: ArrayList<MedicineFrequency>? = ArrayList()
    @SerializedName("medicine_dose")
    @Expose
    private var medDoseArray: ArrayList<MedicineDose>? = ArrayList()
    @SerializedName("medicine_day")
    @Expose
    private var medDayArray: ArrayList<MedicineDay>? = ArrayList()

    fun getMedicineName(): String? {
        if (medicineName == null) return ""
        return medicineName
    }

    fun setMedicineName(medicineName: String) {
        this.medicineName = medicineName
    }

    fun getMedicineTypeArray(): ArrayList<MedicineType>? {
        if (medTypeArray == null) return ArrayList()
        return medTypeArray
    }

    fun setMedicineTypeArray(medTypeArray: ArrayList<MedicineType>?) {
        this.medTypeArray = medTypeArray
    }

    fun getMedicineFrequencyArray(): ArrayList<MedicineFrequency>? {
        if (medFrequencyArray == null) return ArrayList()
        return medFrequencyArray
    }

    fun setMedicineFrequencyArray(medFrequencyArray: ArrayList<MedicineFrequency>?) {
        this.medFrequencyArray = medFrequencyArray
    }

    fun getMedicineDoseArray(): ArrayList<MedicineDose>? {
        if (medDoseArray == null) return ArrayList()
        return medDoseArray
    }

    fun setMedicineDoseArray(medDoseArray: ArrayList<MedicineDose>?) {
        this.medDoseArray = medDoseArray
    }

    fun getMedicineDayArray(): ArrayList<MedicineDay>? {
        if (medDayArray == null) return ArrayList()
        return medDayArray
    }

    fun setMedicineDayArray(medDayArray: ArrayList<MedicineDay>?) {
        this.medDayArray = medDayArray
    }


}