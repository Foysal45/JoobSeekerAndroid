package com.bdjobs.app.ajkerDeal.api.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageInfo(
    @SerializedName("ImageCount")
    var imageCount: Int = 0,
    @SerializedName("LargeImagesLink")
    var largeImagesLink: List<String> = listOf(),
    @SerializedName("SmallImagesLink")
    var smallImagesLink: List<String> = listOf()
) : Parcelable