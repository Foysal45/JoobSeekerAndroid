package com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload

import android.os.Parcelable
import com.example.livevideoshopping.api.models.HomeViewType
import com.example.livevideoshopping.api.models.live_list.LiveListData
import com.example.livevideoshopping.api.models.response_body.recommended_deal_payload.product_thumbnail_response.ProductResponseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecommendedDealPayload(
    var homeViewType: HomeViewType = HomeViewType.TYPE_RECOMMENDED_DEAL,
    var liveList: MutableList<LiveListData>? = null,
    var indexCount: Int = 0,

    var blockName: String? = null,
    var spanCount: Int = 1,
    var imageUrl: String? = null,
    var blockColor: String? = null,
    var blockTextColor: String? = null,
    var blockActionName: String? = null,

    var isLoading: Boolean = true,
    var isFailed: Boolean = false


) : ProductResponseModel(), Parcelable