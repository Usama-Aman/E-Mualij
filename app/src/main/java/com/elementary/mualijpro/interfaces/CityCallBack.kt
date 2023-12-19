package com.elementary.mualijpro.interfaces

import com.elementary.mualijpro.models.posts.cities.Cities
import java.text.FieldPosition

interface CityCallBack {
    fun onCallBack(cityObject: Cities, position: Int)
}