package com.bdjobs.app.liveInterview.data.socketClient

//
// Created by Soumik on 4/29/2021.
// piyal.developer@gmail.com
//

interface SignalingEvent {

    fun onEventDisconnected()
    fun onEventConnectionError(args: Array<Any>)

    //According to Web
    fun setLocalSocketID(id:String)
    fun on1stUserCheck(args: Array<Any?>?)
    fun onNewUser(args: Array<Any?>?)
    fun onNewUserStartNew(args: Array<Any?>?)
    fun onReceiveIceCandidate(args: Array<Any?>?)
    fun onReceiveCall(args: Array<Any?>?)
    fun onReceiveSDP(args: Array<Any?>?)
    fun onEndCall()

    // chatting socket
    fun onReceiveChat(args: Array<Any?>?)


}