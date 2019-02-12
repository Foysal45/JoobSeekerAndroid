package com.bdjobs.app.API.ModelClasses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ADDorUpdateModel {


    @SerializedName("messgae")
    @Expose
    var messgae: String? = null

    @SerializedName("path")
    @Expose
    var path: String? = null

    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("messageType")
    @Expose
    var messageType: String? = null
    @SerializedName("isResumeUpdate")
    @Expose
    var isResumeUpdate: String? = null
    @SerializedName("isCvPosted")
    @Expose
    var isCvPosted: String? = null

}
