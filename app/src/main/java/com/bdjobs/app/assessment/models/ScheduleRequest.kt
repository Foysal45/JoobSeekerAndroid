package com.bdjobs.app.assessment.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class ScheduleRequest(
        var userId : String? = "",
        var decodeId : String? = "",
        var pageNo : String? = "",
        var pageSize : String? = "",
        var fromDate : String? = "",
        var toDate : String? = "",
        var venue : String? = ""
) : Parcelable