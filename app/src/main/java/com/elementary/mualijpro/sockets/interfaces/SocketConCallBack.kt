package com.elementary.mualijpro.mualij.sockets.interfaces

interface SocketConCallBack {
    fun onSocketError(message: String)
    fun onSocketConSuccess()
    fun onSocketConReConnect()
}