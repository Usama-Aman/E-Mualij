package com.taxi.bravoapp.utils

import android.content.Context
import android.os.Handler
import android.util.Log
import com.elementary.mualijpro.sockets.interfaces.SocketCallback
import com.elementary.mualijpro.mualij.sockets.interfaces.SocketConCallBack
import com.elementary.mualijpro.mualij.sockets.WebResponseSocket
import com.elementary.mualijpro.R
import com.elementary.mualijpro.sockets.ack.AckWithTimeOut
import com.elementary.mualijpro.utils.AppUtils
import com.elementary.mualijpro.utils.Constants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class SocketSingleton {

    private val apiCode200: Int = 200
    private val apiCode422: Int = 422
    private val apiCode500: Int = 500

    companion object {
        val TAG = "SocketConnection"
        lateinit var instance: SocketSingleton
        lateinit var mSocket: Socket
        lateinit var context: Context
        lateinit var socketConCallBack: SocketConCallBack

        fun getInstance(context: Context): SocketSingleton {
            this.context = context

            if (!::instance.isInitialized) {
                instance = SocketSingleton()

                initSocket()
            }

            return instance
        }

        fun setCallBackRef(socketConCallBack: SocketConCallBack) {
            this.socketConCallBack = socketConCallBack
        }

        fun initSocket() {

            initializeSocket()
            listenerForConnection()
        }

        fun getSocketObj(): Socket {
            return mSocket!!
        }

        private fun initializeSocket() {
            try {
                mSocket = IO.socket(AppUtils.decryptValue(Constants.MAIN_SOCKET_BASE_URL))
                mSocket!!.connect()
            } catch (e: Exception) {

            }
        }


        private fun listenerForConnection() {
            //Log.e("listenerForConnection: ", "-----")
            if (::mSocket.isInitialized) {

                mSocket!!.on(Socket.EVENT_CONNECT) {
                    Log.e(TAG, "EVENT_CONNECT")
                    if (::socketConCallBack.isInitialized) {
                        socketConCallBack?.onSocketConSuccess()
                    }
                }
                mSocket!!.on(Socket.EVENT_RECONNECT) {
                    Log.e(TAG, "EVENT_RECONNECT")
                }
                mSocket!!.on(Socket.EVENT_RECONNECT_FAILED) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.socket_connection_error_occurred))
                    Log.e(TAG, "EVENT_RECONNECT_FAILED")
                }
                mSocket!!.on(Socket.EVENT_RECONNECTING) {
                    Log.e(TAG, "EVENT_RECONNECTING")
                }
                mSocket!!.on(Socket.EVENT_RECONNECT_ERROR) {
                    Log.e(TAG, "EVENT_RECONNECT_ERROR")
                }
                mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.socket_connection_timeout))
                    Log.e(TAG, "EVENT_CONNECT_ERROR")
                }
                mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.socket_connection_timeout))
                    Log.e(TAG, "EVENT_CONNECT_TIMEOUT")
                }
                mSocket!!.on(Socket.EVENT_ERROR) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.socket_error_occurred))
                    Log.e(TAG, "EVENT_ERROR")
                }
                mSocket!!.on(Socket.EVENT_DISCONNECT) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.socket_disconnect_error))
                    Log.e(TAG, "EVENT_DISCONNECT")
                }
                mSocket!!.on(Socket.EVENT_MESSAGE) {
                    socketGenericErrorMessage(context?.resources!!.getString(R.string.something_went_wrong_with_socket_connection))
                    Log.e(TAG, "EVENT_MESSAGE")
                }
            }
        }

        private fun socketGenericErrorMessage(error: String) {
            val h = Handler(context?.mainLooper)
            h.post {
                if (::socketConCallBack.isInitialized)
                    socketConCallBack?.onSocketError(error)
            }
        }
    }

    fun callSocketApiRequest(
        key: String,
        jsonObject: JsonObject,
        tag: Int,
        reqCallBack: SocketCallback
    ) {

        if (mSocket!!.connected()) {
            mSocket!!.emit(key, jsonObject, object : AckWithTimeOut(Constants.AckTimeOut) {

                override fun call(vararg args: Any?) {
                    super.call(args)
                    if (args[0] == "No Ack") {
                        val h = Handler(context?.mainLooper)
                        h.post {
                            reqCallBack.onAckReqFailure(
                                context?.resources?.getString(R.string.socket_request_connection_fail)!!,
                                tag
                            )
                        }
                    } else {
                        val jsonObject1 = args[0] as JSONObject
                        val gson = GsonBuilder()
                        val collectionType = object : TypeToken<WebResponseSocket<Any>>() {}.type
                        val response = gson.create().fromJson<WebResponseSocket<Any>>(
                            jsonObject1.toString(),
                            collectionType
                        )
                    }
                }
            })
        } else
            socketNotConnected(reqCallBack, tag)
    }

    private fun socketNotConnected(reqCallBack: SocketCallback, tag: Int) {
        reqCallBack.onAckReqFailure(
            context?.resources?.getString(R.string.socket_connection_error)!!,
            tag
        )

        initSocket()
    }

    //Response Handling
    private fun responseHandling(
        response: WebResponseSocket<Any>,
        tag: Int,
        reqCallBack: SocketCallback
    ) {

        if (response != null)
            when {
                response.status == apiCode200 -> reqCallBack.onAckReqSuccess(response, tag)
                response.status == apiCode422 -> reqCallBack.onAckReqFailure(
                    response.message!!,
                    tag
                )
                response.status == apiCode500 -> reqCallBack.onAckReqFailure(
                    response.message!!,
                    tag
                )
                else -> reqCallBack.onAckReqFailure(
                    context?.resources?.getString(R.string.txt_something_went_wrong)!!,
                    tag
                )
            }
        else
            reqCallBack.onAckReqFailure(
                context?.resources?.getString(R.string.txt_something_went_wrong)!!,
                tag
            )
    }


}