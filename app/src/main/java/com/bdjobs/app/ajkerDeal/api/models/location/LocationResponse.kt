package com.bdjobs.app.ajkerDeal.api.models.location


import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("DistrictInfo")
    var districtInfo: List<DistrictInfoModel> = listOf(),
    @SerializedName("SpecialDeliveryThana")
    var specialDeliveryThana: List<String> = listOf(),
    @SerializedName("Area")
    var area: List<AreaInfoModel> = listOf(),
    @SerializedName("ThanaShundarban")
    var thanaShundarban: List<ThanaShundarbanModel> = listOf()
)