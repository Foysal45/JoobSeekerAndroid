package com.bdjobs.app.ajkerDeal.api.endpoints

import com.example.livevideoshopping.api.models.location.DistrictInfoModel
import com.example.livevideoshopping.api.models.location.LocationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationInterface {

    @GET("District/v3/LoadAllDistrictFromJson/{parentId}")
    fun getDistrictList(@Path("parentId") distId: Int = 0): Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>

    @GET("District/v3/LoadAllDistrictFromJson/{parentId}/{districtId}/{isCourier}")
    fun getThanaOrAreaList(
        @Path("parentId") parentid: Int,
        @Path("districtId") districtId: Int,
        @Path("isCourier") isCourier: Int = 0
    ): Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>

    @GET("/District/GetDistrictsForMerchantDeliveryZones/{merchantId}/{districtId}/{thanaId}")
    fun getDistrictList(
        @Path("merchantId") merchantId: Int = 0,
        @Path("districtId") distId: Int = 0,
        @Path("thanaId") thanaId: Int = 0
    ): Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>

    @GET("/District/GetDistrictOptions/{districtId}/{thanaId}/{areaId}")
    fun getDistrictOption(
        @Path("districtId") districtId: Int = 0,
        @Path("thanaId") thanaId: Int = 0,
        @Path("areaId") areaId: Int = 0
    ): Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<DistrictInfoModel>>
}