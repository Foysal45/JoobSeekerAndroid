package com.bdjobs.app.API.ModelClasses

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PhotoInfoModel(
        @SerializedName("common")
        val common: Any?="",
        @SerializedName("data")
        val `data`: List<PhotoInfoDataModel>? = listOf(),
        @SerializedName("message")
        val message: String?="",
        @SerializedName("statuscode")
        val statuscode: String?=""
)
@Keep
data class PhotoInfoDataModel(
        @SerializedName("FolderId")
        val folderId: String?="",
        @SerializedName("FolderName")
        val folderName: String?="",
        @SerializedName("ImageName")
        val imageName: String?="",
        @SerializedName("IsResumeUpdate")
        val isResumeUpdate: String?="",
        @SerializedName("decodId")
        val decodId: String?="",
        @SerializedName("userId")
        val userId: String?=""
)
@Keep
data class PhotoUploadModel(
        @SerializedName("common")
        val common: String,
        @SerializedName("data")
        val `data`: List<PhotoUploadDataModel>,
        @SerializedName("message")
        val message: String,
        @SerializedName("statuscode")
        val statuscode: String
)
@Keep
data class PhotoUploadDataModel(
        @SerializedName("path")
        val path: String?=""
)
@Keep
data class PhotoUploadResponseModel(
        @SerializedName("common")
        val common: Any?="",
        @SerializedName("data")
        val `data`: List<PhotoUploadResponseData>?= listOf(),
        @SerializedName("message")
        val message: String?="",
        @SerializedName("statuscode")
        val statuscode: String?=""
)
@Keep
data class PhotoUploadResponseData(
        @SerializedName("decodeId")
        val decodeId: String?="",
        @SerializedName("folderId")
        val folderId: String?="",
        @SerializedName("folderName")
        val folderName: String?="",
        @SerializedName("imageName")
        val imageName: String?="",
        @SerializedName("isResumeUpdate")
        val isResumeUpdate: String?="",
        @SerializedName("path")
        val path: String?="",
        @SerializedName("userid")
        val userid: String?=""
)