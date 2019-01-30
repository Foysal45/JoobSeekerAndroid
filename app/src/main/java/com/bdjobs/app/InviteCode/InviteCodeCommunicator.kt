package com.bdjobs.app.InviteCode

interface InviteCodeCommunicator {

    fun getInviteCodeUserType():String?
    fun getInviteCodepcOwnerID():String?
    fun getInviteCodeStatus():String?
    fun backButtonClicked()
    fun goToPaymentMethod(paymentMethod:String="",accountNumber:String="",fromInviteCodeSubmitPage:Boolean=false)
    fun getPaymentMethod():String?
    fun getAccountNumber():String?
    fun getfromInviteCodeSubmitPage():Boolean
}