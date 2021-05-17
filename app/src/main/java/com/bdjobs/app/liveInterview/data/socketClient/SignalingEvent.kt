package com.bdjobs.app.liveInterview.data.socketClient

//
// Created by Soumik on 4/29/2021.
// piyal.developer@gmail.com
//

interface SignalingEvent {

    fun onEventConnected()
    fun onEventIPADDR()
    fun onEventCreated(args: Array<Any?>?)
    fun onEventFull()
    fun onEventJoin(args: Array<Any>)
    fun onEventJoined(args: Array<Any>)
    fun onEventLog(args: Array<Any>)
    fun onEventMessage(args: Array<Any>)
    fun onEventMessage2(args: Array<Any?>?)
    fun onEventDisconnected()
    fun onEventConnectionError(args: Array<Any>)

    // chatting socket

    fun onUserJoined(args: Array<Any>)
    fun onUserDisconnected(args: Array<Any>)
    fun onMessageReceived(args: Array<Any>)

}