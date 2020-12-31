package com.bdjobs.app.videoResume.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Question(
        var serial : Int,
        var title : String,
        var time : String,
        var isNew : Boolean,
        var isSubmitted : Boolean
) : Parcelable