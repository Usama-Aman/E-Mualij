package com.elementary.mualijpro.networks

import com.google.gson.JsonObject
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.local.profile.ProfileGetModel
import com.elementary.mualijpro.models.posts.cities.CitiesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("doctor/login")
    fun loginReq(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/schedule/update")
    fun weeklyScheduleUpdate(@Body body: JsonObject): Call<WebResponse<Any>>

    @GET("doctor/weekly/schedule/list")
    fun getWeeklySchedule(): Call<WebResponse<Any>>

    @POST("doctor/change/password")
    fun changePass(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/device/save")
    fun deviceRegister(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/schedule/appointment_list")
    fun getWeeklyAppointments(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/appointment/detail")
    fun getPatientData(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/appointment/detail")
    fun getAppointmentDetail(@Body body: JsonObject): Call<WebResponse<Any>>

    @GET("doctor/prescription/medicines")
    fun getPrescriptionsArray(): Call<WebResponse<Any>>

    @POST("doctor/prescription/add")
    fun updatePrescription(@Body body: JsonObject): Call<WebResponse<Any>>

    @Multipart
    @POST("doctor/profile/update")
    fun updateProfile(
        @Part("first_name") firstName: RequestBody?,
        @Part("city_id") cityId: RequestBody?,
        @Part("last_name") lastName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("mobile_number") mobileNumber: RequestBody?,
        @Part("statement") statement: RequestBody?,
        @Part("education") education: RequestBody?,
        @Part("certificates") certificates: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("preferred_language") preferredLanguage: RequestBody?,
        @Part("doctor_fee") doctorFee: RequestBody?,
        @Part("is_online_accept") isOnlineAccept: RequestBody?,
        @Part("location") location: RequestBody?,
        @Part("latitude") latitude: RequestBody?,
        @Part("longitude") longitude: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Call<WebResponse<Any>>

    @POST("doctor/forget/password")
    fun forgotPass(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/appointment/delete")
    fun cancelAppointment(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/reschedule/list")
    fun getAvailableScheduleList(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/reschedule/update")
    fun rescheduleAppointment(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/patient_report")
    fun reportPatient(@Body body: JsonObject): Call<WebResponse<Any>>

    @GET("cities")
    fun getCitiesList(): Call<CitiesResponse>

    @GET("doctor/profile/get_list")
    fun getProfileData(): Call<WebResponse<Any>>

    @Multipart
    @POST("chat/upload_file")
    fun uploadChatImage(
        @Part("type") type: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<WebResponse<Any>>

    @Multipart
    @POST("chat/upload_file")
    fun uploadChatVideo(
        @Part("type") type: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<WebResponse<Any>>

    @Multipart
    @POST("chat/upload_file")
    fun uploadChatAudio(
        @Part("type") type: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Call<WebResponse<Any>>

    @POST("doctor/statistics")
    fun getStatistics(@Body body: JsonObject): Call<WebResponse<Any>>

    @POST("doctor/complete_appointment")
    fun completeAppointment(@Body body: JsonObject): Call<WebResponse<Any>>

}
