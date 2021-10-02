package com.bdjobs.app.Employers

import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.MessageDataModel

interface EmployersCommunicator {
    fun backButtonPressed()
    fun gotoJobListFragment(companyID : String?, companyName : String?)
    fun getCompanyID() : String
    fun getCompanyName() : String
    fun getJobId() : String
    fun getTime() : String
    fun scrollToUndoPosition(position:Int)
    fun decrementCounter(position:Int)
    fun setMessageId(messageId: String)
    fun getMessageId ():String
    fun gotoMessageDetail()
    fun positionClicked(position:Int?)
    fun getPositionClicked():Int?
    fun setFollowedEmployerList( empList:ArrayList<FollowEmployerListData>?)
    fun getFollowedEmployerList():ArrayList<FollowEmployerListData>?

    fun setCurrentPage(value:Int?)
    fun setTotalPage(value:Int?)
    fun setIsloading(value:Boolean?)
    fun setIsLastPage(value:Boolean?)
    fun setFollowedListSize(value:Int?)

    fun getCurrentPage():Int?
    fun getTotalPage():Int?
    fun getIsloading():Boolean?
    fun getIsLastPage():Boolean?
    fun getFollowedListSize():Int?

    //Added by Rakib
    fun getEmployerMessageList() : ArrayList<MessageDataModel>?
    fun setEmployerMessageList(employerMessageList : ArrayList<MessageDataModel>?)
    fun getTotalRecords(): Int?
    fun setTotalRecords(value:Int?)
    fun getTotalFollowedEmployersCount():Int
    fun setTotalFollowedEmployersCount(count: Int)


}