package com.bdjobs.app.videoResume.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class Question(
        var serial : String,
        var title : String,
        var time : String,
        var isNew : Boolean,
        var isSubmitted : Boolean
) : Parcelable