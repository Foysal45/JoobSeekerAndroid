package com.bdjobs.app.videoInterview.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class VideoManager (
        var jobId : String? = null,
        var applyId : String? = null,
        var questionId : String? = null,
        var questionSerial : String? = null,
        var questionText : String? = null,
        var questionDuration : String? = null,
        var totalQuestion : Int? = null,
        var file : File? = null
) : Parcelable