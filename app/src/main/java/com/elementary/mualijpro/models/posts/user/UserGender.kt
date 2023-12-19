package com.elementary.mualijpro.models.posts.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserGender {

    @SerializedName("name")
    @Expose
    private var name: String? = ""
    @SerializedName("key")
    @Expose
    private var key: String? = ""

    fun getName(): String? {
        if (name == null) return "Male"
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getKey(): String? {
        if (key == null) return "male"
        return key
    }

    fun setKey(key: String) {
        this.key = key
    }
}