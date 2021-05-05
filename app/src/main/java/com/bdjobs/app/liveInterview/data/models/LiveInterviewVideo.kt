package com.bdjobs.app.liveInterview.data.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

//
// Created by Soumik on 5/4/2021.
// piyal.developer@gmail.com
//

@Parcelize
data class LiveInterviewVideo (
        var file: File? = null,
        var path: String? = null,
        var absolutePath:String? = null,
        var uri:Uri? = null
): Parcelable