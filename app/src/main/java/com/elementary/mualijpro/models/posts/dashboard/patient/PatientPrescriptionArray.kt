package com.elementary.mualijpro.models.posts.dashboard.patient

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PatientPrescriptionArray {

    @SerializedName("id")
    @Expose
    private var id: Int? = 0
    @SerializedName("pmed_name")
    @Expose
    private var medName: String? = ""
    @SerializedName("medicine_day_id")
    @Expose
    private var medDayId: Int? = 0
    @SerializedName("medicine_type_id")
    @Expose
    private var medTypeId: Int? = 0
    @SerializedName("medicine_dosage_id")
    @Expose
    private var medDosageId: Int? = 0
    @SerializedName("medicine_frequency_id")
    @Expose
    private var medFrequencyId: Int? = 0
    @SerializedName("prescription_id")
    @Expose
    private var prescriptionId: Int? = 0
    @SerializedName("medicine_day")
    @Expose
    private var medDay: String? = ""
    @SerializedName("medicine_type")
    @Expose
    private var medType: String? = ""
    @SerializedName("medicine_frequency")
    @Expose
    private var medFrequency: String? = ""
    @SerializedName("medicine_dosage")
    @Expose
    private var medDosage: String? = ""

    private var isAllValuesSet: Boolean? = true

    fun getId(): Int? {
        if (id == null) return 0
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getMedicine(): String? {
        if (medName == null) return ""
        return medName
    }

    fun setMedicine(medName: String?) {
        this.medName = medName
    }

    fun getMedDayID(): Int? {
        if (medDayId == null) return 0
        return medDayId
    }

    fun setMedDayID(medDayId: Int?) {
        this.medDayId = medDayId
    }

    fun getMedTypeId(): Int? {
        if (medTypeId == null) return 0
        return medTypeId
    }

    fun setMedTypeId(medTypeId: Int?) {
        this.medTypeId = medTypeId
    }

    fun getMedDoseId(): Int? {
        if (medDosageId == null) return 0
        return medDosageId
    }

    fun setMedDoseId(medDosageId: Int?) {
        this.medDosageId = medDosageId
    }

    fun getMedFrequencyId(): Int? {
        if (medFrequencyId == null) return 0
        return medFrequencyId
    }

    fun setMedFrequencyId(medFrequencyId: Int?) {
        this.medFrequencyId = medFrequencyId
    }

    fun getPrescriptionId(): Int? {
        if (prescriptionId == null) return 0
        return prescriptionId
    }

    fun setPrescriptionId(prescriptionId: Int?) {
        this.prescriptionId = prescriptionId
    }

    fun getMedDay(): String? {
        if (medDay == null) return ""
        return medDay
    }

    fun setMedDay(medDay: String) {
        this.medDay = medDay
    }

    fun getMedType(): String? {
        if (medType == null) return ""
        return medType
    }

    fun setMedType(medType: String) {
        this.medType = medType
    }

    fun getMedFrequency(): String? {
        if (medFrequency == null) return ""
        return medFrequency
    }

    fun setMedFrequency(medFrequency: String) {
        this.medFrequency = medFrequency
    }

    fun getMedDose(): String? {
        if (medDosage == null) return ""
        return medDosage
    }

    fun setMedDose(medDosage: String) {
        this.medDosage = medDosage
    }


    fun getIsAllValueSet(): Boolean? {
        if (isAllValuesSet == null) return false
        return isAllValuesSet
    }

    fun setIsAllValueSet(isAllValuesSet: Boolean) {
        this.isAllValuesSet = isAllValuesSet
    }

}