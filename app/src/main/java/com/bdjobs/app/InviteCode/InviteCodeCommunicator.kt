package com.bdjobs.app.InviteCode

interface InviteCodeCommunicator {

    fun getInviteCodeUserType():String?
    fun getInviteCodepcOwnerID():String?
    fun getInviteCodeStatus():String?
    fun backButtonClicked()
}