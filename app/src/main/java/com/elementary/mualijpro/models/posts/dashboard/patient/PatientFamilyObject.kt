package com.elementary.mualijpro.models.posts.dashboard.patient

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PatientFamilyObject {

    @SerializedName("full_name")
    @Expose
    private var fullName: String? = ""
    @SerializedName("full_image")
    @Expose
    private var photo: String? = ""
    @SerializedName("family_age")
    @Expose
    private var age: Int? = 0
    @SerializedName("gender")
    @Expose
    private var gender: String? = ""

    fun getFullName(): String? {
        if (fullName == null) return ""
        return fullName
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun getPhoto(): String? {
        if (photo == null) return "empty url"
        return photo
    }

    fun setPhoto(photo: String) {
        this.photo = photo
    }

    fun getAge(): Int? {
        if (age == null) return 0
        return age
    }

    fun setAge(age: Int?) {
        this.age = age
    }

    fun getGender(): String? {
        if (gender == null) return ""
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

}