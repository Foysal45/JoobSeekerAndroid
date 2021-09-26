package com.bdjobs.app.ajkerDeal.ui.home.page_home

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.bdjobs.app.BuildConfig
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.bdjobs.app.ajkerDeal.api.models.response_body.recommended_deal_payload.RecommendedDealPayload
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager.VideoPagerActivity
import com.bdjobs.app.ajkerDeal.utilities.*
import com.bdjobs.app.databinding.FragmentHomeNewBinding
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeNewFragment : Fragment() {
    private var binding: FragmentHomeNewBinding? = null
    private val viewModel: HomeNewViewModel by inject()

    private lateinit var dataAdapter: HomeNewAdapter
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)

    private val classifiedHashMap: HashMap<String, Int> = HashMap()
    private val mHomePageDataList = ArrayList<RecommendedDealPayload>()
    private val liveList: MutableList<LiveListData> = mutableListOf()

    private val totalItemToBeViewed = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeNewBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = HomeNewAdapter()

        val gridLayoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })

        fetchLiveShowHandPicked(totalItemToBeViewed)
        manageItemClickListener()
    }

    private fun manageItemClickListener() {

//        binding?.swipeRefresh?.setOnRefreshListener {
//            binding?.swipeRefresh?.isRefreshing = true
//            fetchLiveShowHandPicked(totalItemToBeViewed)
//        }

        dataAdapter.onItemClick = { model, parentPosition ->
            Timber.d("requestBody ${model}")

            if (model.statusName == "live") {
                goToViewShow(model, model.statusName, parentPosition)
            } else if (model.statusName == "replay") {
                goToViewShow(model, model.statusName, parentPosition)
            } else if (model.statusName == "upcoming") {
                val formatDate = "${model.liveDate} ${model.fromTime}"
                val date = sdf.parse(formatDate)
                alert("আপকামিং", "এই লাইভটি শুরু হবে ${DigitConverter.relativeWeekday(date)}।", false, "ঠিক আছে") {

                }.show()
            }
        }
    }

    private fun fetchLiveShowHandPicked(count: Int) {
        viewModel.fetchLiveShow(count).observe(viewLifecycleOwner, Observer { list ->

//            binding?.swipeRefresh?.isRefreshing = false

            if (BuildConfig.DEBUG) {
                /*list.add(
                    0,
                    LiveListData(
                        id = 384,
                        videoTitle = "Test live",
                        coverPhoto = "",
                        //videoChannelLink = "https://ad-live-streaming.s3-ap-southeast-1.amazonaws.com/live_show/1433045/384/Fantastic_Salwar_Kameez.m3u8",
                        channelLogo = "https://static.ajkerdeal.com/images/banners/1433045/logo.jpg",
                        customerName = "Fabliha Live",
                        customerId = 328702,
                        channelId = 3232712,
                        statusName = "replay", // live replay
                        channelType = "customer",
                        facebookVideoUrl = "https://www.facebook.com/flashfashionhouse/videos/557937342187835",
                        redirectToFB = true,
                        isShowMobile = true,
                        mobile = "01555555555",
                        alternativeMobile = "01666666666",
                        isShowComment = true,
                        isShowProductCart = false,
                        orderPlaceFlag = 1,
                        isYoutubeVideo = 1,
                        videoId = "lhj-6LwNXCk"
                    )
                )*/
                list.add(
                    0,
                    LiveListData(
                        id=5789,
                        liveDate="14/09/2021",
                        fromTime="17:53:00",
                        toTime="18:53:00",
                        channelId=1696416,
                        channelType="customer",
                        isActive=1,
                        insertedBy=746,
                        scheduleId=0,
                        coverPhoto="https://static.ajkerdeal.com/LiveVideoImage/LiveVideoCoverPhoto/5789/livecoverphoto.jpg",
                        videoTitle="Test Live",
                        customerName=null,
                        profileID=null,
                        compStringName="",
                        channelLogo="https://static.ajkerdeal.com/images/banners/1696416/logo.jpg",
                        channelName="Flash",
                        liveChannelName=null,
                        videoChannelLink="",
                        customerId=0,
                        merchantId=0,
                        statusName="replay",
                        paymentMode="both",
                        facebookPageUrl="https://www.facebook.com/flashfashionhouse/videos/557937342187835",
                        mobile="01853165356", alternativeMobile="",
                        redirectToFB=false, isShowMobile=true,
                        isShowComment=true, isShowProductCart=false,
                        facebookVideoUrl="https://www.facebook.com/flashfashionhouse/videos/557937342187835",
                        orderPlaceFlag=1, categoryId=7, subCategoryId=111, subSubCategoryId=0,
                        isThirdPartyProductUrl=0, isNotificationSended=true, videoId="557937342187835",
                    )
                )
                list.add(
                    0,
                    LiveListData(
                        id=5928,
                        liveDate="19/09/2021",
                        fromTime="22:27:21",
                        toTime="23:00:21",
                        channelId=1412606,
                        channelType="customer",
                        isActive=1,
                        insertedBy=1412606,
                        scheduleId=0,
                        coverPhoto="https://static.ajkerdeal.com/LiveVideoImage/LiveVideoCoverPhoto/5928/livecoverphoto.jpg",
                        videoTitle="Test Live LP",
                        customerName=null,
                        profileID=null,
                        compStringName="",
                        channelLogo="https://static.ajkerdeal.com/images/banners/1412606/logo.jpg",
                        channelName="Gm - Gents Mart",
                        liveChannelName=null,
                        videoChannelLink="https://ad-live-streaming.s3-ap-southeast-1.amazonaws.com/live_show/1412606/5928/live_hls.m3u8",
                        customerId=0,
                        merchantId=0,
                        statusName="replay",
                        paymentMode="both",
                        facebookPageUrl="",
                        mobile="", alternativeMobile="",
                        redirectToFB=false, isShowMobile=false,
                        isShowComment=true, isShowProductCart=false,
                        facebookVideoUrl="",
                        orderPlaceFlag=0, categoryId=0, subCategoryId=0, subSubCategoryId=0,
                        isThirdPartyProductUrl=0, isNotificationSended=false, videoId="",
                    )
                )
            }

            liveList.clear()
            liveList.addAll(list)
            dataAdapter.initList(list)
            Timber.d("requestBody ${dataAdapter.itemCount}, ${list?.size}, ${liveList.size}, ${list}")
            //val position = mHomePageDataList.indexOfFirst { it.homeViewType == HomeViewType.TYPE_LIVE }
            val position = classifiedHashMap["1"] ?: -1
            if (position == -1) return@Observer
            Timber.d("LiveHandPickDebug api call actionType1 $position")

        })
    }

    private fun goToViewShow(model: LiveListData, statusName: String?, position: Int) {
        val videoList: MutableList<CatalogData> = mutableListOf()
        var playIndex = 0
        Timber.d("requestBody $position, $statusName, ${liveList.size}, $model")
        when (statusName) {
            "live" -> {
                generateLiveVideoList(model, videoList)
            }
            "replay" -> {

                Timber.d("requestBody ${liveList.size}")
                val replayList = liveList.filter { it.statusName == "replay" }
                playIndex = replayList.indexOf(model) ?: -1
                replayList.forEach { model1 ->
                    generateLiveVideoList(model1, videoList)
                }
            }
        }
        Intent(requireContext(), VideoPagerActivity::class.java).apply {
            putExtra("playIndex", if (playIndex > -1) playIndex else 0)
            putParcelableArrayListExtra("videoList", videoList as java.util.ArrayList<out Parcelable>)
            putExtra("noCache", true)
            putExtra("isLiveShow", true)
        }.also {
            startActivity(it)
        }
    }

    private fun generateLiveVideoList(model: LiveListData, list: MutableList<CatalogData>): MutableList<CatalogData> {
        list.add(
            CatalogData(
                model.id,
                model.videoTitle,
                model.coverPhoto,
                "",
                model.videoChannelLink ?: "",
                channelLogo = model.channelLogo ?: "",
                customerName = model.channelName,
                customerId = model.channelId,
                sellingTag = model.paymentMode,
                sellingText = model.statusName,
                facebookPageUrl = model.facebookPageUrl ?: "",
                mobile = model.mobile ?: "",
                alternativeMobile = model.alternativeMobile ?: "",
                redirectToFB = model.redirectToFB,
                channelType = model.channelType ?: "",
                isShowMobile = model.isShowMobile,
                isShowComment = model.isShowComment,
                isShowProductCart = model.isShowProductCart,
                facebookVideoUrl = model.facebookVideoUrl,
                liveDate = model.liveDate,
                orderPlaceFlag = model.orderPlaceFlag,
                isYoutubeVideo = model.isYoutubeVideo == 1,
                videoId = model.videoId
            )
        )
        return list
    }


}