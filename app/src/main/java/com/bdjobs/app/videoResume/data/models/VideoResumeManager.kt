package com.bdjobs.app.videoResume.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class VideoResumeManager (
        var slNo : String? = null,
        var questionSerialNo : String? = null,
        var questionId : String? = null,
        var questionText : String? = null,
        var questionDuration : String? = null,
        var aID : String? = null,
        var totalView : String? = null,
        var file : File? = null
) : Parcelable