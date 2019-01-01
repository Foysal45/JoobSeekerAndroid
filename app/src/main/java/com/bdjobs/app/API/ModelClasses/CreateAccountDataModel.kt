package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.SerializedName

data class CreateAccountDataModel(
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
        val userPicUrl: String?,
        @SerializedName("tmpId")
        val tmpId: String?
)