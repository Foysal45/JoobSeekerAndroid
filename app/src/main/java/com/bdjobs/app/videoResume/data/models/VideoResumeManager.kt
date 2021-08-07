package com.bdjobs.app.videoResume.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.io.File

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class VideoResumeManager (
        var slNo : String? = null,
        var questionSerialNo : String? = null,
        var questionId : String? = null,
        var questionText : String? = null,
        var questionTextBng : String? = null,
        var questionDuration : String? = null,
        var answerHintBn : String? = null,
        var answerHintEn : String? = null,
        var aID : String? = null,
        var totalView : String? = null,
        var file : File? = null
) : Parcelable