package com.bdjobs.app.ajkerDeal.ui.video_shopping.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@KoinApiExtension
class HomeLiveExoRecyclerView : RecyclerView, KoinComponent {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val exoPlayer: SimpleExoPlayer by inject()
    private var handlerMain = Handler(Looper.getMainLooper())
    private var workRunnable: Runnable? = null

    private var viewHolderParent: View? = null
    private var holder: HomeLiveShoppingAdapter.ViewHolder? = null

    private var playPosition = -1

    init {
        exoPlayer.apply {
            repeatMode = Player.REPEAT_MODE_OFF
            volume = 0f
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            //addAnalyticsListener(EventLogger(this.trackSelector as DefaultTrackSelector))
        }
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //todo it should run
                    //run()
                } else {
                    resetVideoView()
                }
            }
        })

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view) {
                    resetVideoView()
                }
            }
        })

        exoPlayer.addListener(object : Player.EventListener {

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {

                    }
                    Player.STATE_READY -> {
                        holder?.binding?.videoCover?.visibility = View.INVISIBLE
                        //holder?.binding?.playIcon?.visibility = View.INVISIBLE
                    }
                    Player.STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = false
                    }
                }
            }
        })
        /*exoPlayer.addVideoListener(object : VideoListener{
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                super.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio)

                holder?.binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                exoPlayer.videoScalingMode = Renderer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        })*/
    }

    private fun findVisibleItem(): List<Int> {

        /*return if (layoutManager is GridLayoutManager) {
            val firstVisibleItemPosition: Int = (layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
            val lastVisibleItemPosition: Int = (layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
            listOf(firstVisibleItemPosition, firstVisibleItemPosition)
        } else {
            listOf()
        }*/

        val firstPosition = (layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
        val lastPosition = (layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()

        Timber.tag("debugExoRecyclerview").d("HomeLive firstPosition $firstPosition lastPosition $lastPosition")
        return if (firstPosition == 0 && lastPosition == 0) {
            if ((adapter?.itemCount ?: 0) > 1) {
                listOf()
            } else {
                listOf(0)
            }
        } else {
            (firstPosition..lastPosition).toList().distinct()
        }
    }

    fun run() {
        val runTime = 1000 * 10L
        val visibleIndexList = findVisibleItem()
        Timber.tag("debugExoRecyclerview").d("HomeLive visibleIndexList $visibleIndexList")
        var currentIndex = 0
        if (visibleIndexList.isEmpty()) return
        var currentPosition = visibleIndexList[currentIndex]
        if (currentPosition == -1) return

        if (playVideo(currentPosition)) {
            workRunnable?.let { handlerMain.removeCallbacks(it) }
            workRunnable = object : Runnable {
                override fun run() {
                    Timber.tag("debugExoRecyclerview").d("HomeLive Runnable called for position $currentPosition")
                    exoPlayer.playWhenReady = false
                    holder?.binding?.playerView?.player = null
                    holder?.binding?.videoCover?.visibility = View.VISIBLE
                    //holder?.binding?.playIcon?.visibility = View.VISIBLE
                    currentIndex++
                    if (currentIndex in 0..visibleIndexList.lastIndex) {
                        currentPosition = visibleIndexList[currentIndex]
                        playVideo(currentPosition)
                        handlerMain.postDelayed(this, runTime)
                    }
                }
            }
            handlerMain.postDelayed(workRunnable!!, runTime)
        }

    }

    fun playVideo(currentPosition: Int): Boolean {

        if (currentPosition == RecyclerView.NO_POSITION) return false
        if (currentPosition == playPosition) return false

        val adapter = (adapter as HomeLiveShoppingAdapter)
        val viewHolder = adapter.getViewByPosition(currentPosition)
        if (viewHolder is HomeLiveShoppingAdapter.ViewHolder) {
            holder = viewHolder
        } else {
            playPosition = -1
            return false
        }

        if (holder == null) {
            playPosition = -1
            return false
        }
        playPosition = currentPosition
        Timber.tag("debugExoRecyclerview").d("HomeLive playVideo currentPosition $currentPosition")
        viewHolderParent = holder!!.binding.root
        val model: LiveListData = adapter.getDataByPosition(currentPosition) ?: return false
        //if (model.statusName != "live") return false

        val videoUrl = model.videoChannelLink ?: return false
        val mediaSource = MediaSourceBuilder().build(context, Uri.parse(videoUrl), true)
        //val clipMediaSource = ClippingMediaSource(mediaSource, 1000 * 10L)
        exoPlayer.setMediaSource(mediaSource, true)
        exoPlayer.prepare()

        val binding = holder!!.binding
        binding.playerView.setKeepContentOnPlayerReset(true)
        binding.playerView.player = exoPlayer
        //binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        exoPlayer.volume = 0f
        exoPlayer.playWhenReady = true

        return true
    }

    fun resetVideoView() {
        playPosition = -1
        exoPlayer.playWhenReady = false
        holder?.binding?.playerView?.player = null
        holder?.binding?.videoCover?.visibility = View.VISIBLE
        //holder?.binding?.playIcon?.visibility = View.VISIBLE
        workRunnable?.let {
            handlerMain.removeCallbacks(it)
        }
    }

    fun release() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        workRunnable?.let {
            handlerMain.removeCallbacks(it)
        }
        viewHolderParent = null
        holder = null
    }

}