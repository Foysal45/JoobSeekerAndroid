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

    private var socket:Socket?=null
    private var localSocketID:String = ""
    private var remoteSocketID:String = ""
    private var activeUser:String = ""

    private var processId:String = ""
    private var userName:String = ""
    private var serverURL:String = "https://live.bdjobs.com/"

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

    fun init(signalingEvent: SignalingEvent, processId:String, userName:String) {
        try {
            this.processId = processId
            this.userName = userName
            socket = IO.socket(serverURL)

            socket?.on(EventConstants.EVENT_SOCKET_CONNECT) {
                localSocketID = socket?.id().toString()

                Timber.tag("live").d("Local socket id $localSocketID")

                signalingEvent.setLocalSocketID(localSocketID)

                val subscribeJO = JSONObject()

                try {
                    subscribeJO.put("room", processId)
                    subscribeJO.put("socketId", localSocketID)
                    subscribeJO.put("username", userName)
                    subscribeJO.put("uType", "A")
                    socket?.emit(EventConstants.EVENT_SUBSCRIBE, subscribeJO)
                    Timber.tag("live").d("onEmit subscribe: jsonObject $subscribeJO")

                } catch (e: JSONException) {
                    Timber.tag("live").d("subscribe: JSONException $e")
                    e.printStackTrace()
                }
            }

            socket?.on(EventConstants.EVENT_PARTICIPANT_COUNT) {args: Array<Any?>? ->
                Timber.tag("live").d("participantCount: %s", args?.get(0))
//                val message = args?.get(0) as JSONObject
//                Timber.tag("live").d("participant string - ${message.getString("participant")}")
            }

            socket?.on(EventConstants.EVENT_1ST_USER_CHECK) {args: Array<Any?>? ->
                val argument = args?.get(0) as JSONObject
                remoteSocketID = argument.getString("firstUserId")
                signalingEvent.on1stUserCheck(args)
            }

            socket?.on(EventConstants.EVENT_NEW_USER) {args: Array<Any?>? ->
                val argument = args?.get(0) as JSONObject
                remoteSocketID = argument.getString("socketId")
                activeUser = argument.getString("activeUserCount")
                signalingEvent.onNewUser(args)
                sendApplicantId()
                // temp solution if browser reload , infinite loop on back navigation
//                socket?.disconnect()
//                socket?.connect()
            }
            socket?.on(EventConstants.EVENT_NEW_USER_START_NEW) {args: Array<Any?>? ->
                val argument = args?.get(0) as JSONObject
                remoteSocketID = argument.getString("sender")
                signalingEvent.onNewUserStartNew(args)
               // sendReInitRequest()
            }

            socket?.on(EventConstants.EVENT_SEND_RELOAD_TO_FIRST_USER) {args: Array<Any?>? ->
                Timber.tag("live").d("send reload to first: %s", args?.get(0))
            }

            socket?.on(EventConstants.EVENT_CALL_START) {args: Array<Any?>? ->
                signalingEvent.onReceiveCall(args)
                sendInterviewReceived()
            }

            socket?.on(EventConstants.EVENT_CALL_RESUME_START) {args: Array<Any?>? ->
                Timber.tag("live").d("call resume: %s", args?.get(0))
            }

            socket?.on(EventConstants.EVENT_INTERVIEW_CALL_RECIEVE) {args: Array<Any?>? ->
                Timber.tag("live").d("interview recieve: %s", args?.get(0))
               // signalingEvent.onInterviewReceive(args)
            }

            socket?.on(EventConstants.EVENT_ICE_CANDIDATES) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onReceiveIceCandidate(args)
                }
            }

//            socket?.on(EventConstants.EVENT_INTERVIEW_CALL_RECIEVE) { args: Array<Any?>? ->
//                if (args != null) {
//                    signalingEvent.onReceiveIceCandidate(args)
//                }
//            }

            socket?.on(EventConstants.EVENT_INTERVIEW_CALL_END) { args: Array<Any?>? ->
                Timber.tag("live").d("call end: %s", args?.get(0))
                sendCallHasEnded()
                signalingEvent.onEndCall()
            }

            socket?.on(EventConstants.EVENT_END_LOCAL) { args: Array<Any?>? ->
                Timber.tag("live").d("end local: %s", args?.get(0))
            }

            socket?.on(EventConstants.EVENT_SDP) { args: Array<Any?>? ->
                if (args != null) {
                    signalingEvent.onReceiveSDP(args)
                }
            }

            socket?.on(EventConstants.EVENT_CHAT) { args: Array<Any?>? ->
                Timber.tag("live").d("chat: %s", args?.get(0))
                signalingEvent.onReceiveChat(args)
            }
            socket?.on(EventConstants.EVENT_SEEN_MSG) { args: Array<Any?>? ->
                Timber.tag("live").d("seen message: %s", args?.get(0))
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                Timber.tag("live").d("Socket Disconnected")
                signalingEvent.onEventDisconnected()
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args: Array<Any> ->
                signalingEvent.onEventConnectionError(args)
            }

            Timber.tag("live").d("Connecting ... $socket")

            socket?.connect()

        } catch (e: Exception) {
            Timber.tag("live").d("Exception : $e")
            e.printStackTrace()
        }
    }

    fun destroy() {
        socket?.disconnect()
        socket?.close()

    }

    fun sendInterviewReceived(){
        val sendInterviewReceived = JSONObject()

        try {
            sendInterviewReceived.put("local", localSocketID )

        } catch (e: Exception) {
            Timber.tag("live").d("SignallingServer: sendInterviewRecieved error - $e")
        }
        Timber.tag("live").d("sendingCallHasEnded string - $sendInterviewReceived")

        socket?.emit(EventConstants.EVENT_INTERVIEW_CALL_RECIEVE, sendInterviewReceived)
    }

    fun sendCallHasEnded(){
        val sendingCallHasEnded = JSONObject()

        try {
            sendingCallHasEnded.put("local", remoteSocketID )
            sendingCallHasEnded.put("sender", localSocketID)

        } catch (e: Exception) {
            Timber.tag("live").d("SignallingServer: sendingCallHasEnded error - $e")
        }
        Timber.tag("live").d("sendingCallHasEnded string - $sendingCallHasEnded")

        socket?.emit(EventConstants.EVENT_END_LOCAL, sendingCallHasEnded)
    }

    fun sendReInitRequest() {

        val sendingReInit = JSONObject()

        try {
            sendingReInit.put("to", remoteSocketID )
            sendingReInit.put("sender", localSocketID)
            sendingReInit.put("isReInit", "true")

        } catch (e: Exception) {
            Timber.tag("live").d("SignallingServer: sendingReInit error - $e")
        }
        Timber.tag("live").d("sendingId string - $sendingReInit")

        socket?.emit(EventConstants.EVENT_RE_INIT, sendingReInit)
    }

    fun sendApplicantId() {

        val sendingId = JSONObject()

        try {
            sendingId.put("to", remoteSocketID )
            sendingId.put("sender", localSocketID)
            sendingId.put("ActiveUser", activeUser)
            sendingId.put("isloginuser", "true")
            sendingId.put("activeUserCount", activeUser)

        } catch (e: Exception) {
            Timber.tag("live").d("SignallingServer: sendingId error - $e")
        }
        Timber.tag("live").d("sendingId string - $sendingId")

        socket?.emit(EventConstants.EVENT_NEW_USER_START_NEW, sendingId)
    }

    fun sendReload(){
        val sendReload = JSONObject()

        try {
            sendReload.put("socketId", localSocketID )
            sendReload.put("userType", "A")
            sendReload.put("activeUserCount", activeUser)
        } catch (e: Exception) {
            Timber.tag("live").d("SignallingServer: sendReload error - $e")
        }
        Timber.tag("live").d("sendReload string - $sendReload")

        socket?.emit(EventConstants.EVENT_NEW_USER, sendReload)
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
            Timber.tag("live").d("SignallingServer: sendingSDP error - $e")
        }
        Timber.tag("live").d("sendingSDP string - $sendingSDP")

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

            Timber.tag("live").d("onEmit IceCandidateJO $sendingIceCandidate")

        } catch (e: JSONException) {
            Timber.tag("live").d("onEmit IceCandidate: JSONException $e")
            e.printStackTrace()
        }
        socket?.emit(EventConstants.EVENT_ICE_CANDIDATES, sendingIceCandidate)

    }

    fun sendChatMessage(message:String, imageLocal: String, imageRemote: String, messageCount: Int) {
        val sendingChat = JSONObject()

        try {
            sendingChat.put("room", processId)
            sendingChat.put("msg", message)
            sendingChat.put("sender", userName)
            sendingChat.put("imgLocal", imageLocal)
            sendingChat.put("imgRemote", imageRemote)
            sendingChat.put("newCount", messageCount)

            Timber.tag("live").d("onEmit sendingChatJO $sendingChat")

        } catch (e: JSONException) {
            Timber.tag("live").d("onEmit sendingChatJO: JSONException $e")
            e.printStackTrace()
        }
        socket?.emit(EventConstants.EVENT_CHAT, sendingChat)

    }

}