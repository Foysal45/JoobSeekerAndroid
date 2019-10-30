package com.bdjobs.app.Notification

interface NotificationCommunicatior {
    fun backButtonPressed()
    fun goToNotificationListFragment()
    fun goToMessageListFragment()
    fun positionClicked(item :Int)
    fun getPositionClicked(): Int
    fun positionClickedMessage(item: Int)
    fun getPositionClickedMessage() : Int
}