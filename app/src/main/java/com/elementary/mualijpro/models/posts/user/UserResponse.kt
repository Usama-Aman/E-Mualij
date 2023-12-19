package com.elementary.mualijpro.models.posts.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("user")
    @Expose
    private var user: User? = null

    @SerializedName("token")
    @Expose
    private var token: String? = null

    fun getUser(): User? {
        return user
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }
}