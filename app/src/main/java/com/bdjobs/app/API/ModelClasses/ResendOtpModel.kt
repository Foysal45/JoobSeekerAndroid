package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ResendOtpModel(@SerializedName("statuscode")
                          val statuscode: String = "",
                          @SerializedName("isResumeUpdate")
                          val isResumeUpdate: String = "",
                          @SerializedName("data")
                          val data: String = "",
                          @SerializedName("common")
                          val common: String = "",
                          @SerializedName("message")
                          val message: String = "")