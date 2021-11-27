package com.bdjobs.app.ajkerDeal.api

import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogRequest
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderRequest
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderResponse
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListRequest
import com.bdjobs.app.ajkerDeal.api.models.live_order_management.LiveOrderManagementResponseBody
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductRequest
import com.bdjobs.app.ajkerDeal.api.models.order.DeliveryInfoModel
import com.bdjobs.app.ajkerDeal.api.models.registration.RegistrationRequest
import com.bdjobs.app.ajkerDeal.api.models.registration.RegistrationResponse
import com.bdjobs.app.ajkerDeal.api.models.video_comments.VideoInsertCommentsRequest
import com.bdjobs.app.ajkerDeal.api.models.video_comments.VideoInsertReplyCommentsRequest
import com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments.VideoCommentsModel
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*

interface ApiInterfaceAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit): ApiInterfaceAPI {
            return retrofit.create(ApiInterfaceAPI::class.java)
        }
    }

    @POST("api/videoshopping/LoadAllHandPickLives")
    suspend fun fetchHandPickLives(@Body requestBody: LiveListRequest): NetworkResponse<ResponseHeader<List<LiveListData>>, ErrorResponse>


    @POST("api/videoshopping/LoadAllHandPickLives")
    fun fetchHandPickLivesFromJobDetails(@Body requestBody: LiveListRequest): Call<ResponseHeader<List<LiveListData>>>

    @POST("CustomerAccess/SignUpNew")
    suspend fun registerUser(@Body requestBody: RegistrationRequest): NetworkResponse<RegistrationResponse, ErrorResponse>

    @GET("/api/videoshopping/CheckIsCustomerBlock/{customerId}")
    suspend fun checkIsCustomerBlock(@Path("customerId") customerId: Int): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    @GET("api/videoshopping/LoadCatalogInfo/{catalogId}")
    suspend fun getCatalogInfoById(@Path("catalogId") catalogId: Int): NetworkResponse<ResponseHeader<List<CatalogData>>, ErrorResponse>

    @POST("api/videoshopping/LoadVideoShoppingList")
    suspend fun getVideoShoppingList(@Body requestBody: CatalogRequest): NetworkResponse<ResponseHeader<List<CatalogData>>, ErrorResponse>

    @POST("api/videoshopping/LoadAllLiveVideoProducts")
    suspend fun fetchLiveProducts(@Body requestBody: LiveProductRequest): NetworkResponse<ResponseHeader<List<LiveProductData>>, ErrorResponse>

    @GET("order/LiveProductDeliveryInfo/1")
    suspend fun fetchDeliveryInfo(): NetworkResponse<ResponseHeader<DeliveryInfoModel>, ErrorResponse>

    @POST("api/videoshopping/InsertLiveOrder")
    suspend fun insertLiveOrder(@Body requestBody: List<LiveOrderRequest>): NetworkResponse<ResponseHeader<List<LiveOrderResponse>>, ErrorResponse>

    @POST("api/videoshopping/InsertComments")
    suspend fun insertVideoComments(@Body requestBody: VideoInsertCommentsRequest): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    @GET("api/videoshopping/LoadComments/{catalogId}/{index}/{count}")
    suspend fun fetchVideoComments(
        @Path("catalogId") catalogId: Int,
        @Path("index") index: Int,
        @Path("count") count: Int
    ): NetworkResponse<ResponseHeader<List<VideoCommentsModel>>, ErrorResponse>

    @POST("api/videoshopping/InsertReplyComments")
    suspend fun insertVideoReplyComments(@Body requestBody: VideoInsertReplyCommentsRequest): NetworkResponse<ResponseHeader<Int>, ErrorResponse>

    // Live Order Management
    @GET("/api/videoshopping/LoadCustomerOrder/{customerId}/{index}/{count}")
    suspend fun fetchLiveOrderManagementList(
        @Path("customerId") customerId: Int,
        @Path("index") index: Int,
        @Path("count") count: Int
    ): NetworkResponse<ResponseHeader<List<LiveOrderManagementResponseBody>>, ErrorResponse>

    @FormUrlEncoded
    @POST("https://adm.ajkerdeal.com/LivePlaza/TakeLogForLivePlaza")
    suspend fun logForLivePlaza(
        @Field("LiveId") liveId:Int,
        @Field("DeviceName") deviceName:String,
        @Field("Source") source:String,
    ) : String
}