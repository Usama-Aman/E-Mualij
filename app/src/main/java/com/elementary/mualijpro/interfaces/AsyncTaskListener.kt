package com.elementary.mualijpro.interfaces


interface AsyncTaskListener {
    fun updateResult(path:String)
    fun showProgress()
}