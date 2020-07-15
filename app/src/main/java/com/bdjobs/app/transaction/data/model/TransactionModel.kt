package com.bdjobs.app.transaction.data.model
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@Keep
@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "common")
    val common: Common?,
    @Json(name = "data")
    val `data`: List<TransactionData>?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "statuscode")
    val statuscode: String?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Common(
            @Json(name = "TotalTransaction")
            val totalTransaction: String?
    )


    @Keep
    @JsonClass(generateAdapter = true)
    data class TransactionData(
            @Json(name = "amount")
            val amount: String?,
            @Json(name = "packageName")
            val packageName: String?,
            @Json(name = "paymentMethod")
            val paymentMethod: String?,
            @Json(name = "purchasedDate")
            val purchasedDate: String?
    )

}




