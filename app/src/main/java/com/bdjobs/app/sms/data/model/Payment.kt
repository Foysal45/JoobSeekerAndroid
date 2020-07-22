package com.bdjobs.app.sms.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json


@Keep
data class PaymentInfoBeforeGateway(
        @Json(name = "common")
        val common: Any?, // null
        @Json(name = "data")
        val `data`: List<PaymentInfoBeforeGatewayData>?,
        @Json(name = "message")
        val message: String?, // Success
        @Json(name = "statuscode")
        val statuscode: String? // 0
) {
    @Keep
    data class PaymentInfoBeforeGatewayData(
            @Json(name = "serviceId")
            var serviceId: String? = "", // 78
            @Json(name = "smsSubscribedId")
            var smsSubscribedId: String? = "", // 2134
            @Json(name = "totalAmount")
            var totalAmount: String? = "",// 50
            @Json(name = "totalQuantity")
            var totalQuantity: String? = "", // 100
            @Json(name = "transactionId")
            var transactionId: String? = "", // 200721241028T8B8Rx66
            @Json(name = "userEmail")
            var userEmail: String? = "", // mnsbbb@yahoo.com
            @Json(name = "userFullName")
            var userFullName: String? = "", // Rahim Islam
            @Json(name = "userMobileNo")
            var userMobileNo: String? = ""// 01878130561
    )
}