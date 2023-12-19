package com.elementary.mualijpro.interfaces

import com.elementary.mualijpro.models.posts.cities.CitiesResponse

interface ApiArrayCallBack {
    fun onSuccess(requestType: Int, webResponse: CitiesResponse)
    fun onError(requestType: Int, errorMessage: String)
}