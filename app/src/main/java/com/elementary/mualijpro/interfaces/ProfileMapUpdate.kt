package com.elementary.mualijpro.interfaces


interface ProfileMapUpdate {
    fun onMapDoneClick()
    fun onMapDrag(mapLat: Double, mapLong: Double, mapAddress: String)
}