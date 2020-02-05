package com.bdjobs.app.assessment.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
data class Home(
    @Json(name = "common")
    var common: Any?,
    @Json(name = "data")
    var data: List<HomeData?>?,
    @Json(name = "message")
    var message: String?,
    @Json(name = "statuscode")
    var statuscode: String?
)

@Keep
data class HomeData(
        @Json(name = "btn_cancel")
        var btnCancel: String?,
        @Json(name = "btn_change")
        var btnChange: String?,
        @Json(name = "center_address")
        var centerAddress: String?,
        @Json(name = "isExpired")
        var isExpired: String?,
        @Json(name = "isFromHome")
        var isFromHome: String?,
        @Json(name = "is_proceed_for_new_test")
        var isProceedForNewTest: String?,
        @Json(name = "isTestFree")
        var isTestFree: String?,
        @Json(name = "isUser_firstTime_in_assessmentPanel")
        var isUserFirstTimeInAssessmentPanel: String?,
        @Json(name = "is_user_permitted_for_schld_booking")
        var isUserPermittedForSchldBooking: String?,
        @Json(name = "payment_status")
        var paymentStatus: String?,
        @Json(name = "resume_test_btn_format")
        var resumeTestBtnFormat: String?,
        @Json(name = "test_center")
        var testCenter: String?,
        @Json(name = "test_date")
        var testDate: String?,
        @Json(name = "testFee")
        var testFee: String?,
        @Json(name = "test_time")
        var testTime: String?
)