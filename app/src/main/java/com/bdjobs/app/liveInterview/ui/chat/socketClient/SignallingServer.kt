package com.bdjobs.app.liveInterview.ui.chat.socketClient

import com.bdjobs.app.liveInterview.utils.Constants
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

//
// Created by Soumik on 4/29/2021.
// piyal.developer@gmail.com
//

class SignalingServer {


    companion object {

        private var instance: SignalingServer? = null

        fun get(): SignalingServer? {
            if (instance == null) {
                synchronized(SignalingServer::class.java) {
                    if (instance == null) {
                        instance = SignalingServer()
                    }
                }
            }
            return instance
        }
    }


    private var socket:Socket?=null
    private var roomName:String = "salvin-demo"
    private var serverURL:String = "https://lit-headland-35738.herokuapp.com/"

    fun init(signalingEvent: SignalingEvent) {
        try {
            socket = IO.socket(serverURL)

            socket?.on(Socket.EVENT_CONNECT) {

                socket?.emit(Constants.EVENT_CREATE_OR_JOIN, roomName)
                signalingEvent.onEventConnected()
            }

            socket?.on(Constants.EVENT_IPADDR) {
                signalingEvent.onEventIPADDR()
            }

            socket?.on(Constants.EVENT_CREATED) {
                signalingEvent.onEventCreated()
            }

            socket?.on(Constants.EVENT_FULL) {
                signalingEvent.onEventFull()
            }

            socket?.on(Constants.EVENT_JOIN) {
                signalingEvent.onEventJoin()
            }
            socket?.on(Constants.EVENT_JOINED) {
               signalingEvent.onEventJoined()
            }
            socket?.on(Constants.EVENT_LOG) { args: Array<Any> ->
                signalingEvent.onEventLog(args)
            }
            socket?.on(Constants.EVENT_MESSAGE) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onEventMessage2(args)
                }
                Timber.d(": got a message")

            }
            socket?.on(Constants.EVENT_MESSAGE) { args: Array<Any> ->
                signalingEvent.onEventMessage(args)
            }
            socket?.on(Socket.EVENT_DISCONNECT) {
                signalingEvent.onEventDisconnected()
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> ->
                signalingEvent.onEventConnectionError(args)
            }
            socket?.on(Constants.EVENT_USER_JOINED) { args: Array<Any> ->
                signalingEvent.onUserJoined(args)
            }
            socket?.on(Constants.EVENT_USER_DISCONNECTED) { args : Array<Any> ->
                signalingEvent.onUserDisconnected(args)
            }
            socket?.on(Constants.EVENT_MESSAGE_RECEIVED){args: Array<Any> ->
                signalingEvent.onMessageReceived(args)
            }

            Timber.d("Connecting ... $socket")

            socket?.connect()

        } catch (e: Exception) {
            Timber.d("Exception : $e")
            e.printStackTrace()
        }
    }

    fun destroy() {
        socket?.emit("bye", "bye")
        socket?.disconnect()
        socket?.close()
    }

 /*   fun sendSessionDescription(sessionDescription: SessionDescription) {

        val sendingSDP = JSONObject()
        try {
            sendingSDP.put("type", sessionDescription.type?.canonicalForm())
            sendingSDP.put("sdp", sessionDescription.description)

        } catch (e: Exception) {
            Timber.d("SignallingServer: sendingSDP error - $e")
        }
        Timber.d("sendingSDP string - $sendingSDP")

        socket?.emit(Constants.EVENT_MESSAGE, sendingSDP)
    }

    fun sendIceCandidate(iceCandidate: IceCandidate?) {

        val message = JSONObject()

        try {
            message.put("type", "candidate")
            message.put("label", iceCandidate?.sdpMLineIndex)
            message.put("id", iceCandidate?.sdpMid)
            message.put("candidate", iceCandidate?.sdp)

            socket?.emit(Constants.EVENT_MESSAGE, message)

        } catch (e: JSONException) {
            Timber.d("onEmit IceCandidate: JSONException $e")
            e.printStackTrace()
        }
    }*/

    fun sendMessage() {
        socket?.emit("message-salvin", "got user media")
    }

    fun joinToChatRoom(nickname:String) {
        socket?.emit("join",nickname)
    }

    fun messageDetection(nickname: String,message:String) {
        socket?.emit("messagedetection",nickname,message)
    }
}