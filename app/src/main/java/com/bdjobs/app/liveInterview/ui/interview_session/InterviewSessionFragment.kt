package com.bdjobs.app.liveInterview.ui.interview_session

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.changeColor
import com.bdjobs.app.databinding.FragmentInterviewSessionBinding
import com.bdjobs.app.liveInterview.data.socketClient.CustomSdpObserver
import com.bdjobs.app.liveInterview.data.socketClient.SignalingEvent
import com.bdjobs.app.liveInterview.data.socketClient.SignalingServer
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.demo_connect_employer.streaming.CustomPCObserver
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.RTCConfiguration
import timber.log.Timber
import java.util.*


class InterviewSessionFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener, SignalingEvent {

    private var videoSource: VideoSource? = null
    private var audioSource: AudioSource? = null

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null


    private var eglBaseContext: EglBase.Context? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private val peerConnectionMap = HashMap<String, PeerConnection>()

    private var mSocketId = ""
    private var mRemoteSocketId = ""

    private var localMediaStream: MediaStream? = null
    private var remoteMediaStream: MediaStream? = null

    private var remoteVideoTrack: VideoTrack? = null

    private var remoteViews = ArrayList<SurfaceViewRenderer>()
    private var remoteViewsIndex = 0

    val iceServers = ArrayList<PeerConnection.IceServer>()

    private var audioConstraints: MediaConstraints? = null
    private var videoConstraints: MediaConstraints? = null


    var mic_switch: Boolean = true
    var video_switch: Boolean = true

    private lateinit var bdjobsUserSession: BdjobsUserSession

    private val args: InterviewSessionFragmentArgs by navArgs()
    private var processId = ""

    private lateinit var binding: FragmentInterviewSessionBinding
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private val interviewSessionViewModel: InterviewSessionViewModel by navGraphViewModels(R.id.interviewSessionFragment)
    private var mediaPlayer: MediaPlayer? = null

    val pcConstraints = object : MediaConstraints() {
        init {
            optional.add(KeyValuePair("DtlsSrtpKeyAgreement", "true"))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentInterviewSessionBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = interviewSessionViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = if (args.jobTitle != null) args.jobTitle else ""

        bdjobsUserSession = BdjobsUserSession(requireContext())
        processId = args.processID.toString()

        startSession()
        setUpObservers()

        binding.fabMessage.setOnClickListener { findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToChatFragment(args.processID)) }

    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun createMsgCounter() {
        binding.fabMessage.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val badgeDrawable: BadgeDrawable = BadgeDrawable.create(requireContext())
                badgeDrawable.apply {
                    number = 2
                    horizontalOffset = 30
                    verticalOffset = 20
                    backgroundColor = Color.RED
                }

                BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.fabMessage, null)

                binding.fabMessage.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })


    }

    private fun setUpObservers() {
        interviewSessionViewModel.apply {
            messageButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToChatFragment(args.processID))
            })

            instructionButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToInstructionLandingFragment(args.jobID, args.jobTitle, args.processID))
            })

            yesClick.observe(viewLifecycleOwner, Observer {
                if (it) {
                    binding.apply {
                        btnYesReady.changeColor(R.color.btn_green)
                    }
                } else {
                    binding.apply {
                        btnYesReady.changeColor(R.color.btn_ash)
                    }
                }
            })

            noClick.observe(viewLifecycleOwner, Observer {
                if (it) {
                    binding.apply {
                        btnNoReady.changeColor(R.color.btn_green)
                    }
                } else {
                    binding.apply {
                        btnNoReady.changeColor(R.color.btn_ash)
                    }
                }
            })

            toggleVideoClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    toggleVideo()
                }
            })


            toggleAudioClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    toggleAudio()
                }
            })

            /* isWaitingForEmployers.observe(viewLifecycleOwner, Observer {
                 if (it) {
                     Handler(Looper.myLooper()!!).postDelayed({
                         loadingCounterShowCheck(true)
                         startAudio()
                     },1500)
                 }
             })*/

            isShowLoadingCounter.observe(viewLifecycleOwner, Observer {
                if (it) {
                    startAudio()
                }
            })

            countDownFinish.observe(viewLifecycleOwner, Observer {
                if (it) {
                    try {
                        mediaPlayer?.stop()
                    } catch (e: Exception) {
                    }
                }
            })

            isShowInterviewRoomView.observe(viewLifecycleOwner, Observer {
                if (it) {
                    binding.cameraLocalReady.apply {
                        release()
                        visibility = View.GONE
                    }
                    createMsgCounter()
                }
            })

            isShowParentReadyView.observe(viewLifecycleOwner, Observer {
                if (it) {
                    initializeLocalCamera()
                }
            })
        }
    }

    private fun initializeLocalCamera() {
        eglBaseContext = EglBase.create().eglBaseContext

        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(requireContext()).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
                eglBaseContext,
                true,
                true
        )
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)


        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory()

        audioConstraints = MediaConstraints()
        val audioConstraints = MediaConstraints()

        // add all existing audio filters to avoid having echos
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation2", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl2", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression2", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAudioMirroring", "false"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))

        audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
        localAudioTrack?.setEnabled(true)

        val videoCapturerAndroid: VideoCapturer? = createVideoCapturer()
        videoCapturerAndroid?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
            videoSource = peerConnectionFactory?.createVideoSource(videoCapturerAndroid.isScreencast)
            videoCapturerAndroid.initialize(surfaceTextureHelper, requireActivity(), videoSource?.capturerObserver)
        }
        videoCapturerAndroid?.startCapture(640, 480, 30)


        binding.cameraLocalReady.setMirror(true)
        binding.cameraLocalReady.init(eglBaseContext, null)
        binding.cameraLocalReady.setEnableHardwareScaler(true)
        binding.cameraLocalReady.setZOrderMediaOverlay(true)

        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)

        localMediaStream = peerConnectionFactory?.createLocalMediaStream("ARDAMS")
        localMediaStream?.apply {
            addTrack(localAudioTrack)
            addTrack(localVideoTrack)
        }

        localVideoTrack?.addSink(binding.cameraLocalReady)
    }

    private fun startAudio() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bdjobs_calling)
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
    }

    private fun startSession() {
//        initializeLocalCamera()

        SignalingServer.get()?.init(this, processId)
        iceServers.add(
                PeerConnection.IceServer.builder("stun:stun.bdjobs.com")
                        .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK).createIceServer()
        )
        iceServers.add(
                PeerConnection.IceServer.builder("turn:turn.bdjobs.com:3478?transport=udp").setUsername("test1")
                        .setPassword(
                                "bdjobs1"
                        ).setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK)
                        .createIceServer()
        )
        iceServers.add(
                PeerConnection.IceServer.builder("turn:turn.bdjobs.com:3478?transport=tcp").setUsername("test1")
                        .setPassword(
                                "bdjobs1"
                        ).setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK)
                        .createIceServer()
        )

        eglBaseContext = EglBase.create().getEglBaseContext()

        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(requireContext()).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)


        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
                eglBaseContext,
                true,
                true
        )
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)


        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory()

        audioConstraints = MediaConstraints()
        val audioConstraints = MediaConstraints()

        // add all existing audio filters to avoid having echos
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation2", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl2", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression2", "true"))

        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAudioMirroring", "false"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))

        audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
        localAudioTrack?.setEnabled(true)

        val videoCapturerAndroid: VideoCapturer? = createVideoCapturer()
        videoCapturerAndroid?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
            videoSource = peerConnectionFactory?.createVideoSource(videoCapturerAndroid.isScreencast)
            videoCapturerAndroid.initialize(surfaceTextureHelper, requireActivity(), videoSource?.capturerObserver)
        }
        videoCapturerAndroid?.startCapture(320, 240, 30)


        binding.localMeSurfaceView.setMirror(true)
        binding.localMeSurfaceView.init(eglBaseContext, null)
        binding.localMeSurfaceView.setEnableHardwareScaler(true)
        binding.localMeSurfaceView.setZOrderMediaOverlay(true)

        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)

        localMediaStream = peerConnectionFactory?.createLocalMediaStream("ARDAMS")
        localMediaStream?.apply {
            addTrack(localAudioTrack)
            addTrack(localVideoTrack)
        }

        localVideoTrack?.addSink(binding.localMeSurfaceView)
//
//        remoteViews.add(binding.remoteHostSurfaceView)
//        remoteViews.add(binding.remoteOneSurfaceView)
//        remoteViews.add(binding.remoteTwoSurfaceView)
//
//        for (remoteView in remoteViews) {
//            remoteView.setMirror(false)
//            try {
//                remoteView.init(eglBaseContext, null)
//            } catch (e: Exception) {
//                Timber.e("Exception: ${e.localizedMessage}")
//            }
//        }
        binding.remoteHostSurfaceView.setMirror(true)
        binding.remoteHostSurfaceView.init(eglBaseContext, null)
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return if (useCamera2()) {
            createCameraCapturer(Camera2Enumerator(requireContext()))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(requireContext())
    }

    private fun gotRemoteStream(stream: MediaStream) {
        Timber.d("Got Remote Stream")
        remoteVideoTrack = stream.videoTracks[0]
        requireActivity().runOnUiThread {
            try {
                remoteVideoTrack?.addSink(binding.remoteHostSurfaceView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    private fun getOrCreatePeerConnection(socketId: String, role: String): PeerConnection {

        Timber.tag("live").d("getOrCreatePeerConnection socketId $socketId role $role")

        var peerConnection = peerConnectionMap[socketId]


        if (peerConnection != null) {
            return peerConnection
        }

        val rtcConfig = RTCConfiguration(iceServers)
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.ALL

        peerConnection =
                peerConnectionFactory?.createPeerConnection(rtcConfig, pcConstraints, object : CustomPCObserver() {

                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)

                        Timber.tag("live").d("getOrCreatePeerConnection onIceCandidate${p0} ")
                        if (p0 != null) {
                            SignalingServer.get()?.sendIceCandidate(p0)
                        }

                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)

                        Timber.tag("live").d("onAddStream  Remote MediaStream ${p0?.videoTracks?.size} ")

                        gotRemoteStream(p0!!)

                    }
                })


        peerConnection!!.addStream(localMediaStream)
        peerConnectionMap[socketId] = peerConnection
        Timber.tag("live").d("getOrCreatePeerConnection size $socketId ${peerConnectionMap.size} ,  ${peerConnectionMap.values} ")

        return peerConnection
    }

    private fun doAnswer() {
        val peerConnection = getOrCreatePeerConnection(mRemoteSocketId, "R")

        peerConnection?.createAnswer(object : CustomSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                peerConnection.setLocalDescription(CustomSdpObserver(), sessionDescription)
                if (sessionDescription != null) {
                    SignalingServer.get()?.sendSessionDescription(sessionDescription)
                }
            }
        }, pcConstraints)
    }


    private fun toggleVideo() {

        if (localMediaStream != null && localMediaStream?.videoTracks?.size!! > 0) {
            video_switch = !video_switch

            localMediaStream?.videoTracks!![0].setEnabled(video_switch)

            if (video_switch) binding.localVideoControl.setImageResource(R.drawable.ic_video_on)
            else binding.localVideoControl.setImageResource(R.drawable.ic_video_off)
        }

    }

    private fun toggleAudio() {
        if (localMediaStream != null && localMediaStream?.audioTracks?.size!! > 0) {
            mic_switch = !mic_switch

            localMediaStream?.audioTracks!![0].setEnabled(mic_switch)

            if (mic_switch) binding.localMicControl.setImageResource(R.drawable.ic_mic_on)
            else binding.localMicControl.setImageResource(R.drawable.ic_microphone_off)
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(internetBroadCastReceiver)
        mediaPlayer?.release()
        super.onDestroy()
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        interviewSessionViewModel.networkCheck(isConnected)
    }

    override fun onEventDisconnected() {
        Timber.tag("live").d("Disconnected")
        remoteVideoTrack?.removeSink(binding.remoteHostSurfaceView)
    }

    override fun onEventConnectionError(args: Array<Any>) {
        Timber.tag("live").d("ERROR: %s", args[0])
    }

    override fun setLocalSocketID(id: String) {
        Timber.tag("live").d("setLocalSocketID: %s", id)
        mSocketId = id
    }

    override fun onNewUser(args: Array<Any?>?) {
        Timber.tag("live").d("onNewUser: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("socketId")
        getOrCreatePeerConnection(mRemoteSocketId, "R")
    }

    override fun on1stUserCheck(args: Array<Any?>?) {
        Timber.tag("live").d("on1stUserCheck: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("firstUserId")
        getOrCreatePeerConnection(mRemoteSocketId, "R")
    }

    override fun onNewUserStartNew(args: Array<Any?>?) {
        Timber.tag("live").d("onNewUserStartNew: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("sender")
        getOrCreatePeerConnection(mRemoteSocketId, "R")
    }

    override fun onReceiveSDP(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveSDP: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("sender")
        val sessionDescriptionJO = argument.getJSONObject("description")

        val peerConnection = getOrCreatePeerConnection(mRemoteSocketId, "R")
        peerConnection!!.setRemoteDescription(
                CustomSdpObserver(),
                SessionDescription(
                        SessionDescription.Type.OFFER,
                        sessionDescriptionJO.getString("sdp")
                )
        )
        doAnswer()
    }

    override fun onReceiveIceCandidate(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveIceCandidate: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        val candidate = argument.getJSONObject("candidate")
        val peerConnection = getOrCreatePeerConnection(mRemoteSocketId, "R")
        peerConnection.addIceCandidate(IceCandidate(candidate.getString("sdpMid"), candidate.getInt("sdpMLineIndex"), candidate.getString("candidate")))
    }

    override fun onReceiveCall(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveCall: %s", args?.get(0))
        interviewSessionViewModel.loadingCounterShowCheck(true)
        //1st layout disappear + count down + then 2nd layout appear.
    }

    override fun onReceiveChat(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveChat: %s", args?.get(0))
    }
}