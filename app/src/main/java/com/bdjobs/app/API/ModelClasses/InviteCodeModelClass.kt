package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InviteCodeHomeModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeHomeModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeHomeModelData(
        @SerializedName("inviteCodeStatus")
        val inviteCodeStatus: String = "",
        @SerializedName("pcOwnerID")
        val pcOwnerID: String = "",
        @SerializedName("userType")
        val userType: String = ""
)

@Keep
data class OwnerInviteCodeModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<OwnerInviteCodeModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class OwnerInviteCodeModelData(
        @SerializedName("inviteCode")
        val inviteCode: String = ""
)

@Keep
data class InviteCodeCategoryAmountModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeCategoryAmountModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeCategoryAmountModelData(
        @SerializedName("categoryName")
        val categoryName: String = "",
        @SerializedName("ownerAmount")
        val ownerAmount: String = ""
)

@Keep
data class OwnerInviteListModel(
        @SerializedName("common")
        val common: OwnerInviteListModelCommon = OwnerInviteListModelCommon(),
        @SerializedName("data")
        val `data`: List<OwnerInviteListModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class OwnerInviteListModelData(
        @SerializedName("category")
        val category: String = "",
        @SerializedName("created_date")
        val createdDate: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("ownerAmount")
        val ownerAmount: String = "",
        @SerializedName("paidStatus")
        val paidStatus: String = "",
        @SerializedName("photoUrl")
        val photoUrl: String = "",
        @SerializedName("userID")
        val userID: String = "",
        @SerializedName("verifyStatus")
        val verifyStatus: String = ""
)
@Keep
data class OwnerInviteListModelCommon(
        @SerializedName("totalInvitation")
        val totalInvitation: String = "",
        @SerializedName("total_account")
        val totalAccount: String = ""
)

@Keep
data class InviteCodeOwnerStatementModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeOwnerStatementModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeOwnerStatementModelData(
        @SerializedName("amount")
        val amount: String = "",
        @SerializedName("balance")
        val balance: String = "",
        @SerializedName("date")
        val date: String = "",
        @SerializedName("type")
        val type: String = ""
)
@Keep
data class InviteCodeUserStatusModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeUserStatusModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeUserStatusModelData(
        @SerializedName("Amount")
        val amount: String = "",
        @SerializedName("category")
        val category: String = "",
        @SerializedName("created_date")
        val createdDate: String = "",
        @SerializedName("educationInfo")
        val educationInfo: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("paidStatus")
        val paidStatus: String = "",
        @SerializedName("personalInfo")
        val personalInfo: String = "",
        @SerializedName("photoInfo")
        val photoInfo: String = "",
        @SerializedName("photoUrl")
        val photoUrl: String = "",
        @SerializedName("skills")
        val skills: String = "",
        @SerializedName("verifiedStatus")
        val verifiedStatus: String = ""
)
@Keep
data class InviteCodePaymentMethodModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeOwnerPaymentIMethodModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeOwnerPaymentIMethodModelData(
        @SerializedName("accountNo")
        val accountNo: String = "",
        @SerializedName("isExist")
        val isExist: String = "",
        @SerializedName("paymentType")
        val paymentType: String = ""
)

@Keep
data class InviteCodeBalanceModel(
        @SerializedName("common")
        val common: Any = Any(),
        @SerializedName("data")
        val `data`: List<InviteCodeBalanceModelData> = listOf(),
        @SerializedName("message")
        val message: String = "",
        @SerializedName("statuscode")
        val statuscode: String = ""
)
@Keep
data class InviteCodeBalanceModelData(
        @SerializedName("totalPayable")
        val totalPayable: String = "",
        @SerializedName("total_earned")
        val totalEarned: String = "",
        @SerializedName("total_withDraw")
        val totalWithDraw: String = "",

        @SerializedName("las_withDraw_date")
        val las_withDraw_date: String = ""
)

@Keep
data class PaymentTypeInsertModel(
    @SerializedName("common")
    val common: Any = Any(),
    @SerializedName("data")
    val `data`: List<PaymentTypeInsertModelData> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("statuscode")
    val statuscode: String = ""
)
@Keep
data class PaymentTypeInsertModelData(
    @SerializedName("inserted")
    val inserted: String = ""
)
@Keep
data class InviteCodeUserVerifyModel(
    @SerializedName("common")
    val common: Any = Any(),
    @SerializedName("data")
    val `data`: List<InviteCodeUserVerifyModelData> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("statuscode")
    val statuscode: String = ""
)
@Keep
data class InviteCodeUserVerifyModelData(
    @SerializedName("Amount")
    val amount: String = "",
    @SerializedName("Eligible")
    val eligible: String = ""
)