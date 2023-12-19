package com.elementary.mualijpro.models.posts.dashboard.updateprescription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdatePrescriptionDataResponse {

    @SerializedName("prescription_pdf_link")
    @Expose
    private var prescriptionPdfUrl: String? = ""


    fun getPrescriptionPdfUrl(): String? {
        if (prescriptionPdfUrl == null) return ""
        return prescriptionPdfUrl
    }

    fun setPrescriptionPdfUrl(prescriptionPdfUrl: String) {
        this.prescriptionPdfUrl = prescriptionPdfUrl
    }

}