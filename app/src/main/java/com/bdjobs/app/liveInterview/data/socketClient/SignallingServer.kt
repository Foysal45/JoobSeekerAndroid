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
    private var remoteSocketID:String = ""
    private var roomName:String = "6117059"
    private var serverURL:String = "https://live.bdjobs.com/"

    fun init(signalingEvent: SignalingEvent) {
        try {
            socket = IO.socket(serverURL)

            socket?.on(EventConstants.EVENT_SOCKET_CONNECT) {
                localSocketID = socket?.id().toString()
                Timber.d("TAG: Local socket id $localSocketID")

                signalingEvent.setLocalSocketID(localSocketID)

                val subscribeJO = JSONObject()

                try {
                    subscribeJO.put("room", roomName)
                    subscribeJO.put("socketId", localSocketID)
                    subscribeJO.put("username", "Salvin")
                    subscribeJO.put("uType", "A")
                    socket?.emit(EventConstants.EVENT_SUBSCRIBE, subscribeJO)
                    Timber.d("TAG:onEmit subscribe: jsonObject $subscribeJO")

                } catch (e: JSONException) {
                    Timber.d("TAG:onEmit subscribe: JSONException $e")
                    e.printStackTrace()
                }
            }

            socket?.on(EventConstants.EVENT_PARTICIPANT_COUNT) {args: Array<Any?>? ->
                val message = args?.get(0) as JSONObject
                Timber.d("TAG: participant string - ${message.getString("participant")}")
            }

            socket?.on(EventConstants.EVENT_1ST_USER_CHECK) {args: Array<Any?>? ->
                signalingEvent.on1stUserCheck(args)
            }

            socket?.on(EventConstants.EVENT_NEW_USER_START_NEW) {args: Array<Any?>? ->
                val argument = args?.get(0) as JSONObject
                remoteSocketID = argument.getString("sender")
                signalingEvent.onNewUserStartNew(args)
            }

            socket?.on(EventConstants.EVENT_CALL_START) {args: Array<Any?>? ->
                Timber.d("TAG: call start: %s", args?.get(0))
            }

            socket?.on(EventConstants.EVENT_CALL_RESUME_START) {args: Array<Any?>? ->
                Timber.d("TAG: call resume: %s", args?.get(0))
            }

            socket?.on(EventConstants.EVENT_INTERVIEW_CALL_RECIEVE) {args: Array<Any?>? ->
                Timber.d("TAG: interview recieve: %s", args?.get(0))
            }

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

            socket?.on(Socket.EVENT_DISCONNECT) {
                signalingEvent.onEventDisconnected()
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> ->
                signalingEvent.onEventConnectionError(args)
            }

            Timber.d("TAG: Connecting ... $socket")

            socket?.connect()

        } catch (e: Exception) {
            Timber.d("TAG: Exception : $e")
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
        val sendingSDPDescription = JSONObject()

        try {
            sendingSDPDescription.put("type", sessionDescription.type?.canonicalForm())
            sendingSDPDescription.put("sdp", sessionDescription.description)
            sendingSDP.put("description", sendingSDPDescription)
            sendingSDP.put("to", remoteSocketID)
            sendingSDP.put("sender", localSocketID)

        } catch (e: Exception) {
            Timber.d("TAG:SignallingServer: sendingSDP error - $e")
        }
        Timber.d("TAG:sendingSDP string - $sendingSDP")

        socket?.emit(EventConstants.EVENT_SDP, sendingSDP)
    }

    fun sendIceCandidate(iceCandidate: IceCandidate?) {

        val sendingICE = JSONObject()
        val sendingIceCandidate = JSONObject()

        try {
            sendingICE.put("candidate", iceCandidate?.sdp)
            sendingICE.put("sdpMid", iceCandidate?.sdpMid)
            sendingICE.put("sdpMLineIndex", iceCandidate?.sdpMLineIndex)
            sendingIceCandidate.put("candidate", sendingICE)
            sendingIceCandidate.put("to", remoteSocketID)
            sendingIceCandidate.put("sender", localSocketID)

            Timber.d("TAG:onEmit IceCandidateJO $sendingIceCandidate")

        } catch (e: JSONException) {
            Timber.d("TAG:onEmit IceCandidate: JSONException $e")
            e.printStackTrace()
        }
        socket?.emit(EventConstants.EVENT_ICE_CANDIDATES, sendingIceCandidate)

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

}