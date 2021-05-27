package com.bdjobs.app.liveInterview.data.socketClient

import com.bdjobs.app.liveInterview.utils.EventConstants
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
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
    private var localSocketID:String = ""
    private var roomName:String = "6606231"
    private var TAG:String = "SS"
    private var serverURL:String = "https://live.bdjobs.com/"

    fun init(signalingEvent: SignalingEvent) {
        try {
            socket = IO.socket(serverURL)

            socket?.on(EventConstants.EVENT_SOCKET_CONNECT) {
                localSocketID = socket?.id().toString()
                Timber.d("TAG: Local socket id $localSocketID")
                signalingEvent.setLocalSocketID(localSocketID)
                socket?.emit(EventConstants.EVENT_SUBSCRIBE, roomName, localSocketID, "Salvin", "A")
            }

            socket?.on(EventConstants.EVENT_PARTICIPANT_COUNT) {args: Array<Any?>? ->
                val message = args?.get(0) as JSONObject
                Timber.d("TAG: participant string - ${message.getString("participant")}")
            }

            socket?.on(EventConstants.EVENT_1ST_USER_CHECK) {args: Array<Any?>? ->
                signalingEvent.on1stUserCheck(args)
            }

            socket?.on(EventConstants.EVENT_NEW_USER_START_NEW) {args: Array<Any?>? ->
                signalingEvent.onNewUserStartNew(args)
            }

//            socket?.on(EventConstants.EVENT_CREATED) { args: Array<Any?>? ->
//                signalingEvent.onEventCreated(args)
//            }
//
//            socket?.on(EventConstants.EVENT_FULL) {
//                signalingEvent.onEventFull()
//            }
//
//            socket?.on(EventConstants.EVENT_JOIN) { args: Array<Any> ->
//                signalingEvent.onEventJoin(args)
//            }
//            socket?.on(EventConstants.EVENT_JOINED) { args: Array<Any> ->
//               signalingEvent.onEventJoined(args)
//            }
//            socket?.on(EventConstants.EVENT_LOG) { args: Array<Any> ->
//                signalingEvent.onEventLog(args)
//            }

            socket?.on(EventConstants.EVENT_ICE_CANDIDATES) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onReceiveIceCandidate(args)
                }
            }

            socket?.on(EventConstants.EVENT_INTERVIEW_CALL_RECIEVE) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onReceiveIceCandidate(args)
                }
            }
            socket?.on(EventConstants.EVENT_SDP) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onReceiveSDP(args)
                }
            }

//            socket?.on(EventConstants.EVENT_MESSAGE) { args: Array<Any?>? ->
//                if (args != null) {
//                    signalingEvent.onEventMessage2(args)
//                }
//                Timber.d(": got a message")
//
//            }
//            socket?.on(EventConstants.EVENT_MESSAGE) { args: Array<Any> ->
//                signalingEvent.onEventMessage(args)
//            }
            socket?.on(Socket.EVENT_DISCONNECT) {
                signalingEvent.onEventDisconnected()
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> ->
                signalingEvent.onEventConnectionError(args)
            }
            socket?.on(EventConstants.EVENT_USER_JOINED) { args: Array<Any> ->
                signalingEvent.onUserJoined(args)
            }
            socket?.on(EventConstants.EVENT_USER_DISCONNECTED) { args : Array<Any> ->
                signalingEvent.onUserDisconnected(args)
            }
            socket?.on(EventConstants.EVENT_MESSAGE_RECEIVED){ args: Array<Any> ->
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

    fun sendSessionDescription(sessionDescription: SessionDescription) {

        val sendingSDP = JSONObject()
        try {
            sendingSDP.put("type", sessionDescription.type?.canonicalForm())
            sendingSDP.put("sdp", sessionDescription.description)

        } catch (e: Exception) {
            Timber.d("SignallingServer: sendingSDP error - $e")
        }
        Timber.d("sendingSDP string - $sendingSDP")

        socket?.emit(EventConstants.EVENT_MESSAGE, sendingSDP)
    }

    fun sendIceCandidate(iceCandidate: IceCandidate?) {

        val message = JSONObject()

        try {
            message.put("type", "candidate")
            message.put("label", iceCandidate?.sdpMLineIndex)
            message.put("id", iceCandidate?.sdpMid)
            message.put("candidate", iceCandidate?.sdp)

            socket?.emit(EventConstants.EVENT_MESSAGE, message)

        } catch (e: JSONException) {
            Timber.d("onEmit IceCandidate: JSONException $e")
            e.printStackTrace()
        }
    }

    fun sendMessage() {
        socket?.emit("message-salvin", "got user media")
    }

    fun joinToChatRoom(nickname:String) {
        socket?.emit("join",nickname)
    }

    fun messageDetection(nickname: String,message:String) {
        socket?.emit("messagedetection",nickname,message)
    }

    fun sendMedia() {
        socket?.emit(EventConstants.EVENT_MESSAGE, "got user media")
    }
}