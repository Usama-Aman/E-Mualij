package com.elementary.mualijpro.interfaces

interface ApiResponseEvent {
    fun onSuccess(requestType: Int, webResponse: Any)
    fun onError(requestType: Int, errorMessage: String)
}