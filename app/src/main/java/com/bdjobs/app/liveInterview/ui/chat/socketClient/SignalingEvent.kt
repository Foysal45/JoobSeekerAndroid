package com.bdjobs.app.liveInterview.ui.chat.socketClient

//
// Created by Soumik on 4/29/2021.
// piyal.developer@gmail.com
//

interface SignalingEvent {

    fun onEventConnected()
    fun onEventIPADDR()
    fun onEventCreated()
    fun onEventFull()
    fun onEventJoin()
    fun onEventJoined()
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