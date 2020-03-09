package com.bdjobs.app.assessment.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Booking (
        var userId : String? = "",
        var decodeId : String? = "",
        var strActionType : String? = "",
        var scId : String? = "",
        var schId : String? = "",
        var opId : String? = "",
        var fltBdjAmount : String? = "",
        var strTransactionDate : String? = "",
        var isFromHome : String? = ""
) : Parcelable