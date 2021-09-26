package com.bdjobs.app.ajkerDeal.api.models.registration

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("Mobile")
    var mobile: String? = "",
    @SerializedName("Password")
    var password: String? = "",
    @SerializedName("Name")
    var name: String? = "",
    @SerializedName("DeviceId")
    var deviceId: String? = "",
    @SerializedName("FireBaseToken")
    var fireBaseToken: String? = "",
    @SerializedName("Address")
    var address: String? = "",
    @SerializedName("DistrictId")
    var districtId: Int = 0,
    @SerializedName("Email")
    var email: String? = "",
    @SerializedName("Gender")
    var gender: String? = "",
    @SerializedName("LocationId")
    var locationId: Int = 0,
    @SerializedName("IsSignUpByLivePlaza")
    var isSignUpByLivePlaza: Int = 0,
    @SerializedName("FacebookPageUrl")
    var facebookPageUrl: String? = ""
)
