package com.elementary.mualijpro.sockets.interfaces

interface SocketCallback {
    fun onAckReqSuccess(responseObject: Any, tag: Any)
    fun onAckReqFailure(message: String, tag: Any)
}