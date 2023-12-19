package com.elementary.mualijpro.models.posts.dashboard.prescriptiondialogarrays

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MedicineFrequency {

    @SerializedName("id")
    @Expose
    private var id: Int? = 0

    @SerializedName("frequency")
    @Expose
    private var value: String? = ""

    fun getId(): Int? {
        if (id == null) return 0
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getValue(): String? {
        if (value == null) return ""
        return value
    }

    fun setValue(value: String) {
        this.value = value
    }

}