package com.elementary.mualijpro.networks

import android.accounts.NetworkErrorException
import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.elementary.mualijpro.R
import com.elementary.mualijpro.interfaces.ApiArrayCallBack
import com.elementary.mualijpro.interfaces.ApiResponseEvent
import com.google.gson.JsonObject
import com.elementary.mualijpro.models.local.WebResponse
import com.elementary.mualijpro.models.local.profile.ProfileGetModel
import com.elementary.mualijpro.models.posts.cities.CitiesResponse
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

class ApiController private constructor(mContext: Context) : Runnable {

    private var apiClient: ApiClient? = null
    private val apiCode422: Int = 422
    private val apiCode500: Int = 500
    private val apiCode200: Int = 200
    private val apiCode235: Int = 235
    private val apiCode429: Int = 429
    private val apiCode400: Int = 400
    private val apiCode503: Int = 503//Maintenance Code
    private val mContext: Context = mContext

    init {
        if (mApiService == null) {
            if (apiClient == null)
                apiClient = ApiClient(mContext)

            mApiService = Objects.requireNonNull<Retrofit>(apiClient!!.getClient())
                .create(ApiService::class.java)

        }
    }

    override fun run() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    // User Login
    fun loginUser(jsonObject: JsonObject) {
        mApiService?.loginReq(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.loginRequest)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.loginRequest, handleError(t))
            }
        })
    }

    fun forgotPass(jsonObject: JsonObject) {
        mApiService?.forgotPass(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.forgotPass)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.forgotPass, handleError(t))
            }
        })
    }

    fun changePass(jsonObject: JsonObject) {
        mApiService?.changePass(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.changePass)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.changePass, handleError(t))
            }
        })
    }

    fun weeklyScheduleUpdate(jsonObject: JsonObject) {
        mApiService?.weeklyScheduleUpdate(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.weeklyScheduleUpdate)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.weeklyScheduleUpdate, handleError(t))
            }
        })
    }

    fun getWeeklySchedule() {
        mApiService?.getWeeklySchedule()?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.weeklyScheduleList)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.weeklyScheduleList, handleError(t))
            }
        })
    }


    // Get Weekly Appointments
    fun weeklyAppointments(jsonObject: JsonObject) {
        mApiService?.getWeeklyAppointments(jsonObject)
            ?.enqueue(object : Callback<WebResponse<Any>> {
                override fun onResponse(
                    call: Call<WebResponse<Any>>,
                    response: Response<WebResponse<Any>>
                ) {
                    responseHandling(response, APIsTag.weeklyAppointments)
                }

                override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                    apiResponseEvent?.onError(APIsTag.weeklyAppointments, handleError(t))
                }
            })
    }


    // Get Available Schedule List
    fun availableScheduleRequest(jsonObject: JsonObject) {
        mApiService?.getAvailableScheduleList(jsonObject)
            ?.enqueue(object : Callback<WebResponse<Any>> {
                override fun onResponse(
                    call: Call<WebResponse<Any>>,
                    response: Response<WebResponse<Any>>
                ) {
                    responseHandling(response, APIsTag.availableScheduleList)
                }

                override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                    apiResponseEvent?.onError(APIsTag.availableScheduleList, handleError(t))
                }
            })
    }

    // Reschedule Appointment
    fun rescheduleAppointment(jsonObject: JsonObject) {
        mApiService?.rescheduleAppointment(jsonObject)
            ?.enqueue(object : Callback<WebResponse<Any>> {
                override fun onResponse(
                    call: Call<WebResponse<Any>>,
                    response: Response<WebResponse<Any>>
                ) {
                    responseHandling(response, APIsTag.reschedule)
                }

                override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                    apiResponseEvent?.onError(APIsTag.reschedule, handleError(t))
                }
            })
    }


    // Get Patient Data
    fun getPatientData(jsonObject: JsonObject) {
        mApiService?.getPatientData(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.patientData)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.patientData, handleError(t))
            }
        })
    }

    // Get Patient Data
    fun getAppointmentDetail(jsonObject: JsonObject) {
        mApiService?.getAppointmentDetail(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.appointmentDetail)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.appointmentDetail, handleError(t))
            }
        })
    }

    // Get Prescriptions Array
    fun getPrescriptionsArray() {
        mApiService?.getPrescriptionsArray()?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.prescriptionsArrayData)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.prescriptionsArrayData, handleError(t))
            }
        })
    }

    fun deviceRegister(jsonObject: JsonObject) {
        mApiService?.deviceRegister(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.deviceRegister)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.deviceRegister, handleError(t))
            }
        })
    }

    // Cancel  Appointment
    fun cancelAppointment(jsonObject: JsonObject) {
        mApiService?.cancelAppointment(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.cancelAppointment)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.cancelAppointment, handleError(t))
            }
        })
    }


    // Report Patient
    fun reportPatient(jsonObject: JsonObject) {
        mApiService?.reportPatient(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.reportPatient)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.reportPatient, handleError(t))
            }
        })
    }


    // User Update Profile
    fun updateProfile(
        firstName: RequestBody?,
        cityId: RequestBody?,
        lastName: RequestBody?,
        email: RequestBody?,
        mobileNumber: RequestBody?,
        statement: RequestBody?,
        education: RequestBody?,
        certificates: RequestBody?,
        gender: RequestBody?,
        preferredLanguage: RequestBody?,
        doctorFee: RequestBody?,
        isOnlineAccept: RequestBody?,
        location: RequestBody?,
        latitude: RequestBody?,
        longitude: RequestBody?,
        photo: MultipartBody.Part?
    ) {

        mApiService?.updateProfile(
            firstName,
            cityId,
            lastName,
            email,
            mobileNumber,
            statement,
            education,
            certificates,
            gender,
            preferredLanguage,
            doctorFee,
            isOnlineAccept,
            location,
            latitude,
            longitude,
            photo
        )?.enqueue(object : Callback<WebResponse<Any>> {

            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.updateProfile)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.updateProfile, handleError(t))
            }
        })
    }

    private var callApi: Call<WebResponse<Any>>? = null

    fun uploadChatImage(
        type: RequestBody?,
        file: MultipartBody.Part?
    ) {

        callApi = mApiService?.uploadChatImage(
            type,
            file
        )
        callApi?.enqueue(object : Callback<WebResponse<Any>> {

            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.uploadChatImage)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.uploadChatImage, handleError(t))
            }
        })


    }

    fun uploadChatVideo(
        type: RequestBody?,
        file: MultipartBody.Part?
    ) {

        callApi = mApiService?.uploadChatVideo(
            type,
            file
        )

        callApi?.enqueue(object : Callback<WebResponse<Any>> {

            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.uploadChatVideo)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.uploadChatVideo, handleError(t))
            }
        })
    }

    fun uploadChatAudio(
        type: RequestBody?,
        file: MultipartBody.Part?
    ) {

        callApi = mApiService?.uploadChatAudio(
            type,
            file
        )

        callApi?.enqueue(object : Callback<WebResponse<Any>> {

            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.uploadChatAudio)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {

                apiResponseEvent?.onError(APIsTag.uploadChatAudio, handleError(t))
            }
        })
    }

    fun cancelRunningTask() {
        if (callApi != null) {
            if (!callApi?.isCanceled!!) {
                callApi?.cancel()
            }
        }
    }

    // Get Weekly Appointments
    fun updatePrescription(jsonObject: JsonObject) {
        mApiService?.updatePrescription(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.updatePrescription)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.updatePrescription, handleError(t))
            }
        })
    }


    // Complete Appointment
    fun completeAppointment(jsonObject: JsonObject) {
        mApiService?.completeAppointment(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.completeAppointment)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.completeAppointment, handleError(t))
            }
        })
    }

    // Get Weekly Appointments
    fun getStatistics(jsonObject: JsonObject) {
        mApiService?.getStatistics(jsonObject)?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.getStatistics)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.getStatistics, handleError(t))
            }
        })
    }


    // Get Cities
    fun getCitiesList() {
        mApiService?.getCitiesList()?.enqueue(object : Callback<CitiesResponse> {
            override fun onResponse(
                call: Call<CitiesResponse>,
                response: Response<CitiesResponse>
            ) {
                responseHandlingArray(response, APIsTag.getCities)
            }

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.getCities, handleError(t))
            }
        })
    }

    // Get Profile Data
    fun getProfileData() {
        mApiService?.getProfileData()?.enqueue(object : Callback<WebResponse<Any>> {
            override fun onResponse(
                call: Call<WebResponse<Any>>,
                response: Response<WebResponse<Any>>
            ) {
                responseHandling(response, APIsTag.getProfileData)
            }

            override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                apiResponseEvent?.onError(APIsTag.getProfileData, handleError(t))
            }
        })
    }


    // Success Response Handling
    private fun responseHandlingArray(response: Response<CitiesResponse>, requestType: Int) {

        if (response.isSuccessful) {
            if ((response.code() == apiCode200 || response.code() == apiCode400) && response.body() != null) {
                val result = response.body()
                if (result?.getCode() == apiCode235)
                    apiResponseEvent?.onError(
                        requestType,
                        result?.getMessage()!!
                    )
                else
                    if (result?.getStatus()!!)
                        if (result?.getAppToken() != null && Constants.appToken == result?.getAppToken())
                            apiArrayResponseEvent?.onSuccess(requestType, result)
                        else
                            apiArrayResponseEvent?.onError(
                                requestType,
                                mContext.resources.getString(R.string.txt_unknown_request)
                            )
                    else
                        apiArrayResponseEvent?.onError(requestType, result?.getMessage()!!)

            } else if ((response.code() == apiCode422 || response.code() == apiCode500) && response.errorBody() != null)
                apiArrayResponseEvent?.onError(
                    requestType,
                    readErrorBody(response.errorBody() as ResponseBody)
                )
            else
                apiArrayResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_something_went_wrong)
                )
        } else {

            if (response.code() == apiCode429)
                apiResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_request_limit_exceed)
                )
            else
                apiArrayResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_something_went_wrong)
                )
        }

    }

    // Success Response Handling
    private fun responseHandling(response: Response<WebResponse<Any>>, requestType: Int) {

        if (response.isSuccessful) {

            if ((response.code() == apiCode200 || response.code() == apiCode400) && response.body() != null) {
                val result = response.body()
                if (result?.code == apiCode235) {

                    apiResponseEvent?.onError(
                        requestType,
                        result?.message!!
                    )
                }
                else {

                    if (result?.status!!) {

                        if (result?.appToken != null && Constants.appToken == result?.appToken) {

                            apiResponseEvent?.onSuccess(requestType, result)
                        }
                        else {

                            apiResponseEvent?.onError(
                                requestType,
                                mContext.resources.getString(R.string.txt_unknown_request)
                            )
                        }
                    }
                    else {

                        apiResponseEvent?.onError(requestType, result?.message!!)
                    }
                }

            } else if ((response.code() == apiCode422 || response.code() == apiCode500) && response.errorBody() != null) {

                apiResponseEvent?.onError(
                    requestType,
                    readErrorBody(response.errorBody() as ResponseBody)
                )
            }
            else {

                apiResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_something_went_wrong)
                )
            }
        } else {

            if (response.code() == apiCode429) {

                apiResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_request_limit_exceed)
                )
            }
            if (response.code() == apiCode503) {
                AppUtils.callServiceDownActivity(mContext)
            }
            else {

                apiResponseEvent?.onError(
                    requestType,
                    mContext.resources.getString(R.string.txt_something_went_wrong)
                )
            }
        }

    }


    // Error Body Handling
    private fun readErrorBody(response: ResponseBody): String {
        var errorMessage: String
        val jObjError = JSONObject(response.string())
        errorMessage = try {
            jObjError.getString("msg")
        } catch (e: Exception) {
            mContext.resources.getString(R.string.txt_something_went_wrong)
        }
        return errorMessage
    }

    // Failure Handling
    private fun handleError(t: Throwable?): String {

        var error = mContext.resources.getString(R.string.txt_something_went_wrong)
        if (t == null)
            return error
        when (t) {
            is SocketTimeoutException -> error = t.localizedMessage
            is UnknownHostException -> error =
                mContext.resources.getString(R.string.txt_no_internet_connection)
            is IOException -> error = t.localizedMessage
            is IllegalStateException -> error = t.localizedMessage
            is NetworkErrorException -> error = t.localizedMessage
            is Exception -> error = t.localizedMessage
            is NumberFormatException -> error = t.message.toString()
        }



        return error
    }

    companion object {

        private var mApiService: ApiService? = null
        private var mApiController: ApiController? = null
        private var TAG: String? = "ApiController"
        private var apiResponseEvent: ApiResponseEvent? = null
        private var apiArrayResponseEvent: ApiArrayCallBack? = null

        fun getInstance(mContext: Context, apiResEvent: ApiResponseEvent): ApiController {
            if (mApiController == null)
                mApiController = ApiController(mContext)
            apiResponseEvent = apiResEvent
            return mApiController as ApiController
        }

        fun getInstanceArray(mContext: Context, apiResEvent: ApiArrayCallBack): ApiController {
            if (mApiController == null)
                mApiController = ApiController(mContext)
            apiArrayResponseEvent = apiResEvent
            return mApiController as ApiController
        }
    }
}
