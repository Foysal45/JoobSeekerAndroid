package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName


data class LoginUserModel(
        @SerializedName("statuscode") val statuscode: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: List<DataUserModel?>?,
        @SerializedName("common") val common: Any?
)

data class Login2UserModel(
        @SerializedName("messageType") val messageType: String?,
        @SerializedName("userFullName") val userFullName: String?,
        @SerializedName("userId") val userId: String?,
        @SerializedName("imageurl") val imageUrl: String?,
        @SerializedName("isBlueCollar") val isBlueCollar: String?
)

data class DataUserModel(
        @SerializedName("userFullName") val userFullName: String?,
        @SerializedName("userId") val userId: String?,
        @SerializedName("imageurl") val imageurl: String?,
        @SerializedName("isBlueCollar") val isBlueCollar: String?
)

data class LoginSessionModel(
        @SerializedName("common")
        val common: Any?,
        @SerializedName("data")
        val `data`: List<DataLoginPasswordModel?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class DataLoginPasswordModel(
        @SerializedName("AppsDate")
        val appsDate: String?,
        @SerializedName("IsResumeUpdate")
        val isResumeUpdate: String?,
        @SerializedName("age")
        val age: String?,
        @SerializedName("catagoryId")
        val catagoryId: String?,
        @SerializedName("decodId")
        val decodId: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("exp")
        val exp: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("isCvPosted")
        val isCvPosted: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("resumeUpdateON")
        val resumeUpdateON: String?,
        @SerializedName("trainingId")
        val trainingId: String?,
        @SerializedName("userId")
        val userId: String?,
        @SerializedName("userName")
        val userName: String?,
        @SerializedName("userPicUrl")
        val userPicUrl: String?
)

data class SocialLoginAccountListModel(
        @SerializedName("common")
        val common: SocialLoginAccountListDataCommon?,
        @SerializedName("data")
        val `data`: List<SocialLoginAccountListData?>?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("statuscode")
        val statuscode: String?
)

data class SocialLoginAccountListData(
        @SerializedName("decodId")
        val decodId: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("fullName")
        val fullName: String?,
        @SerializedName("isMap")
        val isMap: String?,
        @SerializedName("photo")
        val photo: String?,
        @SerializedName("smId")
        val smId: String?,
        @SerializedName("socialMediaId")
        val socialMediaId: String?,
        @SerializedName("socialMediaName")
        val socialMediaName: String?,
        @SerializedName("susername")
        val susername: String?,
        @SerializedName("total")
        val total: String?,
        @SerializedName("userId")
        val userId: String?
)

data class SocialLoginAccountListDataCommon(
        @SerializedName("isMap")
        val isMap: String?,
        @SerializedName("total")
        val total: String?
)
