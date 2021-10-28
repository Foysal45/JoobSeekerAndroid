package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.*
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.bdjobs.app.ajkerDeal.ui.checkout_live.CheckoutLiveActivity
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.databinding.FragmentFbLiveShowBinding
import com.bdjobs.app.ajkerDeal.ui.chat.ChatAdapter
import com.bdjobs.app.ajkerDeal.ui.chat.model.ChatData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.ajkerDeal.api.models.firebase.LikeCount
import com.bdjobs.app.ajkerDeal.api.models.firebase.LiveProductEvent
import com.bdjobs.app.ajkerDeal.api.models.firebase.ViewCount
import com.bdjobs.app.ajkerDeal.ui.chat.ChatBoxBottomSheet
import com.bdjobs.app.ajkerDeal.ui.video_shopping.live_cart.LiveCartBottomSheet
import com.bdjobs.app.ajkerDeal.ui.video_shopping.live_product.LiveProductListBottomSheet
import com.bdjobs.app.ajkerDeal.utilities.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlin.math.abs
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

class FBLiveShowFragment : Fragment() {

    private var binding: FragmentFbLiveShowBinding? = null

    private var model: CatalogData? = null
    private var position: Int = -1
    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var sessionManager: SessionManager
    private lateinit var bdJobsUserSession: BdjobsUserSession
    private val REQUEST_CODE_CHAT = 789
    private val requestCodeCheckOut = 12920
    private var liveStreamId: String = "0"
    private var merchantId: Int = 0
    private var paymentMode: String = "both"
    private var isUrlLoaded: Boolean = false
    private var isLike: Boolean = false
    private var cartProductList: MutableList<LiveProductData> = mutableListOf()
    private var productList: MutableList<LiveProductData> = mutableListOf()
    private val productOverviewAdapter = LiveProductOverviewAdapter()

    // Live Chat
    private lateinit var chatRoomRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var dataAdapter: ChatAdapter
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    private val queryLimit = 15
    private var isLoading = false
    private val visibleThreshold = 6
    private var totalCount = 0
    private var likeCount: Long = 0

    // Live Show
    private lateinit var liveShowViewRef: DatabaseReference
    private lateinit var dbFirestoreViews: CollectionReference
    private lateinit var dbFirestoreLikes: CollectionReference
    private var currentViewLister: ListenerRegistration? = null
    private var currentLikeLister: ListenerRegistration? = null
    private lateinit var productUpdateRef: DatabaseReference
    private lateinit var productHighLightRef: DatabaseReference
    private lateinit var textHighLightRef: DatabaseReference
    private var viewUserKey = ""

    // Handler
    private var videoCoverHandler = Handler(Looper.getMainLooper())
    private var videoCoverRunnable: Runnable? = null
    private var productListHandler = Handler(Looper.getMainLooper())
    private var productListRunnable: Runnable? = null
    private var recentViewerAnimationHandler = Handler(Looper.getMainLooper())
    private var recentViewerRunnable: Runnable? = null
    private var productUploadAnimationHandler = Handler(Looper.getMainLooper())
    private var productUploadRunnable: Runnable? = null

    //Youtube player
    //private var youtubePlayer: YouTubePlayer? = null

    private val viewModel: VideoViewModel by inject()
    private val firebaseApp: FirebaseApp by inject()

    companion object {

        fun newInstance(model: CatalogData, position: Int = 0, exoPlayer: SimpleExoPlayer?): FBLiveShowFragment = FBLiveShowFragment().apply {
            this.model = model
            this.position = position
            this.exoPlayer = exoPlayer
        }

        val tag: String = FBLiveShowFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentFbLiveShowBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("FBLive")

        sessionManager = SessionManager
        bdJobsUserSession = BdjobsUserSession(requireContext())
        liveStreamId = model?.catalogId?.toString() ?: "0"
        merchantId = model?.customerId ?: 0
        paymentMode = model?.sellingTag ?: "both"

        initWebview()
        initVideoInfo()
        initLiveProduct()
    }

    //#region Init
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {
        binding?.webView?.let { webView ->
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                setSupportZoom(false)
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                //loadWithOverviewMode = true
                //useWideViewPort = true
            }
            webView.apply {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                clearHistory()
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
                clearCache(true)
                webViewClient = Callback()
                addJavascriptInterface(WebAppInterface(), "Android")
            }
        }
    }

    private fun initVideoInfo() {

        binding?.channelName?.text = model?.customerName

        var date = model?.liveDate ?: "30/01/2021"
        date = DigitConverter.toBanglaDate(date, "dd/MM/yyyy")
        binding?.videoDate?.text = date

        binding?.channelLogo?.let { view ->
            Glide.with(view)
                .load(model?.channelLogo)
                .apply(RequestOptions().placeholder(R.drawable.profile_image).error(R.drawable.profile_image).circleCrop())
                .into(view)
        }
        binding?.videoCover?.let { view ->
            Glide.with(view).load(model?.coverPhoto).into(view)
        }

        binding?.liveTitle?.text = model?.videoTitle
        binding?.channelLogo1?.let { view ->
            Glide.with(view)
                .load(model?.channelLogo)
                .apply(RequestOptions().placeholder(R.drawable.profile_image).error(R.drawable.profile_image).circleCrop())
                .into(view)
        }
        binding?.shareBtn?.setOnClickListener {
            shareLive()
            //UserLogger.logGenie(requireContext(), "Live_Share")
        }
        binding?.backBtn?.setOnClickListener {
            (activity as VideoPagerActivity).onBackPressed()
        }
        binding?.likeVideo?.setOnClickListener {
            if (isLike) {
                binding?.likeVideo?.setAnimation(R.raw.unlike_video_shadow_animation)
                isLike = false
                //removeLike()
            } else {
                binding?.likeVideo?.setAnimation(R.raw.like_video_shadow_animation)
                isLike = true
                addLike()
                binding?.likeLottie?.setAnimation(R.raw.heart)
                binding?.likeLottie?.playAnimation()
            }
            binding?.likeVideo?.playAnimation()
            //UserLogger.logGenie(requireContext(), "Live_Like")
        }
        binding?.productCart?.setOnClickListener {
            showProductBottomSheet(productOverviewAdapter.productList(), -1)
            //UserLogger.logLiveViewItem(requireContext(), model?.customerId!!) //here customerID is ChannelID using CatalogData
        }

        binding?.cartCount?.setOnClickListener {
            if (cartProductList.isNotEmpty()) {
                showLiveCartBottomSheet()
            }
        }

        if (model?.redirectToFB == true) {
            binding?.cartCount?.isVisible = false
        }

        binding?.commentBtn?.isVisible = model?.isShowComment == true

        binding?.commentBtn?.setOnClickListener {
            binding?.chatLayout?.isVisible = binding?.chatLayout?.isVisible == false

            binding.apply {
                if (binding?.chatLayout?.isVisible!!) {
                    binding?.commentBtn?.setBackgroundResource(R.drawable.ic_sms)
                } else {
                    binding?.commentBtn?.setBackgroundResource(R.drawable.ic_sms_disabled)
                }
            }
        }

        if (model?.sellingText == "live") {
            binding?.viewCount?.isVisible = false
            binding?.channelInfo?.isVisible = true
            binding?.channelLogo3?.let { view ->
                Glide.with(view)
                    .load(model?.channelLogo)
                    .apply(RequestOptions().placeholder(R.drawable.profile_image).error(R.drawable.profile_image).circleCrop())
                    .into(view)
            }
        }

        binding?.dealViewBtn?.setOnClickListener {
            if (binding?.productRecyclerView?.visibility == View.VISIBLE) {
                binding?.productRecyclerView?.visibility = View.INVISIBLE
                binding?.dealViewBtn?.rotation = 0f
            } else {
                binding?.productRecyclerView!!.visibility = View.VISIBLE
                binding?.dealViewBtn?.rotation = 180f
            }
        }

        binding?.offerInfoBtn?.setOnClickListener {
            if (binding?.highlightText?.visibility == View.VISIBLE) {
                binding?.highlightText?.visibility = View.GONE
            } else {
                val text = binding?.highlightText?.text?.toString() ?: ""
                if (text.isNotEmpty()) {
                    binding?.highlightText!!.visibility = View.VISIBLE
                }
            }
        }

    }

    //endregion

    //#region LifeCycleMethods
    override fun onResume() {
        super.onResume()
        binding?.liveStatusLayout?.isVisible = false
        if (cartProductList.isNotEmpty()) cartProductList.clear()
        //facebook
        val fbLink = model?.facebookVideoUrl ?: ""
        if (fbLink.isNotEmpty()) {
            loadUrl()
        }
        //youtube
        if (model?.isYoutubeVideo == true) {
            initYoutubePlayer()
        }
        initChat()                                //commentOut for Firebase Issue
        initLiveCount()                           //commentOut for Firebase Issue
        initLikeCount()                           //commentOut for Firebase Issue
        if (model?.sellingText == "live") {
            addViewer()                           //commentOut for Firebase Issue
            queryRecentViewer()                   //commentOut for Firebase Issue
        }
        liveProductEvent()                        //commentOut for Firebase Issue
        initProductHighlight()                    //commentOut for Firebase Issue
        initTextHighlight()                       //commentOut for Firebase Issue
        initSticker()                             //commentOut for Firebase Issue
        productAutoShow()
        valueEventLister()                        //commentOut for Firebase Issue
        manageFBLink()
        //UserLogger.logLiveWatch(context, model?.customerId!!)
        Timber.d("liveModelDebugFB $model")
    }

    override fun onPause() {
        super.onPause()
        binding?.webView?.onPause()
        binding?.reactionLottie?.cancelAnimation()
        //Chat
        //TODO uncomment the following line
        //chatRoomRef.removeEventListener(valueEventListener)
        binding?.recyclerView?.removeOnScrollListener(chatScrollLister)
        // Show
        //TODO uncomment the following line
        /*productUpdateRef.removeEventListener(productUpdateLister)
        productHighLightRef.removeEventListener(productHighLightLister)
        textHighLightRef.removeEventListener(textHighLightLister)
        if (model?.sellingText == "live") {
            removeViewer()
        }*/
        currentLikeLister?.remove()
        //Handler
        videoCoverRunnable?.let {
            videoCoverHandler.removeCallbacks(it)
        }
        productListRunnable?.let {
            productListHandler.removeCallbacks(it)
        }
        recentViewerRunnable?.let {
            recentViewerAnimationHandler.removeCallbacks(it)
        }
        productUploadRunnable?.let {
            productUploadAnimationHandler.removeCallbacks(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    //#endregion

    //#region Product Cart
    private fun initLiveProduct() {
        binding?.productRecyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productOverviewAdapter
            }
            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return when (e.action) {
                        MotionEvent.ACTION_MOVE -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                            false
                        }
                        else -> false
                    }
                }
            })
        }

        productOverviewAdapter.onItemClick = { model, position ->
            showProductBottomSheet(productOverviewAdapter.productList(), position)
        }

        fetchProductList()
    }

    private fun fetchProductList() {
        viewModel.fetchLiveProducts(liveStreamId.toInt()).observe(viewLifecycleOwner, Observer { list ->
            productList.clear()
            productList.addAll(list)
            binding?.productCart?.isVisible = productList.isNotEmpty()
            productOverviewAdapter.initList(productList)
        })
    }

    private fun showProductBottomSheet(productList: MutableList<LiveProductData>, listPosition: Int, isAutoOpen: Boolean = false) {

        productListRunnable?.let {
            productListHandler.removeCallbacks(it)
        }

        val tag = LiveProductListBottomSheet.tag
        val dialog = LiveProductListBottomSheet.newInstance(model?.catalogId ?: 0, productList, listPosition, cartProductList, model?.redirectToFB ?: false, isAutoOpen)
        dialog.show(childFragmentManager, tag)
        dialog.onBuyClick = { model ->
            dialog.dismiss()
            cartProductList.clear()
            cartProductList.add(model)
            /*if (sessionManager.isLoggedIn) {
                goToCheckout()
            } else {
                val intent = Intent(requireContext(), SignInNewActivity::class.java)
                startActivityForResult(intent, requestCodeCheckOut)
            }*/
            if (this.model?.redirectToFB == true) {
                //UserLogger.logGenie(requireContext(), "Live_BuyNowFB_${liveStreamId}_${merchantId}")
                redirectToFB(this.model?.facebookVideoUrl ?: "")
            } else {
                goToCheckout()
            }
        }
        dialog.onCartClick = { model ->
            addToCart(model)
        }
        dialog.onShopCartClicked = {
            dialog.dismiss()
            binding?.cartCount?.performClick()
        }
    }

    private fun showLiveCartBottomSheet() {
        val tag = LiveCartBottomSheet.tag
        val dialog = LiveCartBottomSheet.newInstance(model?.catalogId ?: 0, cartProductList)
        dialog.show(childFragmentManager, tag)
        dialog.onBuyClick = {
            dialog.dismiss()
            /*if (sessionManager.isLoggedIn) {
                goToCheckout()
            } else {
                val intent = Intent(requireContext(), SignInNewActivity::class.java)
                startActivityForResult(intent, requestCodeCheckOut)
            }*/
            if (this.model?.redirectToFB == true) {
                redirectToFB(this.model?.facebookVideoUrl ?: "")
            } else {
                goToCheckout()
            }
        }
        dialog.onDeleteClick = {
            binding?.cartCount?.text = DigitConverter.toBanglaDigit(cartProductList.size)
        }

    }

    private fun addToCart(model: LiveProductData) {
        if (!cartProductList.contains(model)) {
            cartProductList.add(model)
            context?.toast("কার্টে যোগ হয়েছে")
            binding?.cartCount?.text = DigitConverter.toBanglaDigit(cartProductList.size)
        }
    }

    private fun redirectToFB(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
        }
    }

    private fun goToCheckout() {
        Intent(requireContext(), CheckoutLiveActivity::class.java).apply {
            putExtra("productModel", cartProductList.first())
            putExtra("merchantId", merchantId)
            putParcelableArrayListExtra("productModelList", cartProductList as ArrayList<out Parcelable>)
            putExtra("paymentMode", paymentMode)
            putExtra("channelType", model?.channelType)
            putExtra("orderPlaceFlag", model?.orderPlaceFlag)
        }.also {
            startActivity(it)
        }
        var productIdList = ""
        var productPriceTotal = 0
        cartProductList.forEach { data ->
            productIdList += "${data.id},"
            productPriceTotal += data.productPrice

        }
        //UserLogger.logLiveBeginCheckout(requireContext(), productIdList, productPriceTotal, "LiveVideoShopping", "App", "App", model?.customerId ?: 0)
    }

    private fun pictureDialog(model: LiveProductData, listener: ((type: Int) -> Unit)? = null) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_product_overview, null)
        builder.setView(view)
        val title: TextView = view.findViewById(R.id.title)
        val productImage: ImageView = view.findViewById(R.id.image)
        val close: ImageView = view.findViewById(R.id.close)

        title.text = "প্রোডাক্ট কোড: ${model.id} দাম: ${DigitConverter.toBanglaDigit(model.productPrice, true)}৳"
        Glide.with(productImage)
            .load(model.coverPhoto)
            .apply(RequestOptions().placeholder(R.drawable.logo))
            .into(productImage)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }
    //#endregion

    //#region Live Chat
    @SuppressLint("ClickableViewAccessibility")
    private fun initChat() {

        dataAdapter = ChatAdapter(requireContext())
        val linearLayoutManager = LinearLayoutManager(requireContext())
        //linearLayoutManager.stackFromEnd = true
        binding?.recyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = dataAdapter
            }
            recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return when (e.action) {
                        MotionEvent.ACTION_MOVE -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                            false
                        }
                        else -> false
                    }
                }
            })
        }

        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        chatRoomRef = dbRef.child("chat").child("groupChat").child(liveStreamId)
        chatRoomRef.orderByKey().limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //Timber.tag("chatDebug").d("init load $snapshot")
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    Timber.tag("chatDebug").d("init load $historyList")
                    dataAdapter.initLoad(historyList)
                    binding?.recyclerView?.postDelayed({
                        binding?.recyclerView?.smoothScrollToPosition(dataAdapter.itemCount)
                    }, 200L)
                    if (historyList.isNotEmpty() && (historyList.size > 2)) {
                        binding?.chatLayout?.isVisible = true
                        if (binding?.highlightText?.isVisible == true) {
                            binding?.highlightText?.isVisible = false
                        }
                    } else {
                        binding?.highlightText?.isVisible = true
                    }

                } else {
                    //binding?.emptyView?.visibility = View.VISIBLE
                    Timber.tag("chatDebug").d("chatRoomReference $snapshot")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //binding?.progressBar?.visibility = View.GONE
                //binding?.emptyView?.visibility = View.VISIBLE
                Timber.tag("chatDebug").d("chatRoomReference ${error.message}")
            }
        })

        //valueEventLister() resume called

        binding?.chatBox?.setOnClickListener {

            if (!sessionManager.customerBlock) {
                    if (sessionManager.mobileNumber?.isNotEmpty() == true) {
                        showChatBox()
                    } else {
                        val builder = AlertDialog.Builder(requireContext())
                        val view = requireActivity().layoutInflater.inflate(R.layout.layout_dialog_phone_number, null)

                        builder.setView(view)

                        builder.setPositiveButton("সাবমিট", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                val number = view.findViewById<EditText>(R.id.etMobileNo).text.toString()
                                if (number.isEmpty()) {
                                    context?.toast("Enter a number")
                                } else {
                                    if (isValidPhone(number)) {
                                        dialog?.dismiss()
                                        sessionManager.mobileNumber = number
                                        context?.toast("Number saved")
                                        showChatBox()
                                    } else {
                                        context?.toast("Enter a valid number")
                                    }
                                }
                            }
                        })
                        builder.setNegativeButton("ক্যানসেল", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        builder.show()
                    }
                } else {
                    context?.toast("You are blocked from leaving comments")
                }

        }

        binding?.recyclerView?.addOnScrollListener(chatScrollLister)

        dataAdapter.onItemClicked = { clicked ->
            Timber.d("pressed on data $clicked")
            binding?.chatLayout?.isVisible = false
        }

        binding?.recyclerView?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Timber.d("pressed on empty space")
                binding?.chatLayout?.isVisible = false
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (v.id != 0 && abs(android.R.attr.scrollX - event.x) <= 5 && abs(android.R.attr.scrollY - event.y) <= 5) {
                    // recyclerView space, click Exit
                }
            }
            false
        }

    }

    private val chatScrollLister = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy < 0) {
                val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                Timber.d("onScrolled ${!isLoading} $currentItemCount <= ${firstVisibleItem + visibleThreshold}")
                if (!isLoading && firstVisibleItem < 5 && currentItemCount > queryLimit - 1 /*&& currentItemCount < totalCount*/) {
                    isLoading = true
                    val endKey = dataAdapter.firstItem().key
                    fetchMoreHistory(endKey)

                }
            }
        }
    }

    private fun showChatBox() {
        val dialog = ChatBoxBottomSheet.newInstance()
        val tag = ChatBoxBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { message ->
            dialog.dismiss()
            Timber.d(message)
            hideKeyboard()
            sendChatMessage(message)
        }
    }

    private fun sendChatMessage(msg: String) {
        val key = chatRoomRef.push().key ?: ""
        val date = Date().time
        val model = ChatData(key, sessionManager.userId.toString(), bdJobsUserSession.fullName, "", msg, sdf.format(date), date.toString())
        chatRoomRef.child(key).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("Msg send successfully")
            } else {
                Timber.d("Msg send error. ${it.exception?.message}")
            }
        }
    }

    private fun valueEventLister() {

        valueEventListener = chatRoomRef.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.tag("chatDebug").d(snapshot.toString())
                val historyList: MutableList<ChatData> = mutableListOf()
                snapshot.children.forEach { dataSnapshot ->
                    val model = dataSnapshot.getValue(ChatData::class.java)
                    model?.let {
                        if (!dataAdapter.isDataExist(it)) {
                            historyList.add(it)
                        }
                    }
                }
                //historyList.reverse()
                if (historyList.isNotEmpty()) {
                    dataAdapter.addNewData(historyList)
                    binding?.recyclerView?.smoothScrollToPosition(dataAdapter.itemCount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("agentHistory valueEventListener ${error.message}")
            }
        })
    }

    private fun fetchMoreHistory(endKey: String?) {
        Timber.tag("chatDebug").d("fetchMoreHistory $endKey")
        chatRoomRef.orderByKey().endAt(endKey).limitToLast(queryLimit).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    isLoading = false
                    Timber.tag("chatDebug").d("lazy load $snapshot")
                    val historyList: MutableList<ChatData> = mutableListOf()
                    snapshot.children.forEach { dataSnapshot ->
                        val model = dataSnapshot.getValue(ChatData::class.java)
                        model?.let {
                            historyList.add(it)
                        }
                    }
                    //historyList.reverse()
                    Timber.tag("chatDebug").d("lazy load $historyList")
                    if (historyList.isNotEmpty()) {
                        historyList.removeLast()
                    }
                    dataAdapter.pagingLoadReverse(historyList)
                    if (historyList.size < queryLimit - 1) {
                        isLoading = true
                    }

                } else {
                    isLoading = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = true
                Timber.tag("chatDebug").d("agentHistory fetchMoreHistory ${error.message}")
            }

        })
    }
    //#endregion

    //#region Live View
    private fun initLiveCount() {
        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        liveShowViewRef = dbRef.child("views").child(liveStreamId)

        val dbFirestore = FirebaseFirestore.getInstance(firebaseApp).collection("liveshow").document("views")
        dbFirestoreViews = dbFirestore.collection(liveStreamId)
        if (model?.sellingText == "live") {
            dbFirestoreViews.document("currentView").get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    Timber.tag("firebaseDebug").d("document do not exist")
                    dbFirestoreViews.document("currentView").set(ViewCount(1))
                    dbFirestoreViews.document("totalView").set(ViewCount(1))
                } else {
                    Timber.tag("firebaseDebug").d("${document.data}")
                    dbFirestoreViews.document("totalView").update("view", FieldValue.increment(1))

                    val viewerCount = document.getLong("view") ?: 0L
                    val viewerCountStr = if (viewerCount >= 1000L) {
                        "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
                    } else {
                        viewerCount.toString()
                    }
                    binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)}"
                    Timber.tag("firebaseDebug").d("totalView $viewerCountStr")
                }
            }.addOnFailureListener {
                Timber.tag("firebaseDebug").d(it)
            }
            // Lister
            currentViewLister = dbFirestoreViews.document("currentView").addSnapshotListener(currentViewEventListener)
        } else {
            dbFirestoreViews.document("totalView").get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    Timber.tag("firebaseDebug").d("document do not exist")
                    dbFirestoreViews.document("currentView").set(ViewCount(1))
                    dbFirestoreViews.document("totalView").set(ViewCount(1))
                } else {
                    Timber.tag("firebaseDebug").d("${document.data}")
                    dbFirestoreViews.document("totalView").update("view", FieldValue.increment(1))

                    val viewerCount = document.getLong("view") ?: 0L
                    val viewerCountStr = if (viewerCount >= 1000L) {
                        "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
                    } else {
                        viewerCount.toString()
                    }
                    binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)}"
                    Timber.tag("firebaseDebug").d("totalView $viewerCountStr")


                }
            }.addOnFailureListener {
                Timber.tag("firebaseDebug").d(it)
            }
        }
    }

    private val currentViewEventListener = EventListener<DocumentSnapshot> { value, error ->
        if (value != null && value.exists()) {
            val viewerCount = value.getLong("view") ?: 0L
            val viewerCountStr = if (viewerCount >= 1000L) {
                "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
            } else {
                if (viewerCount > 0L) {
                    viewerCount.toString()
                } else {
                    "0"
                }
            }
            binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)}"
            Timber.tag("firebaseDebug").d("currentView $viewerCountStr")
        }
    }

    private fun addViewer() {
        viewUserKey = liveShowViewRef.push().key ?: ""
        liveShowViewRef.child(viewUserKey).setValue(bdJobsUserSession.fullName)
        dbFirestoreViews.document("currentView").update("view", FieldValue.increment(1))
    }

    private fun removeViewer() {
        liveShowViewRef.child(viewUserKey).removeValue()
        currentViewLister?.remove()
        dbFirestoreViews.document("currentView").update("view", FieldValue.increment(-1))
    }

    private fun queryRecentViewer() {
        liveShowViewRef.orderByKey().limitToLast(5).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val nameList: MutableList<String> = mutableListOf()

                    snapshot.children.forEach { dataSnapshot ->
                        try {
                            val model = dataSnapshot.getValue(String::class.java) ?: ""
                            if (model.isNotEmpty()) {
                                nameList.add("$model যোগদান করেছেন")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if (nameList.isNotEmpty() && isResumed) {
                        val slideInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_start)
                        var i = 0
                        val size = nameList.size

                        recentViewerRunnable = object : Runnable {
                            override fun run() {
                                if (i < size) {
                                    binding?.joinNow?.text = nameList[i]
                                    if (binding?.joinNow?.visibility == View.GONE) {
                                        binding?.joinNow?.visibility = View.VISIBLE
                                    }
                                    binding?.joinNow?.startAnimation(slideInAnim)
                                    i++
                                    recentViewerAnimationHandler.postDelayed(this, 1500L)
                                } else {
                                    binding?.joinNow?.visibility = View.GONE
                                }
                            }
                        }
                        recentViewerAnimationHandler.postDelayed(recentViewerRunnable!!, 1500L)
                    }
                    Timber.tag("viewDebug").d("init load $snapshot")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.tag("chatDebug").d("queryRecentViewer ${error.message}")
            }

        })
    }
    //#endregion

    //#region Live Like
    private fun initLikeCount() {
        val dbFirestore = FirebaseFirestore.getInstance(firebaseApp).collection("liveshow").document("likes")
        dbFirestoreLikes = dbFirestore.collection(liveStreamId)
        dbFirestoreLikes.document("totalLike").get().addOnSuccessListener { document ->
            if (!document.exists()) {
                Timber.tag("firebaseDebug").d("document do not exist")
                dbFirestoreLikes.document("totalLike").set(LikeCount(0))
            } else {
                Timber.tag("firebaseDebug").d("${document.data}")
                //dbFirestoreLikes.document("totalLike").update("like", FieldValue.increment(1))
                /*val viewerCount = document.getLong("like") ?: 0L
                val viewerCountStr = if (viewerCount >= 1000L) {
                    "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
                } else {
                    viewerCount.toString()
                }*/
                //binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)} জন দেখেছেন"
                //Timber.tag("firebaseDebug").d("totalLike $viewerCountStr")
            }
        }.addOnFailureListener {
            Timber.tag("firebaseDebug").d(it)
        }
        currentLikeLister = dbFirestoreLikes.document("totalLike").addSnapshotListener(currentLikeEventListener)
    }

    private val currentLikeEventListener = EventListener<DocumentSnapshot> { value, error ->
        if (value != null && value.exists()) {
            val currentLikeCount = value.getLong("like") ?: 0L
            if (currentLikeCount > likeCount) {
                likeCount = currentLikeCount
                //binding?.reactionLottie?.setAnimation(R.raw.like_react)
                binding?.reactionLottie?.playAnimation()
            }
            /*val viewerCountStr = if (viewerCount >= 1000L) {
                "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
            } else {
                if (viewerCount > 0L) {
                    viewerCount.toString()
                } else {
                    "0"
                }
            }*/
            //binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)} জন দেখছেন"
            Timber.tag("firebaseDebug").d("totalLike $currentLikeCount")
        }
    }

    private fun addLike() {
        //val userId = sessionManager.userId.toString()
        //liveShowLikeRef.child(userId).setValue(true)
        dbFirestoreLikes.document("totalLike").update("like", FieldValue.increment(1))
        //UserLogger.logGenie(requireContext(), "Live_like_${liveStreamId}")
    }

    /*private fun removeLike() {
        val userId = sessionManager.userId.toString()
        liveShowLikeRef.child(userId).removeValue()
    }*/
    //#endregion

    //#region Live New Product Event
    private fun liveProductEvent() {
        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        productUpdateRef = dbRef.child("productUpdate").child(liveStreamId)
        productUpdateRef.addValueEventListener(productUpdateLister)
    }

    private val productUpdateLister = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.tag("firebaseDebug").d("productUpdate $snapshot")
            if (snapshot.exists()) {
                val model: LiveProductEvent? = snapshot.getValue(LiveProductEvent::class.java)
                val timeStamp: Long = model?.time ?: 0
                if (timeStamp > 0L) {
                    val currentTimeStamp = Date().time - 120000
                    if (timeStamp > currentTimeStamp) {

                        binding?.productPreviewImage?.let { view ->
                            Glide.with(view)
                                .load(model?.imageUrl)
                                .into(view)
                        }

                        val slideInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_start)
                        if (binding?.productPreviewLayout?.visibility == View.GONE) {
                            fetchProductList()
                            binding?.productPreviewLayout?.visibility = View.VISIBLE
                        }
                        binding?.productPreviewLayout?.startAnimation(slideInAnim)
                        productUploadRunnable = object : Runnable {
                            override fun run() {
                                binding?.productPreviewLayout?.visibility = View.GONE
                            }
                        }
                        productUploadAnimationHandler.postDelayed(productUploadRunnable!!, 5000L)
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.tag("firebaseDebug").d("chatRoomReference ${error.message}")
        }
    }
    //#endregion

    //#region Live Product Highlight
    private fun initProductHighlight() {
        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        productHighLightRef = dbRef.child("productHighlight").child(liveStreamId)
        productHighLightRef.addValueEventListener(productHighLightLister)
    }

    private val productHighLightLister = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.tag("firebaseDebug").d("productUpdate $snapshot")
            if (snapshot.exists()) {
                val model: LiveProductEvent? = snapshot.getValue(LiveProductEvent::class.java)
                val timeStamp: Long = model?.time ?: 0
                if (timeStamp > 0L) {
                    val currentTimeStamp = Date().time - 120000
                    if (timeStamp > currentTimeStamp) {
                        if (model?.imageUrl.isNullOrEmpty()) {
                            binding?.currentProductIV?.isVisible = false
                        } else {
                            binding?.currentProductIV?.isVisible = true
                            binding?.currentProductIV?.let { view ->
                                Glide.with(view)
                                    .load(model?.imageUrl)
                                    .into(view)
                            }
                            binding?.currentProductIV?.setOnClickListener {
                                pictureDialog(LiveProductData(coverPhoto = model?.imageUrl, productPrice = model?.price ?: 0, id = model?.productCode ?: 0))
                            }
                        }
                    } else {
                        binding?.currentProductIV?.isVisible = false
                    }
                }
            } else {
                binding?.currentProductIV?.isVisible = false
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.tag("firebaseDebug").d("chatRoomReference ${error.message}")
        }
    }
    //#endregion

    //#region Live Text Highlight
    private fun initTextHighlight() {
        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        textHighLightRef = dbRef.child("textHighlight").child(liveStreamId)
        textHighLightRef.addValueEventListener(textHighLightLister)
    }

    private val textHighLightLister = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.tag("firebaseDebug").d("textUpdate $snapshot")
            if (snapshot.exists()) {
                try {
                    val model: LiveProductEvent? = snapshot.getValue(LiveProductEvent::class.java)
                    val msg = model?.productHighlight
                    val color = model?.productHighlightBGColor ?: "#59000000"
                    if (msg.isNullOrEmpty()) {
                        binding?.highlightText?.isVisible = false
                    } else {
                        //binding?.highlightText?.isVisible = true
                        binding?.highlightText?.text = HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        try {
                            binding?.highlightText?.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding?.highlightText?.isVisible = false
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.tag("firebaseDebug").d("chatRoomReference ${error.message}")
        }
    }
    //#endregion

    //#region Live Push Sticker
    private fun initSticker() {
        val dbRef = FirebaseDatabase.getInstance(firebaseApp).getReference("LiveShow")
        productHighLightRef = dbRef.child("sticker").child(liveStreamId)
        productHighLightRef.addValueEventListener(stickerLister)
    }

    private val stickerLister = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.tag("firebaseDebug").d("productUpdate $snapshot")
            if (snapshot.exists()) {
                try {
                    val model: LiveProductEvent? = snapshot.getValue(LiveProductEvent::class.java)
                    if (model?.imageUrl.isNullOrEmpty()) {
                        binding?.pushStickerIV?.isVisible = false
                    } else {
                        binding?.pushStickerIV?.isVisible = true
                        binding?.pushStickerIV?.let { view ->
                            Glide.with(view)
                                .load(model?.imageUrl)
                                .into(view)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding?.pushStickerIV?.isVisible = false
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.tag("firebaseDebug").d("chatRoomReference ${error.message}")
        }
    }
    //#endregion

    //#region Product Auto Show
    private fun productAutoShow() {
        if (model?.isDialogShown == false && model?.isShowProductCart == true) {
            productListRunnable = Runnable {
                if (isAdded) {
                    model?.isDialogShown = true
                    showProductBottomSheet(productList, -1, true)
                }
            }
            productListHandler.postDelayed(productListRunnable!!, AppConstant.AUTO_SHOW_DIALOG_TIME)
        }
    }
    //#endregion

    //#region manageFBLink()
    private fun manageFBLink() {
        Timber.d("facebookPageDebug ${model?.facebookVideoUrl}")
        binding?.fbPageLayout?.isVisible = model?.facebookVideoUrl?.isEmpty() == false

        if (model?.isShowMobile == true) {
            binding?.fbMobile?.isVisible = model?.mobile?.isNotEmpty() == true
        } else {
            binding?.fbMobile?.isVisible = false
        }

        binding?.fbPageLayout?.setOnClickListener {
            redirectToFB(model?.facebookVideoUrl ?: "")
        }

        binding?.fbMobile?.setOnClickListener {
            val mobile = model?.mobile
            if (model?.alternativeMobile?.isNotEmpty() == true) {
                val builder = AlertDialog.Builder(context)
                val alternateMobile = model?.alternativeMobile
                builder.setTitle("কোন নাম্বার এ কল করতে চান?")
                val numberLists = arrayOf(mobile, alternateMobile)
                builder.setItems(numberLists) { _, which ->
                    when (which) {
                        0 -> {
                            callFBMerchant(numberLists[0].toString())
                        }
                        1 -> {
                            callFBMerchant(numberLists[1].toString())
                        }
                    }
                }
                val dialog = builder.create()
                dialog.show()

            } else {
                callFBMerchant(mobile.toString())
            }
        }
    }
    //#endregion

    //#region Callbacks
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHAT) {
            viewModel.checkIsCustomerBlock(sessionManager.userId).observe(viewLifecycleOwner, Observer { flag ->
                sessionManager.customerBlock = flag
                if (flag) {
                    context?.toast("You are blocked from leaving comments")
                } else {
                    showChatBox()
                }
            })
        } else if (resultCode == Activity.RESULT_OK && requestCode == requestCodeCheckOut) {
            if (this.model?.redirectToFB == true) {
                redirectToFB(this.model?.facebookVideoUrl ?: "")
            } else {
                goToCheckout()
            }
        }
    }
    //#endregion

    //#region Share Live
    private fun shareLive() {

        val dialog = progressDialog("অপেক্ষা করুন, শেয়ার লিংক তৈরি হচ্ছে")
        dialog.show()

        val referralUrl = "https://m.ajkerdeal.com/livevideoshopping/$liveStreamId/0"
        val uri = Uri.parse(referralUrl)
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = uri
            domainUriPrefix = AppConstant.DOMAIN_URL_PREFIX
            androidParameters {
                minimumVersion = 21
                fallbackUrl = uri
            }
            googleAnalyticsParameters {
                source = "AndroidApp"
            }
            socialMetaTagParameters {
                title = model?.videoTitle ?: "Live Video Shopping"
                description = "আজকেরডিল লাইভ ভিডিও শপিং"
                imageUrl = Uri.parse(model?.coverPhoto)
            }
            navigationInfoParameters {
                forcedRedirectEnabled = true
            }
            buildShortDynamicLink().addOnSuccessListener { shortDynamicLink ->
                dialog.dismiss()
                val shortLink = shortDynamicLink.shortLink
                val flowchartLink = shortDynamicLink.previewLink //flowchart link is a debugging URL
                Timber.d("createDynamicLink shortLink: $shortLink flowchartLink: $flowchartLink")

                generateShareContent(shortLink.toString())

            }.addOnFailureListener {
                dialog.dismiss()
                Timber.d(it)
                context?.toast("কোথাও কোনো সমস্যা হচ্ছে")
            }
        }

    }

    private fun generateShareContent(url: String) {
        val msg = "আমি এখন একটা লাইভ শপিং সেশন দেখছি, লাইভটি আমার সাথে দেখার জন্য ক্লিক করুন -\n$url"
        shareContent(msg)
    }

    private fun shareContent(msg: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, msg)
        }.also {
            activity?.startActivity(it)
        }
    }
    //#endregion

    //#region WebView
    private fun loadUrl() {
        if (!isUrlLoaded) {
            //val url = "https://m.ajkerdeal.com/play/livevideo.html?profile=258421908212138&video=184540746859411"
            val fbLink = model?.facebookVideoUrl ?: ""
            val url = generateUrl(fbLink)
            if (url.isNotEmpty()) {
                Timber.d("fbLink $url")
                binding?.webView?.isVisible = true
                binding?.webView?.loadUrl(url)
                binding?.progressBar?.isVisible = true
            } else {
                context?.toast("Invalid facebook url")
            }
        } else {
            binding?.webView?.onResume()
        }
    }

    private fun generateUrl(fbLink: String): String {
        //Sample https://www.facebook.com/332656306807004/videos/188025879796045
        Timber.d("fbLink $fbLink")
        var url: String = ""
        if (fbLink.isNotEmpty()) {
            val uri = Uri.parse(fbLink)
            Timber.d("fbLink ${uri.host} ${uri.path} ${uri.pathSegments}")
            if (uri.host == "www.facebook.com") {
                val segments = uri.pathSegments
                if (!segments.isNullOrEmpty()) {
                    if (segments.size == 3) {
                        url = "https://m.ajkerdeal.com/play/livevideo.html?profile=${segments[0]}&video=${segments[2]}"
                    }
                }
            }
        }
        Timber.d("fbLink generateUrl $url")
        return url
    }

    inner class Callback : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding?.progressBar?.isVisible = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            isUrlLoaded = true
            videoCoverHandler.postDelayed(updateUIFromJSPlayerLoaded, 4000L)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            binding?.progressBar?.isVisible = false
        }
    }

    inner class WebAppInterface() {

        @JavascriptInterface
        fun showToast(msg: String?) {
            context?.toast(msg)
        }

        @JavascriptInterface
        fun liveVideoPlayerLoaded(flag: String?) {
            Timber.d("JavascriptInterface LiveVideoPlayerLoaded $flag")
            if (flag == "true") {
                videoCoverHandler.postDelayed(updateUIFromJSPlayerLoaded, 500L)
            }
        }

        @JavascriptInterface
        fun liveVideoFinishStatus(flag: String?) {
            Timber.d("JavascriptInterface LiveVideoFinishStatus $flag")
            if (flag == "true") {
                videoCoverHandler.post(updateUIFromJSPlayerFinish)
            }
        }

    }

    private val updateUIFromJSPlayerLoaded = Runnable {
        Timber.d("JavascriptInterface updateUIFromJSPlayerLoaded called")
        binding?.videoCover?.isVisible = false
        binding?.progressBar?.isVisible = false
    }

    private val updateUIFromJSPlayerFinish = Runnable {
        binding?.liveStatusLayout?.isVisible = true
    }

    private fun callFBMerchant(number: String) {
        try {
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }.also {
                startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //#endregion

    //#region Youtube Player
    private fun initYoutubePlayer() {

        val youTubePlayerView = binding?.youtubePlayerView ?: return
        youTubePlayerView.isVisible = true
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(youtubePlayerLister)

    }

    private val youtubePlayerLister = object : YouTubePlayerListener {

        override fun onReady(youTubePlayer: YouTubePlayer) {
            val videoId = model?.videoId ?: ""
            if (videoId.isNotEmpty()) {
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.play()
            }
        }

        override fun onApiChange(youTubePlayer: YouTubePlayer) {}

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}

        override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}

        override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}

        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {}

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}

        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}

        override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}
    }
    //endregion

    //#region name
    //#endregion
}