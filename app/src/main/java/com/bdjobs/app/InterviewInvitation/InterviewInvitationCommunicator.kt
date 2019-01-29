package com.bdjobs.app.InterviewInvitation

interface InterviewInvitationCommunicator {
    fun backButtonClicked()
    fun goToInvitationDetails(jobID: String, companyName: String, jobTitle: String)
    fun goToInvitationDetailsForAppliedJobs(jobID: String, companyName: String, jobTitle: String)
    fun goToVenueDirection(invitationDate: String, invitationTime: String, venue: String, lat: String, lan: String)
    fun getCompanyJobID(): String
    fun getCompanyJobTitle(): String
    fun getCompanyNm(): String
    fun getInviteTime(): String
    fun getInviteDate(): String
    fun getCompanyVenue(): String
    fun getFrom(): String
    fun getCompanyLat(): String
    fun getCompanyLan(): String
    fun getTime() : String
}