package com.bdjobs.app.ajkerDeal.di

import com.bdjobs.app.ajkerDeal.api.RetrofitUtils.createCache
import com.bdjobs.app.ajkerDeal.api.RetrofitUtils.createOkHttpClient
import com.bdjobs.app.ajkerDeal.api.RetrofitUtils.getGson
import com.bdjobs.app.ajkerDeal.api.RetrofitUtils.retrofitInstance
import com.bdjobs.app.ajkerDeal.repository.AppRepository
import com.bdjobs.app.ajkerDeal.ui.checkout_live.CheckoutLiveViewModel
import com.bdjobs.app.ajkerDeal.ui.checkout_live.live_order_management.LiveOrderManagementViewModel
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewViewModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.VideoShoppingViewModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.live_product.LiveProductListViewModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.player.ExoPlayerUtils
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.VideoCommentsViewModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.video_reply_comments.VideoReplyCommentsViewModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager.VideoViewModel
import com.bdjobs.app.ajkerDeal.utilities.AppConstant
import com.bdjobs.app.ajkerDeal.utilities.FirebaseConnection
import io.reactivex.schedulers.Schedulers.single
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single { getGson() }
    single { createCache(get()) }
    single { createOkHttpClient(get()) }

    single(named("api")) { retrofitInstance(AppConstant.BASE_URL_API, get(), get()) }

    single { com.bdjobs.app.ajkerDeal.api.ApiInterfaceAPI(get(named("api"))) }

    single { AppRepository(get()) }

    single { ExoPlayerUtils.getInstance(get()) }

    single { FirebaseConnection.initFirebaseDatabase(get()) }

    viewModel { HomeNewViewModel(get()) }
    viewModel { VideoShoppingViewModel(get()) }
    viewModel { VideoViewModel(get()) }
    viewModel { CheckoutLiveViewModel(get()) }
    viewModel { LiveProductListViewModel(get()) }
    viewModel { LiveOrderManagementViewModel(get()) }
    viewModel { VideoCommentsViewModel(get()) }
    viewModel { VideoReplyCommentsViewModel(get()) }

}