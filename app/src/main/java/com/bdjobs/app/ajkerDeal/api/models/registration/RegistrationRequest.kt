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
    var isSignUpByLivePlaza: Int = 5,
    @SerializedName("FacebookPageUrl")
    var facebookPageUrl: String? = "",
    @SerializedName("AltMobile")
    var altMobile: String? = "",
    @SerializedName("Age")
    var age: Int? = 0,
    @SerializedName("CurrentSalary")
    var currentSalary:Int? = 0,
    @SerializedName("ExpectedSalary")
    var expectedSalary:Int? = 0,
    @SerializedName("LocationName")
    var locationName:String? = "",
    @SerializedName("KnowingSource")
    var knowingSource:String? = "",

)
