package com.bdjobs.app.liveInterview.ui.interview_session

import android.app.Application
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.changeColor
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databinding.FragmentInterviewSessionBinding
import com.bdjobs.app.liveInterview.data.models.Instructions
import com.bdjobs.app.liveInterview.data.models.Messages
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.liveInterview.data.socketClient.CustomSdpObserver
import com.bdjobs.app.liveInterview.data.socketClient.SignalingEvent
import com.bdjobs.app.liveInterview.data.socketClient.SignalingServer
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.demo_connect_employer.streaming.CustomPCObserver
import kotlinx.android.synthetic.main.fragment_instruction_view_page.*
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.RTCConfiguration
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class InterviewSessionFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener,
    SignalingEvent {

    private var jobId = ""
    private var jobTitle = ""
    private var processId = ""
    private var imageLocal: String = ""
    private var imageRemote: String = ""
    private var messageCount = 0
    private var userName = ""

    private val messageList: ArrayList<Messages> = ArrayList()


    private var mSocketId = ""
    private var mRemoteSocketId = ""

    private lateinit var bdjobsUserSession: BdjobsUserSession

    private val args: InterviewSessionFragmentArgs by navArgs()

    private lateinit var binding: FragmentInterviewSessionBinding
    private val internetBroadCastReceiver = ConnectivityReceiver()

    private var mAdapter: ChatAdapter = ChatAdapter()

    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private var eglBaseContext: EglBase.Context? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private val peerConnectionMap = HashMap<String, PeerConnection>()

    val iceServers = ArrayList<PeerConnection.IceServer>()

    private var videoSource: VideoSource? = null
    private var audioSource: AudioSource? = null

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    private var localMediaStream: MediaStream? = null

    private var remoteVideoTrack: VideoTrack? = null

    val pcConstraints = object : MediaConstraints() {
        init {
            optional.add(KeyValuePair("DtlsSrtpKeyAgreement", "true"))
        }
    }
    private var audioConstraints: MediaConstraints? = null

    var videoCapturerAndroid: VideoCapturer? = null

    private var mediaPlayer: MediaPlayer? = null

    var mic_switch: Boolean = true
    var video_switch: Boolean = true
    var isShowingChatView = false
    var isShowingFeedback = false
    var isShowingInstruction = false


    //instruction
    private val images = listOf(
        R.drawable.ic_video_guideline1,
        R.drawable.ic_video_guideline2,
        R.drawable.ic_live_interview_3
    )

    private val instructionsInBengali = listOf<String>(
        "লাইভ ইন্টারভিউ এর শুরুতে ডিভাইস এর মাইক্রোফোন এবং ক্যামেরা পারমিশন দিতে হবে",
        "লাইভ ইন্টারভিউ এর সময় মাউথ পিস / হেডফোন ব্যবহার করা অপরিহার্য, যেন নিয়োগকর্তা আপনার কথা সহজেই বুঝতে পারে",
        "শান্ত এবং নিরিবিলি স্থানে অবস্থান করে লাইভ ইন্টারভিউ তে অংশগ্রহণ করুন যাতে নিয়োগকর্তা এবং আপনার যোগাযোগ এ বিঘ্ন না ঘটে"
    )

    private val instructionsInEnglish = listOf<String>(
        "Allow device's microphone and camera permission to start Live Interview.",
        "Use mouthpieces/ headphones during the Live Interview so that the employer can easily understand you.",
        "Try to attend Live Interview in a quiet place & avoid interruption."
    )

    private val instructions = mutableListOf<Instructions>()

    private val interviewSessionViewModel: InterviewSessionViewModel by viewModels {
        InterviewSessionViewModelFactory(
            LiveInterviewRepository(requireActivity().application as Application),
            args.processID,
            args.applyID,
            args.jobID
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.tag("live").d("onCreateView")
        // Inflate the layout for this fragment
        binding = FragmentInterviewSessionBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = interviewSessionViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = if (args.jobTitle != null) args.jobTitle else ""


        binding.clReadyView.visibility = View.VISIBLE
        binding.clChatView.visibility = View.GONE
        binding.clFeedbackView.visibility = View.GONE
        binding.clInstructionView.visibility = View.GONE

        binding.tvMessageCount.text = "0"


        bdjobsUserSession = BdjobsUserSession(requireContext())
        userName = bdjobsUserSession.userName.toString()
        processId = args.processID.toString()
        jobId = args.jobID.toString()
        jobTitle = args.jobTitle.toString()
        binding.rvMessages.adapter = mAdapter

        startSession()
        setUpObservers()

        binding.toolBar.setNavigationOnClickListener {
            Timber.tag("live").d("toolBarPressed")
            if (isShowingChatView) {
                Timber.tag("live").d("toolBarPressed-isShowingChatView")
                binding.clReadyView.visibility = View.VISIBLE
                binding.clChatView.visibility = View.GONE
                isShowingChatView = false
            } else if (isShowingInstruction) {
                Timber.tag("live").d("toolBarPressed-isShowingInstruction")
                binding.clReadyView.visibility = View.VISIBLE
                binding.clInstructionView.visibility = View.GONE
                isShowingInstruction = false
            } else {
                Timber.tag("live").d("toolBarPressed- not isShowingChatView")
                findNavController().navigateUp()
            }
        }

        binding.rating.onRatingBarChange { ratingBar, rating, fromUser ->
            interviewSessionViewModel.apply {
                this.rating.value = rating.toInt()
                onRatingChanged()
            }
        }

        //Instructions
        for (i in 0 until 3) {
            instructions.add(Instructions(images[i], instructionsInBengali[i]))
        }
        binding.btnNext.text = "পরবর্তী গাইড"

        binding.viewPagerGuideline.adapter = InstructionAdapter(requireContext(), instructions)
        setupIndicators()
        setCurrentIndicator(0)

        binding.viewPagerGuideline?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == instructions.size - 1) {
                    binding.btnNext.hide()
                    binding.btnSkip.hide()
                    binding.btnStart.show()
                    binding.llIndicators.visibility = View.INVISIBLE
                } else {
                    binding.btnStart.hide()
                    binding.btnNext.show()
                    binding.btnSkip.show()
                    binding.llIndicators.show()
                }

                setCurrentIndicator(position)
            }
        })


        binding.btnNext.setOnClickListener {
            if (view_pager_guideline.currentItem + 1 < instructions.size) {
                view_pager_guideline.currentItem = view_pager_guideline.currentItem + 1
            } else {
                Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnStart.setOnClickListener {
            Timber.tag("live").d("btnStart-isShowingInstruction")
            binding.clReadyView.visibility = View.VISIBLE
            binding.clInstructionView.visibility = View.GONE
            isShowingInstruction = false
        }
        binding.btnSkip.setOnClickListener {
            Timber.tag("live").d("btnSkip-isShowingInstruction")
            binding.clReadyView.visibility = View.VISIBLE
            binding.clInstructionView.visibility = View.GONE
            isShowingInstruction = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.tag("live").d("handleOnBackPressed")
                if (isShowingChatView) {
                    Timber.tag("live").d("handleOnBackPressed-isShowingChatView")
                    binding.clReadyView.visibility = View.VISIBLE
                    binding.clChatView.visibility = View.GONE
                    isShowingChatView = false
                } else if (isShowingInstruction) {
                    Timber.tag("live").d("handleOnBackPressed-isShowingInstruction")
                    binding.clReadyView.visibility = View.VISIBLE
                    binding.clInstructionView.visibility = View.GONE
                    isShowingInstruction = false
                } else {
                    Timber.tag("live").d("handleOnBackPressed- not isShowingChatView")
                    findNavController().navigateUp()
                }

            }
        })
    }

    private fun setupIndicators() {
        val indicators = mutableListOf<ImageView>()
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in instructions.indices) {
            indicators.add(
                ImageView(context).apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.indicator_inactive
                        )
                    )
                }
            )
            indicators[i].layoutParams = layoutParams
            binding.llIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        for (i in 0 until ll_indicators.childCount) {
            val imageView: ImageView = ll_indicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }

        }
    }

    private fun setUpObservers() {
        interviewSessionViewModel.apply {

            instructionButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    if (!isShowingInstruction) {
                        binding.clReadyView.visibility = View.GONE
                        binding.clFeedbackView.visibility = View.GONE
                        binding.clChatView.visibility = View.GONE
                        binding.clInstructionView.visibility = View.VISIBLE
                        isShowingInstruction = true
                    }
                }
            })

            chatButtonClickedClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    if (!isShowingChatView) {
                        binding.clReadyView.visibility = View.GONE
                        binding.clFeedbackView.visibility = View.GONE
                        binding.clChatView.visibility = View.VISIBLE
                        isShowingChatView = true
                        binding.tvMessageCount.text = "0"
                        SignalingServer.get()?.sendMessageSeen()
                    }
                }
            })

            yesClick.observe(viewLifecycleOwner, {
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

            noClick.observe(viewLifecycleOwner, {
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

            isShowLoadingCounter.observe(viewLifecycleOwner, {
                if (it) {
                    startAudio()
                }
            })

            countDownFinish.observe(viewLifecycleOwner, {
                if (it) {
                    try {
                        mediaPlayer?.stop()
                        interviewRoomViewCheck(true)
                    } catch (e: Exception) {
                    }
                }
            })

            isShowActionView.observe(viewLifecycleOwner, {
                if (it) {
                    updateVideoViews()
                }
            })

            //instructions


            sendButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    Timber.tag("live").d("sendButtonClickEvent")
                    sendMessage()
                }
            })

            submitButtonClickedClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    Timber.tag("live").d("submitButtonClickedClickEvent")
                    findNavController().navigateUp()
                }
            })

            chatLogFetchSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it) {
                    var messages: Messages?

                    val data = chatLogData.value?.arrChatdata
                    if (data!!.isNotEmpty()) {
                        for (i in data.indices) {
                            val d = data[i]
                            if (d?.hostType == "A" || d?.hostType == "R") {
                                val time = d.chatTime?.split(" ")!![1].split(":")[0] +
                                        ":${d.chatTime.split(" ")[1].split(":")[1]} ${
                                            d.chatTime.split(
                                                " "
                                            )[2]
                                        }"
                                messages = Messages(
                                    if (d.hostType == "A") d.contactName else args.companyName,
                                    d.chatText,
                                    time,
                                    if (d.hostType == "A") 0 else 1
                                )
                                messageList.add(messages)
                                messageCount++
                            }
                        }
                        mAdapter.differ.submitList(messageList)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            })

            postSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it) {
                    SignalingServer.get()?.sendChatMessage(
                        postMessage.value.toString(),
                        imageLocal,
                        imageRemote,
                        messageCount
                    )
                    val nickname = bdjobsUserSession.userName
                    val simpleDateFormat = SimpleDateFormat("h:mm a")
                    val formattedTime = simpleDateFormat.format(Date())
                    // make instance of message
                    val itemType = if (nickname == bdjobsUserSession.userName!!) 0 else 1
                    val messages =
                        Messages(nickname, postMessage.value.toString(), formattedTime, itemType)

                    messageList.add(messages)

                    // notify the adapter to update the recycler view
                    mAdapter.differ.submitList(messageList)
                    mAdapter.notifyDataSetChanged()
                    binding.etWriteMessage.setText("")
                }
            })

            try {
                receivedChatData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    try {
                        val s = it?.get(0).toString()
                        Timber.tag("live").d("Viewmodel post receivedChatData Chat data")
                        val data = JSONObject(s)

                        //extract data from fired event
                        val nickname = args.companyName
                        val message = data.getString("msg")
                        imageLocal = data.getString("imgLocal")
                        imageRemote = data.getString("imgRemote")
                        messageCount = data.getInt("newCount")
                        binding.tvMessageCount.text = messageCount.toString()

                        val simpleDateFormat = SimpleDateFormat("h:mm a")
                        val formattedTime = simpleDateFormat.format(Date())
                        // make instance of message
                        val itemType = if (nickname == bdjobsUserSession.userName!!) 0 else 1
                        val messages = Messages(nickname, message, formattedTime, itemType)

                        messageList.add(messages)

                        // notify the adapter to update the recycler view
                        mAdapter.differ.submitList(messageList)
                        mAdapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Error in receive: ${e.localizedMessage}")
            }

        }
    }

    private fun sendMessage() {
        if (binding.etWriteMessage.text.isNotEmpty()) {
            Timber.tag("live").d("sendMessage")
            interviewSessionViewModel.postChatMessage(binding.etWriteMessage.text.toString())
        }
    }


    private fun startAudio() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bdjobs_calling)
        mediaPlayer?.start()
        mediaPlayer?.isLooping = true
    }

    private fun startSession() {

        SignalingServer.get()?.init(this, processId, userName)

        iceServers.add(
            PeerConnection.IceServer.builder("stun:stun.bdjobs.com")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK)
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("turn:turn.bdjobs.com:3478?transport=udp")
                .setUsername("test1")
                .setPassword(
                    "bdjobs1"
                ).setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK)
                .createIceServer()
        )
        iceServers.add(
            PeerConnection.IceServer.builder("turn:turn.bdjobs.com:3478?transport=tcp")
                .setUsername("test1")
                .setPassword(
                    "bdjobs1"
                ).setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_INSECURE_NO_CHECK)
                .createIceServer()
        )

        eglBaseContext = EglBase.create().eglBaseContext

        val initializationOptions =
            PeerConnectionFactory.InitializationOptions.builder(requireContext())
                .createInitializationOptions()
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
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation",
                "true"
            )
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googEchoCancellation2",
                "true"
            )
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googDAEchoCancellation",
                "true"
            )
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googTypingNoiseDetection",
                "true"
            )
        )
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googAutoGainControl2",
                "true"
            )
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googNoiseSuppression",
                "true"
            )
        )
        audioConstraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "googNoiseSuppression2",
                "true"
            )
        )
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googAudioMirroring", "false"))
        audioConstraints.mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))

        audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", audioSource)
        localAudioTrack?.setEnabled(true)

        videoCapturerAndroid = createVideoCapturer()
        videoCapturerAndroid?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
            videoSource =
                peerConnectionFactory?.createVideoSource(videoCapturerAndroid!!.isScreencast)
            videoCapturerAndroid!!.initialize(
                surfaceTextureHelper,
                requireActivity(),
                videoSource?.capturerObserver
            )
        }
        videoCapturerAndroid?.startCapture(640, 480, 30)


        binding.svLocal.setMirror(true)
        binding.svLocal.init(eglBaseContext, null)
        binding.svLocal.setEnableHardwareScaler(true)
        binding.svLocal.setZOrderMediaOverlay(false)

        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", videoSource)

        localMediaStream = peerConnectionFactory?.createLocalMediaStream("ARDAMS")
        localMediaStream?.apply {
            addTrack(localAudioTrack)
            addTrack(localVideoTrack)
        }

        localVideoTrack?.addSink(binding.svLocal)

        binding.svRemote.setMirror(true)
//        binding.svRemote.setZOrderMediaOverlay(true)
        binding.svRemote.init(eglBaseContext, null)
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

    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    fun View.setMargins(
        left: Int = this.marginLeft,
        top: Int = this.marginTop,
        right: Int = this.marginRight,
        bottom: Int = this.marginBottom,
    ) {
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(left, top, right, bottom)
        }
    }

    private fun updateVideoViews() {
        runOnUiThread {
            try {
                val params: ViewGroup.LayoutParams = binding.svLocal.getLayoutParams()
                params.height = dpToPx(150)
                params.width = dpToPx(140)
                binding.svLocal.setLayoutParams(params)
                binding.svLocal.setMargins(32, 32, 0, 0)
                binding.svLocal.setZOrderMediaOverlay(true)
            } catch (e: Exception) {
                Timber.tag("live").d("Error updateVideoViews : %s", e.toString())
            }
        }
    }

    private fun gotRemoteStream(stream: MediaStream) {
        Timber.d("Got Remote Stream")
        remoteVideoTrack = stream.videoTracks[0]
        requireActivity().runOnUiThread {
            try {
                remoteVideoTrack?.addSink(binding.svRemote)
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
        rtcConfig.enableCpuOveruseDetection = false

        peerConnection =
            peerConnectionFactory?.createPeerConnection(
                rtcConfig,
                pcConstraints,
                object : CustomPCObserver() {

                    override fun onIceCandidate(p0: IceCandidate?) {
                        super.onIceCandidate(p0)

                        Timber.tag("live").d("getOrCreatePeerConnection onIceCandidate${p0} ")
                        if (p0 != null) {
                            SignalingServer.get()?.sendIceCandidate(p0)
                        }

                    }

                    override fun onAddStream(p0: MediaStream?) {
                        super.onAddStream(p0)

                        Timber.tag("live")
                            .d("onAddStream  Remote MediaStream ${p0?.videoTracks?.size} ")

                        gotRemoteStream(p0!!)

                    }
                })


        peerConnection!!.addStream(localMediaStream)
        peerConnectionMap[socketId] = peerConnection
        Timber.tag("live")
            .d("getOrCreatePeerConnection size $socketId ${peerConnectionMap.size} ,  ${peerConnectionMap.values} ")

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
        Timber.d("onResume")
        super.onResume()
        requireActivity().registerReceiver(
            internetBroadCastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        try {
            requireActivity().unregisterReceiver(internetBroadCastReceiver)
            mediaPlayer?.release()
            bdjobsUserSession.isSessionAlreadyStarted = false
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("live").d("onPause")
    }



    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        interviewSessionViewModel.networkCheck(isConnected)
    }

    override fun onEventDisconnected() {
        //  Timber.tag("live").d("Disconnected")
        //  remoteVideoTrack?.removeSink(binding.remoteHostSurfaceView)
    }

    override fun onEventConnectionError(args: Array<Any>) {
        Timber.tag("live").d("ERROR: %s", args[0])
        findNavController().navigateUp()
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
        runOnUiThread {
            try {
                interviewSessionViewModel.employerAvailable()
            } catch (e: Exception) {
                Timber.e("Error employerAvailable: ${e.localizedMessage}")
            }
        }


    }

    override fun on1stUserCheck(args: Array<Any?>?) {
        Timber.tag("live").d("on1stUserCheck: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("firstUserId")
        getOrCreatePeerConnection(mRemoteSocketId, "R")
        runOnUiThread {
            try {
                interviewSessionViewModel.employerAvailable()
            } catch (e: Exception) {
                Timber.e("Error employerAvailable: ${e.localizedMessage}")
            }
        }

    }

    //    D/live: onNewUserStartNew: {"sender":"9cLcAQCDodByQ6H9AAAN","isloginuser":false,"ActiveUser":2,"activeUserCount":2}
    override fun onNewUserStartNew(args: Array<Any?>?) {
        Timber.tag("live").d("onNewUserStartNew: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("sender")

        val isloginuser = argument.getString("isloginuser")
        if (isloginuser.equals("true")) {
            runOnUiThread {
                try {
                    interviewSessionViewModel.interviewRoomViewCheck(true)
                } catch (e: Exception) {
                    Timber.e("Error isloginuser interviewRoomViewCheck: ${e.localizedMessage}")
                }
            }
        }

        getOrCreatePeerConnection(mRemoteSocketId, "R")
        runOnUiThread {
            try {
                interviewSessionViewModel.employerAvailable()
            } catch (e: Exception) {
                Timber.e("Error employerAvailable: ${e.localizedMessage}")
            }
        }

    }

    override fun onReceiveSDP(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveSDP: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("sender")
        val sessionDescriptionJO = argument.getJSONObject("description")

        val peerConnection = getOrCreatePeerConnection(mRemoteSocketId, "R")
        peerConnection.setRemoteDescription(
            CustomSdpObserver(),
            SessionDescription(
                SessionDescription.Type.OFFER,
                sessionDescriptionJO.getString("sdp")
            )
        )
        doAnswer()
    }

    override fun onEndCall() {
        runOnUiThread {
            try {
                Timber.tag("live").d("clFeedbackView visible")
                binding.clReadyView.visibility = View.GONE
                binding.clChatView.visibility = View.GONE
                binding.clFeedbackView.visibility = View.VISIBLE
                isShowingFeedback = true
            } catch (e: Exception) {
                Timber.e("Error onEndCall: ${e.localizedMessage}")
            }
        }
    }

    override fun onInterviewReceive(args: Array<Any?>?) {
//        Timber.tag("live").d("onInterviewReceive: %s", args?.get(0))
//        interviewSessionViewModel.postStartCall()
    }


    override fun onReceiveIceCandidate(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveIceCandidate: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        val candidate = argument.getJSONObject("candidate")
        val peerConnection = getOrCreatePeerConnection(mRemoteSocketId, "R")
        peerConnection.addIceCandidate(
            IceCandidate(
                candidate.getString("sdpMid"),
                candidate.getInt("sdpMLineIndex"),
                candidate.getString("candidate")
            )
        )
    }

    override fun onReceiveCall(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveCall: %s", args?.get(0))
        runOnUiThread {
            try {
                interviewSessionViewModel.loadingCounterShowCheck(true)
                //  interviewSessionViewModel.postStartCall()
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Error in call receive: ${e.localizedMessage}")
            }
        }
    }


    override fun onReceiveChat(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveChat: %s", args?.get(0))
        runOnUiThread {
            try {
                interviewSessionViewModel.receivedData(args)
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("Error in receive: ${e.localizedMessage}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("live").d("onDestroyView")
        SignalingServer.get()?.destroy()
        try {
            localVideoTrack?.removeSink(binding.svLocal)
            remoteVideoTrack?.removeSink(binding.svRemote)
            videoCapturerAndroid?.stopCapture()
            localVideoTrack?.dispose()
            localAudioTrack?.dispose()
            remoteVideoTrack?.dispose()
            binding.svRemote.release()
            binding.svLocal.release()
        } catch (e: Exception) {
            Timber.tag("live").d(e.toString())
        }
    }
}