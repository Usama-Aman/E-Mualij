package com.elementary.mualijpro.models.posts.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User : Serializable {

    @SerializedName("id")
    @Expose
    private var id: Int? = 0
    @SerializedName("first_name")
    @Expose
    private var firstName: String? = ""
    @SerializedName("last_name")
    @Expose
    private var lastName: String? = ""
    @SerializedName("city_id")
    @Expose
    private var cityId: Int? = 0
    @SerializedName("email")
    @Expose
    private var email: String? = ""
    @SerializedName("mobile_number")
    @Expose
    private var mobileNumber: String? = ""
    @SerializedName("photo")
    @Expose
    private var photo: String? = ""
    @SerializedName("designation")
    @Expose
    private var designation: String? = ""
    @SerializedName("location")
    @Expose
    private var location: String? = ""
    @SerializedName("statement")
    @Expose
    private var statement: String? = ""
    @SerializedName("education")
    @Expose
    private var education: String? = ""
    @SerializedName("certificates")
    @Expose
    private var certificates: String? = ""
    @SerializedName("preferred_language")
    @Expose
    private var preferredLang: String? = ""
    @SerializedName("doctor_fee")
    @Expose
    private var doctorFee: String? = ""
    @SerializedName("is_sponsored")
    @Expose
    private var isSponsored: String? = ""
    @SerializedName("is_online_accept")
    @Expose
    private var isOnlineAccept: Int? = 0
    @SerializedName("token")
    @Expose
    private var token: String? = ""
    @SerializedName("full_name")
    @Expose
    private var fullName: String? = ""
    @SerializedName("full_image")
    @Expose
    private var fullImage: String? = ""
    @SerializedName("gender")
    @Expose
    private var gender: UserGender? = UserGender()

    @SerializedName("latitude")
    @Expose
    private var latitude: Double? = 0.0
    @SerializedName("longitude")
    @Expose
    private var longitude: Double? = 0.0

    fun getLatitude(): Double? {
        if (latitude == null) return 0.0
        return latitude
    }

    fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }

    fun getLongitude(): Double? {
        if (longitude == null) return 0.0
        return longitude
    }

    fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }

    fun getGender(): UserGender? {
        return gender
    }

    fun setGender(gender: UserGender) {
        this.gender = gender
    }

    fun getFullImage(): String? {
        if (fullImage == null) return "empty url"
        return fullImage
    }

    fun setFullImage(fullImage: String) {
        this.fullImage = fullImage
    }

    fun getFullName(): String? {
        if (fullName == null) return ""
        return fullName
    }

    fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun getToken(): String? {
        if (token == null) return ""
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getIsOnlineAccept(): Int? {
        if (isOnlineAccept == null) return 0
        return isOnlineAccept
    }

    fun setIsOnlineAccept(isOnlineAccept: Int) {
        this.isOnlineAccept = isOnlineAccept
    }

    fun getIsSponsored(): String? {
        if (isSponsored == null) return ""
        return isSponsored
    }

    fun setIsSponsored(isSponsored: String) {
        this.isSponsored = isSponsored
    }

    fun getPreferredLanguage(): String? {
        if (preferredLang == null) return "en"
        return preferredLang
    }

    fun setPreferredLanguage(preferredLang: String) {
        this.preferredLang = preferredLang
    }

    fun getDoctorFee(): String? {
        if (doctorFee == null) return ""
        return doctorFee
    }

    fun setDoctorFee(doctorFee: String) {
        this.doctorFee = doctorFee
    }

    fun getCertificates(): String? {
        if (certificates == null) return ""
        return certificates
    }

    fun setCertificates(certificates: String) {
        this.certificates = certificates
    }

    fun getEducation(): String? {
        if (education == null) return ""
        return education
    }

    fun setEducation(education: String) {
        this.education = education
    }

    fun getStatement(): String? {
        if (statement == null) return ""
        return statement
    }

    fun setStatement(statement: String) {
        this.statement = statement
    }

    fun getLocation(): String? {
        if (location == null) return "";
        return location
    }

    fun setLocation(location: String) {
        this.location = location
    }

    fun getDesignation(): String? {
        if (designation == null) return ""
        return designation
    }

    fun setDesignation(designation: String) {
        this.designation = designation
    }

    fun getPhoto(): String? {
        if (photo == null) return ""
        return photo
    }

    fun setPhoto(photo: String) {
        this.photo = photo
    }

    fun getMobileNumber(): String? {
        if (mobileNumber == null) return ""
        return mobileNumber
    }

    fun setMobileNumber(mobileNumber: String) {
        this.mobileNumber = mobileNumber
    }

    fun getEmail(): String? {
        if (email == null) return ""
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getLastName(): String? {
        if (lastName == null) return ""
        return lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun getFirstName(): String? {
        if (firstName == null) return ""
        return firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getId(): Int? {
        if (id == null) return 0
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getCityId(): Int? {
        if (cityId == null) return 0
        return cityId
    }

    fun setCityId(cityId: Int) {
        this.cityId = cityId
    }

}