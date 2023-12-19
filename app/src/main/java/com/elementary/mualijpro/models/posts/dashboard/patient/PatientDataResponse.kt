package com.elementary.mualijpro.models.posts.dashboard.patient

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PatientDataResponse {

    @SerializedName("full_name")
    @Expose
    private var fullName: String? = ""

    @SerializedName("photo")
    @Expose
    private var photo: String? = ""

    @SerializedName("format_date_of_birth")
    @Expose
    private var dob: String? = ""

    @SerializedName("comment")
    @Expose
    private var detail: String? = ""

    @SerializedName("gender")
    @Expose
    private var gender: String? = ""

    @SerializedName("age")
    @Expose
    private var age: Int? = 0

    @SerializedName("appointment_id")
    @Expose
    private var appointmentId: Int? = 0

    @SerializedName("appointment_type")
    @Expose
    private var appointmentType: String? = ""

    @SerializedName("appointment_time")
    @Expose
    private var appointmentTime: String? = ""

    @SerializedName("appointment_date")
    @Expose
    private var appointmentDate: String? = ""

    @SerializedName("is_expired")
    @Expose
    private var isExpired: Int? = 0

    @SerializedName("prescriptions")
    @Expose
    private var prescriptions: PatientPrescriptionObject? = PatientPrescriptionObject()

    @SerializedName("patient_id")
    @Expose
    private var patientId: Int? = 0

    @SerializedName("is_add_prescription")
    @Expose
    private var isAddPrescription: Int? = 0

    @SerializedName("prescription_pdf_link")
    @Expose
    private var prescriptionPdfUrl: String? = ""

    @SerializedName("is_family_appointment")
    @Expose
    private var isFamilyAppointment: Int? = 0

    @SerializedName("family_data")
    @Expose
    private var familyData: PatientFamilyObject = PatientFamilyObject()

    private var patientMsg: String? = ""

    private var statusMsg: Boolean? = false

    fun getStatusMsg(): Boolean? {
        if (statusMsg == null) return false
        return statusMsg
    }

    fun setStatusMsg(statusMsg: Boolean) {
        this.statusMsg = statusMsg
    }


    fun getPatientMsg(): String? {
        if (patientMsg == null) return ""
        return patientMsg
    }

    fun setPatientMsg(msg: String) {
        this.patientMsg = msg
    }

    fun getIsFamilyAppointment(): Int? {
        if (isFamilyAppointment == null) return 0
        return isFamilyAppointment
    }

    fun setIsFamilyAppointment(isFamilyAppointment: Int) {
        this.isFamilyAppointment = isFamilyAppointment
    }

    fun getIsExpired(): Int? {
        if (isExpired == null) return 0
        return isExpired
    }

    fun setIsExpired(isExpired: Int) {
        this.isExpired = isExpired
    }

    fun getFamilyData(): PatientFamilyObject? {
        if (familyData == null) return PatientFamilyObject()
        return familyData
    }

    fun setFamilyData(familyData: PatientFamilyObject) {
        this.familyData = familyData
    }

    fun getFullName(): String? {
        if (fullName == null) return ""
        return fullName
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }


    fun getAppointmentType(): String? {
        if (appointmentType == null) return ""
        return appointmentType
    }

    fun setAppointmentType(appointmentType: String) {
        this.appointmentType = appointmentType
    }


    fun getAppointmentTime(): String? {
        if (appointmentTime == null) return ""
        return appointmentTime
    }

    fun setAppointmentTime(appointmentTime: String) {
        this.appointmentTime = appointmentTime
    }


    fun getAppointmentDate(): String? {
        if (appointmentDate == null) return ""
        return appointmentDate
    }

    fun setAppointmentDate(appointmentDate: String) {
        this.appointmentDate = appointmentDate
    }


    fun getPhoto(): String? {
        if (photo == null) return "empty url"
        return photo
    }

    fun setPhoto(photo: String) {
        this.photo = photo
    }

    fun getDob(): String? {
        if (dob == null) return ""
        return dob
    }

    fun setDob(dob: String?) {
        this.dob = dob
    }

    fun getDetail(): String? {
        if (detail == null) return ""
        return detail
    }

    fun setDetail(detail: String?) {
        this.detail = detail
    }

    fun getGender(): String? {
        if (gender == null) return ""
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getAge(): Int? {
        if (age == null) return 0
        return age
    }

    fun setAge(age: Int?) {
        this.age = age
    }

    fun getAppointmentId(): Int? {
        if (appointmentId == null) return 0
        return appointmentId
    }

    fun setAppointmentId(appointmentId: Int?) {
        this.appointmentId = appointmentId
    }

    fun getPatientPrescriptionObject(): PatientPrescriptionObject? {
        if (prescriptions == null) return PatientPrescriptionObject()
        return prescriptions
    }

    fun setPatientPrescriptionObject(prescriptions: PatientPrescriptionObject?) {
        this.prescriptions = prescriptions
    }

    fun getPatientId(): Int? {
        if (patientId == null) return 0
        return patientId
    }

    fun setPatientId(patientId: Int?) {
        this.patientId = patientId
    }

    fun getIsAddPrescription(): Int? {
        if (isAddPrescription == null) return 0
        return isAddPrescription
    }

    fun setIsAddPrescription(isAddPrescription: Int?) {
        this.isAddPrescription = isAddPrescription
    }

    fun getPrescriptionPdfUrl(): String? {
        if (prescriptionPdfUrl == null) return ""
        return prescriptionPdfUrl
    }

    fun setPrescriptionPdfUrl(prescriptionPdfUrl: String) {
        this.prescriptionPdfUrl = prescriptionPdfUrl
    }

}