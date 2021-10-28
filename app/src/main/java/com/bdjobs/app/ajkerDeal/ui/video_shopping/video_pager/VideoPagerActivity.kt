package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.ajkerDeal.ui.video_shopping.VideoShoppingViewModel
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.broadcast_receivers.ConnectivityReceiver
import com.bdjobs.app.databinding.ActivityVideoPagerBinding
import com.bdjobs.app.ajkerDeal.ui.video_shopping.player.MediaSourceBuilder
import com.bdjobs.app.ajkerDeal.utilities.SessionManager
import com.bdjobs.app.ajkerDeal.utilities.reduceDragSensitivity
import com.bdjobs.app.ajkerDeal.utilities.toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import org.koin.android.ext.android.inject
import timber.log.Timber

class VideoPagerActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityVideoPagerBinding
    private lateinit var pagerAdapter: VideoPagingAdapter

    private lateinit var trackSelector: DefaultTrackSelector
    private val dataList: MutableList<CatalogData> = mutableListOf()
    private lateinit var sessionManager: SessionManager
    private lateinit var connectivityReceiver : ConnectivityReceiver

    private val permissionDrawOverOtherApp = 2048
    private var playbackPosition: Long = 0

    var isFromFloatWidget: Boolean = false
    private var cacheFlag: Boolean = false
    private var isLiveShow: Boolean = false
    private var catalogId: Int = 0
    private var subCategoryId: Int = 0
    private var subSubCategoryId: Int = 0

    private var isLoading: Boolean = false
    private var selectedTrackIndex = -1
    private var shouldResume: Boolean = false
    private var firstTrackSelect: Boolean = false

    private val viewModel: VideoShoppingViewModel by inject()
    private val exoPlayer: SimpleExoPlayer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // bundle Data
        val currentPlayingIndex = intent?.getIntExtra("playIndex", 0) ?: 0
        playbackPosition = intent?.getLongExtra("playbackPosition", 0L) ?: 0L
        isFromFloatWidget = intent?.getBooleanExtra("isFromFloatWidget", false) ?: false
        val parcelDataList: List<CatalogData> = intent?.getParcelableArrayListExtra("videoList") ?: mutableListOf()
        cacheFlag = intent?.getBooleanExtra("noCache", false) ?: false
        isLiveShow = intent?.getBooleanExtra("isLiveShow", false) ?: false
        catalogId = intent?.getIntExtra("categoryId", 0) ?: 0
        subCategoryId = intent?.getIntExtra("subCategoryId", 0) ?: 0
        subSubCategoryId = intent?.getIntExtra("subSubCategoryId", 0) ?: 0
        dataList.clear()
        dataList.addAll(parcelDataList)

        sessionManager = SessionManager

        if (!cacheFlag) {
            //startPreLoadingService(dataList.map { it.videoLink1 })
        }
        trackSelector = exoPlayer.trackSelector as DefaultTrackSelector
        with(exoPlayer) {
            repeatMode = Player.REPEAT_MODE_OFF
            volume = 1f
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            addListener(eventListener)
        }
        //exoPlayer.addAnalyticsListener(EventLogger(trackSelector))

        pagerAdapter = VideoPagingAdapter(this, dataList, exoPlayer)
        with(binding.viewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 1
            setCurrentItem(currentPlayingIndex, false)
            setPageTransformer(null)
            //isUserInputEnabled = false //disable scrolling
            reduceDragSensitivity()
        }
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val model = dataList[position]
                exoPlayer.playWhenReady = false
                exoPlayer.stop()
                exoPlayer.clearMediaItems()

                if (model.facebookVideoUrl.isEmpty() && !model.isYoutubeVideo) {
                    preparePlayer(Uri.parse(model.videoLink1))
                    exoPlayer.playWhenReady = true
                }
                //preparePlayer(Uri.parse(model.videoLink1))
                //exoPlayer.playWhenReady = true
                loadMoreVideo(position, dataList.size)
            }
        })
        if (currentPlayingIndex != 0) {
            val model = dataList[currentPlayingIndex]
            exoPlayer.playWhenReady = false
            if (model.facebookVideoUrl.isEmpty() && !model.isYoutubeVideo) {
                preparePlayer(Uri.parse(model.videoLink1))
                exoPlayer.playWhenReady = true
            }
            //preparePlayer(Uri.parse(model.videoLink1))
            //exoPlayer.playWhenReady = true
        }

        checkIsCustomerBlocked()
        connectivityReceiver = ConnectivityReceiver()
    }

    private val eventListener = object : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            Timber.d("debugPlayerState state $state")
            when (state) {
                Player.STATE_BUFFERING -> {
                    playbackStateUpdate(state)
                }
                Player.STATE_READY -> {
                    playbackStateUpdate(state)

                    if (cacheFlag && !firstTrackSelect) {
                        firstTrackSelect = true
                        selectTrack(sessionManager.selectedVideoQuality)
                    }
                }
                Player.STATE_ENDED -> {
                    playbackStateUpdate(state)
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            Timber.d("onPlayerError ${error.type}")
            playbackErrorUpdate(error.type)
        }
    }

    private fun playbackStateUpdate(state: Int) {
        val currentFragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
        if (currentFragment is LiveShowFragment) {
            currentFragment.onPlayerStateChanged(state)
            Timber.d("debugPlayerState currentFragment is LiveShowFragment")
        } else {
            Timber.d("debugPlayerState findFragmentByTag is null")
        }
    }

    private fun playbackErrorUpdate(state: Int) {
        val currentFragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
        if (currentFragment is LiveShowFragment) {
            currentFragment.onErrorState(state)
            Timber.d("debugPlayerState currentFragment is LiveShowFragment")
        } else {
            Timber.d("debugPlayerState findFragmentByTag is null")
        }
    }

    private fun preparePlayer(uri: Uri){
        Timber.d("preparePlayer $uri")
        val mediaSource = MediaSourceBuilder().build(this, uri, cacheFlag)
        exoPlayer.setMediaSource(mediaSource, true)
        exoPlayer.prepare()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            showPictureInPicture()
        } else {
            super.onBackPressed()
        }*/
    }

    override fun onStart() {
        super.onStart()
        if (shouldResume) {
            val position = binding.viewPager.currentItem
            preparePlayer(Uri.parse(dataList[position].videoLink1))
            exoPlayer.playWhenReady = true
        }
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        shouldResume = true
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        //exoPlayer.release()
        //finishAndRemoveTask()
        ConnectivityReceiver.connectivityReceiverListener = null
        unregisterReceiver(connectivityReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.removeListener(eventListener)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        //showPictureInPicture()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.playerView.visibility = View.VISIBLE
            binding.playerView.player = exoPlayer
            exoPlayer.volume = 0f
        } else {
            binding.playerView.visibility = View.GONE
            binding.playerView.player = null
            exoPlayer.volume = 1f
        }
    }

    private fun showPictureInPicture() {

        /*val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        if (appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).uid, packageName) == AppOpsManager.MODE_ALLOWED){
            //Picture in Picture is enabled, yay!
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val display = windowManager.defaultDisplay
                val point = Point()
                display.getSize(point)
                val pipParams = PictureInPictureParams.Builder().apply {
                    setAspectRatio(Rational(point.x, point.y))
                }
                enterPictureInPictureMode(pipParams.build())
            } else {
                enterPictureInPictureMode()
            }
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                /*or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun nextVideoPage() {
        val currentIndex = binding.viewPager.currentItem
        val nextIndex = currentIndex + 1
        if (nextIndex < dataList.size) {
            binding.viewPager.currentItem = nextIndex
        }
    }

    fun previousVideoPage() {
        val currentIndex = binding.viewPager.currentItem
        val previousIndex = currentIndex - 1
        if (previousIndex >= 0) {
            binding.viewPager.currentItem = previousIndex
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadMoreVideo(index: Int, count: Int) {

        if (isLiveShow) return
        if (index > (count - 5) && !isLoading) {
            isLoading = true
            viewModel.getCatalogListByCat(count, catalogId, subCategoryId, subSubCategoryId).observe(this, Observer { list ->
                isLoading = false
                if (list.isEmpty()) {
                    isLoading = true
                } else {
                    dataList.addAll(list)
                    val start = dataList.size
                    pagerAdapter.notifyItemRangeInserted(start, list.size)
                }
            })
        }
    }

    fun trackSelectorDialog() {
        Timber.tag("logTrack").d("trackSelectorDialog called")
        val resolutions: MutableList<String> = mutableListOf()
        resolutions.add("Auto")

        val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return
        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackType = mappedTrackInfo.getRendererType(rendererIndex)
            if (trackType == C.TRACK_TYPE_VIDEO) {
                val trackGroupArray: TrackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
                val trackCount = trackGroupArray.get(0).length
                for (trackIndex in 0 until trackCount) {
                    val format = trackGroupArray.get(0).getFormat(trackIndex)
                    val width = format.width
                    val height = format.height
                    val bitrate = format.bitrate
                    //val trackName: String = DefaultTrackNameProvider(resources).getTrackName(format)
                    //val trackName = "${height}p ${width}x${height} ${df.format(bitrate/1000000f).toFloat()} Mbps"
                    val trackName = "${height}p"
                    Timber.tag("logTrack").d("track item $trackIndex: trackName: $trackName")

                    resolutions.add(trackName)
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose resolution")
        builder.setSingleChoiceItems(resolutions.toTypedArray(), selectedTrackIndex+1) { dialog, which ->
            Timber.d("setSingleChoiceItems which $which")
            selectedTrackIndex = which - 1
        }
        builder.setPositiveButton("OK") { dialog, which ->
            selectTrack(selectedTrackIndex)
            sessionManager.selectedVideoQuality = selectedTrackIndex
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.show()
        dialog.window?.decorView?.systemUiVisibility = window.decorView.systemUiVisibility
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun selectTrack(selectionIndex: Int) {
        Timber.tag("logTrack").d("log tracks clicked")

        val mappedTrackInfo: MappingTrackSelector.MappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return
        val parameters = trackSelector.parameters
        val builder = parameters.buildUpon()
        if (selectedTrackIndex == -1) {
            builder.clearSelectionOverrides()
        } else {
            for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
                val trackType = mappedTrackInfo.getRendererType(rendererIndex)
                if (trackType == C.TRACK_TYPE_VIDEO) {
                    builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, false)
                    //{"data":0,"groupIndex":1,"length":1,"reason":2,"tracks":[0]}
                    val groupIndex = 0
                    val tracks = intArrayOf(selectionIndex)
                    val reason = C.SELECTION_REASON_MANUAL
                    val data = 0
                    val override = DefaultTrackSelector.SelectionOverride(groupIndex, tracks, trackType)
                    builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override)
                }
            }
        }
        trackSelector.setParameters(builder)

    }

    private fun checkIsCustomerBlocked() {
        viewModel.checkIsCustomerBlock(sessionManager.userId).observe(this, Observer { flag ->
            sessionManager.customerBlock = flag
        })
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            this.toast("ইন্টারনেট কানেকশন সমস্যা হচ্ছে")
        }
    }

}