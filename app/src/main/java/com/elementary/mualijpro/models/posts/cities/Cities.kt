package com.elementary.mualijpro.models.posts.cities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Cities {
    @SerializedName("id")
    @Expose
    private var id: Int? = 0
    @SerializedName("name_en")
    @Expose
    private var nameEn: String? = ""
    @SerializedName("name_ar")
    @Expose
    private var nameAr: String? = ""
    var isSelected = false

    fun getId(): Int? {
        if (id == null) return 0
        return id
    }

    fun getNameEn(): String? {
        if (nameEn == null) return ""
        return nameEn
    }

    fun getNameAr(): String? {
        if (nameAr == null) return ""
        return nameAr
    }
}