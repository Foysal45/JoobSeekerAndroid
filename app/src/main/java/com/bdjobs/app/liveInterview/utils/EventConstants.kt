package com.bdjobs.app.liveInterview.utils

//
// Created by Soumik on 5/7/2021.
// piyal.developer@gmail.com
//

object EventConstants {

    const val EVENT_SOCKET_CONNECT = "connect"
    const val EVENT_SUBSCRIBE = "subscribe"
    const val EVENT_PARTICIPANT_COUNT = "participantCount"
    const val EVENT_NEW_USER = "new user"
    const val EVENT_1ST_USER_CHECK = "1stUsercheck"
    const val EVENT_SEND_RELOAD_TO_FIRST_USER = "sendReloadTo1stUser"
    const val EVENT_NEW_USER_START_NEW = "newUserStartNew"
    const val EVENT_ICE_CANDIDATES = "ice candidates"
    const val EVENT_SDP = "sdp"
    const val EVENT_CHAT = "chat"
    const val EVENT_SEEN_MSG = "seenMsg"
    const val EVENT_CALL_START = "callStart"
    const val EVENT_CALL_RESUME_START = "callResumeStart"
    const val EVENT_INTERVIEW_CALL_END = "interviewcallEnd"
    const val EVENT_INTERVIEW_CALL_RECIEVE = "Interviewreceive"
    const val EVENT_APPLICANT_STATUS = "applicantStatus"
    const val EVENT_END_LOCAL = "endlocal"
    const val EVENT_RE_INIT = "reinit"
    const val EVENT_INACTIVE_USER = "inactiveUser"

}