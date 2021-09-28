package com.bdjobs.app.ajkerDeal.api.models.firebase_models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseCredential(
    var projectId: String = "",
    var applicationId: String = "",
    var apikey: String = "",
    var databaseUrl: String = "",
    var storageUrl: String = "",
    var firebaseWebApiKey: String = ""
): Parcelable
