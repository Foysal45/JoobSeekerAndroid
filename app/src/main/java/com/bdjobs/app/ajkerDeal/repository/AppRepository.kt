package com.bdjobs.app.ajkerDeal.repository

import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogRequest
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderRequest
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListRequest
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductRequest
import com.bdjobs.app.ajkerDeal.api.models.registration.RegistrationRequest
import com.bdjobs.app.ajkerDeal.api.models.video_comments.VideoInsertCommentsRequest

class AppRepository(
    private val apiInterfaceAPI: com.bdjobs.app.ajkerDeal.api.ApiInterfaceAPI
) {

    suspend fun fetchHandPickLives(requestBody: LiveListRequest) = apiInterfaceAPI.fetchHandPickLives(requestBody)

    suspend fun getCatalogInfoById(catalogId: Int) = apiInterfaceAPI.getCatalogInfoById(catalogId)

    suspend fun checkIsCustomerBlock(customerId: Int) = apiInterfaceAPI.checkIsCustomerBlock(customerId)

    suspend fun getVideoShoppingList(requestBody: CatalogRequest) = apiInterfaceAPI.getVideoShoppingList(requestBody)

    suspend fun fetchLiveProducts(requestBody: LiveProductRequest) = apiInterfaceAPI.fetchLiveProducts(requestBody)

    suspend fun fetchDeliveryInfo() = apiInterfaceAPI.fetchDeliveryInfo()

    suspend fun insertLiveOrder(requestBody: List<LiveOrderRequest>) = apiInterfaceAPI.insertLiveOrder(requestBody)

    suspend fun insertVideoComments(requestBody: VideoInsertCommentsRequest) = apiInterfaceAPI.insertVideoComments(requestBody)

    suspend fun registerUser(requestBody: RegistrationRequest) = apiInterfaceAPI.registerUser(requestBody)

    // Live Order Management
    suspend fun fetchLiveOrderManagementList(customerId: Int, index: Int, count: Int) = apiInterfaceAPI.fetchLiveOrderManagementList(customerId, index, count)

    // live plaza log
    suspend fun logLivePlaza(liveId: Int) = apiInterfaceAPI.logForLivePlaza(liveId,"android","bdjobs")
}