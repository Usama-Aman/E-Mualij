package com.elementary.mualijpro.utils

import com.elementary.mualijpro.models.local.WebResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CommonFunctions {

    companion object {

        fun <Any> convertDataType(objectData: WebResponse<*>, classType: Class<Any>): Any {
            val gs = Gson()
            val js = gs.toJson(objectData.data)
            return gs.fromJson<Any>(js, classType)
        }

        fun <Any> convertDataTypeToArray(objectData: WebResponse<*>): ArrayList<Any> {
            val gs = Gson()
            val arrayListType = object : TypeToken<ArrayList<Any>>() {}.type
            val js = gs.toJson(objectData.data)
            return gs.fromJson<ArrayList<Any>>(js, arrayListType)
        }

    }
}
