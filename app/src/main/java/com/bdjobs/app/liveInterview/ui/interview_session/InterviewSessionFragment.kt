package com.bdjobs.app.liveInterview.ui.interview_session

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_instruction_view_page.*
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class InterviewSessionFragment : Fragment(), ConnectivityReceiver.ConnectivityReceiverListener,
    SignalingEvent {

    private val crashReport = FirebaseCrashlytics.getInstance()
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var jobId = ""
    private var jobTitle = ""
    private var processId = ""
    private var imageLocal: String = ""
    private var imageRemote: String = ""
    private var messageCount = 0
    private var userName = ""

    private var mSocketId = ""
    private var mRemoteSocketId = ""

    private val messageList: ArrayList<Messages> = ArrayList()
    private var mAdapter: ChatAdapter = ChatAdapter()


    private val args: InterviewSessionFragmentArgs by navArgs()

    private lateinit var binding: FragmentInterviewSessionBinding
    private val internetBroadCastReceiver = ConnectivityReceiver()


    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private var eglBaseContext: EglBase.Context? = null
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private val peerConnectionMap = HashMap<String, PeerConnection>()

    val iceServers = ArrayList<PeerConnection.IceServer>()

    private var localVideoSource: VideoSource? = null
    private var localAudioSource: AudioSource? = null

    private var localMediaStream: MediaStream? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null


    private var remoteMediaStream: MediaStream? = null
    private var remoteVideoTrack: VideoTrack? = null
    private var remoteAudioTrack: AudioTrack? = null



    val pcConstraints = object : MediaConstraints() {
        init {
            optional.add(KeyValuePair("DtlsSrtpKeyAgreement", "true"))
            optional.add(KeyValuePair("OfferToReceiveAudio", "true"))
            optional.add(KeyValuePair("OfferToReceiveVideo", "true"))
        }
    }

    val audioConstraints = object : MediaConstraints() {
        init {
            optional.add(KeyValuePair("googEchoCancellation", "true"))
            optional.add(KeyValuePair("googEchoCancellation2", "true"))
            optional.add(KeyValuePair("googDAEchoCancellation", "true"))
            optional.add(KeyValuePair("googTypingNoiseDetection", "true"))
            optional.add(KeyValuePair("googAutoGainControl", "true"))
            optional.add(KeyValuePair("googAutoGainControl2", "true"))
            optional.add(KeyValuePair("googNoiseSuppression", "true"))
            optional.add(KeyValuePair("googNoiseSuppression2", "true"))
            optional.add(KeyValuePair("googAudioMirroring", "false"))
            optional.add(KeyValuePair("googHighpassFilter", "true"))
        }
    }

    var videoCapturerAndroid: VideoCapturer? = null
    private var mediaPlayer: MediaPlayer? = null


    //instruction
    private val images = listOf(R.drawable.ic_video_guideline1, R.drawable.ic_video_guideline2, R.drawable.ic_live_interview_3)

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentInterviewSessionBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = interviewSessionViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("live").d("onViewCreated")

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        //set device audio to headphone only and max volume

        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager?.isMicrophoneMute= false
//        audioManager?.isSpeakerphoneOn = false
        audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION

        val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        Timber.tag("live").d("currentVolume $currentVolume - maxVolume $maxVolume")
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)

        //keep screen on
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = if (args.jobTitle != null) args.jobTitle else ""


        bdjobsUserSession = BdjobsUserSession(requireContext())
        userName = bdjobsUserSession.userName.toString()
        processId = args.processID.toString()
        jobId = args.jobID.toString()
        jobTitle = args.jobTitle.toString()

        crashReport.setUserId(userName)

        setUpObservers()
        startSession()

        binding.rvMessages.adapter = mAdapter

        binding.toolBar.setNavigationOnClickListener {
            Timber.tag("live").d("toolBar Back Button Clicked")
            interviewSessionViewModel.apply { canGoBackToDetailFragment() }
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
            Timber.tag("live").d("Instructions btnStart Clicked")
            setCurrentIndicator(0)
            view_pager_guideline.currentItem = 0
            interviewSessionViewModel.apply {
                isReadyViewVisible.value = true
                isReadyViewHidden.value = false

                isInstructionVisible.value = false
                isInstructionHidden.value = true
            }
        }
        binding.btnSkip.setOnClickListener {
            Timber.tag("live").d("Instructions btnSkip Clicked")
            setCurrentIndicator(0)
            interviewSessionViewModel.apply {
                isReadyViewVisible.value = true
                isReadyViewHidden.value = false

                isInstructionVisible.value = false
                isInstructionHidden.value = true
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.tag("live").d("System Back Button Pressed")
                interviewSessionViewModel.apply { canGoBackToDetailFragment() }
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

            /* Ready View Start */
            isReadyViewVisible.observe(viewLifecycleOwner, { if (it) { binding.apply { clReadyView.visibility = View.VISIBLE } } })
            isReadyViewHidden.observe(viewLifecycleOwner, { if (it) { binding.apply { clReadyView.visibility = View.GONE } } })

            isInstructionVisible.observe(viewLifecycleOwner, { if (it) { binding.apply { clInstructionView.visibility = View.VISIBLE } } })
            isInstructionHidden.observe(viewLifecycleOwner, { if (it) { binding.apply { clInstructionView.visibility = View.GONE } } })

            isChatViewShowing.observe(viewLifecycleOwner, { if (it) { binding.apply { clChatView.visibility = View.VISIBLE } } })
            isChatViewHidden.observe(viewLifecycleOwner, {
                if (it) {
                    binding.apply { clChatView.visibility = View.GONE }
                    updateMessageCounter(0)
                }
            })

            chatButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    SignalingServer.get()?.sendMessageSeen()
                }
            })

            isYesButtonClicked.observe(viewLifecycleOwner, {
                if (it) {
                    binding.apply { btnYesReady.changeColor(R.color.btn_green) }
                    SignalingServer.get()?.sendApplicantStatus(1)
                }else {
                    binding.apply { btnYesReady.changeColor(R.color.btn_ash) }
                }
            })

            isNoButtonClicked.observe(viewLifecycleOwner, {
                if (it) {
                    binding.apply { btnNoReady.changeColor(R.color.btn_green) }
                    SignalingServer.get()?.sendApplicantStatus(2)
                } else {
                    binding.apply { btnNoReady.changeColor(R.color.btn_ash) }
                }
            })
            /* Ready View End */

            /* CountDown View Start */
            isCountDownVisible.observe(viewLifecycleOwner, { if (it) { startAudio() } })

            countDownFinish.observe(viewLifecycleOwner, {
                if (it) {
                    try {
                        mediaPlayer?.stop()
                    } catch (e: Exception) {
                        Timber.tag("live").d("Error:countDownFinish - %s", e.toString())
                    }
                }
            })
            /* CountDown View End */

            /* Multipeer View Start */
            isInterviewStarted.observe(viewLifecycleOwner, {
                if (it) {
                    localVideoTrack?.removeSink(binding.previewSurfaceView)
                    localVideoTrack?.addSink(binding.localJobseekerSurfaceView)
                }
            })

            isOngoingInterviewVisible.observe(viewLifecycleOwner, { if (it) { binding.apply { clOngoingInterviewView.visibility = View.VISIBLE } } })
            isOngoingInterviewHidden.observe(viewLifecycleOwner, { if (it) { binding.apply { clOngoingInterviewView.visibility = View.GONE } } })

            isOnGoingChatViewShowing.observe(viewLifecycleOwner, { if (it) { binding.apply { clChatView.visibility = View.VISIBLE } } })
            isOnGoingChatViewHidden.observe(viewLifecycleOwner, {
                if (it) {
                    binding.apply { clChatView.visibility = View.GONE }
                    updateMessageCounter(0)
                }
            })

            onGoingChatButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    SignalingServer.get()?.sendMessageSeen()
                }
            })
            isVideoOn.observe(viewLifecycleOwner, {
                if (it) {
                    if (localMediaStream != null && localMediaStream?.videoTracks?.size!! > 0) {
                        localMediaStream?.videoTracks!![0].setEnabled(true)
                        binding.apply { localVideoControl.setImageResource(R.drawable.ic_video_on)}
                    }
                }
            })

            isVideoOff.observe(viewLifecycleOwner, {
                if (it) {
                    if (localMediaStream != null && localMediaStream?.videoTracks?.size!! > 0) {
                        localMediaStream?.videoTracks!![0].setEnabled(false)
                        binding.apply { localVideoControl.setImageResource(R.drawable.ic_video_off)}
                    }
                }
            })

            isAudioOn.observe(viewLifecycleOwner, {
                if (it) {
                    if (localMediaStream != null && localMediaStream?.audioTracks?.size!! > 0) {
                        localMediaStream?.audioTracks!![0].setEnabled(true)
                        binding.apply { localAudioControl.setImageResource(R.drawable.ic_mic_on)}
                    }
                }
            })

            isAudioOff.observe(viewLifecycleOwner, {
                if (it) {
                    if (localMediaStream != null && localMediaStream?.audioTracks?.size!! > 0) {
                        localMediaStream?.audioTracks!![0].setEnabled(false)
                        binding.apply { localAudioControl.setImageResource(R.drawable.ic_microphone_off)}
                    }
                }
            })
            /* Multipeer View end */

            /* Chat View start */
            sendMessageClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    binding.apply {
                        if (binding.etWriteMessage.text.isNotEmpty()) {
                            Timber.tag("live").d("sendMessageClickEvent")
                            postChatMessage(binding.etWriteMessage.text.toString())
                        }
                    }
                }
            })

            chatLogFetchSuccess.observe(viewLifecycleOwner, {
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

            postSuccess.observe(viewLifecycleOwner, {
                if (it) {
                    SignalingServer.get()?.sendChatMessage(postMessage.value.toString(), imageLocal, imageRemote, messageCount)

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

            onChatReceived.observe(viewLifecycleOwner, {
                try{
                    val s = it?.get(0).toString()
                    Timber.tag("live").d("Viewmodel post receivedChatData Chat data")
                    val data = JSONObject(s)

                    //extract data from fired event
                    val nickname = args.companyName
                    val message = data.getString("msg")
                    imageLocal = data.getString("imgLocal")
                    imageRemote = data.getString("imgRemote")
                    messageCount = data.getInt("newCount")

                    updateMessageCounter(messageCount)

                    val simpleDateFormat = SimpleDateFormat("h:mm a")
                    val formattedTime = simpleDateFormat.format(Date())
                    // make instance of message
                    val itemType = if (nickname == bdjobsUserSession.userName!!) 0 else 1
                    val messages = Messages(nickname, message, formattedTime, itemType)

                    messageList.add(messages)

                    // notify the adapter to update the recycler view
                    mAdapter.differ.submitList(messageList)
                    mAdapter.notifyDataSetChanged()
                }
                catch (e:Exception){
                    Timber.tag("live").d("Error:onChatReceived - %s", e.toString())
                }
            })
            /* Chat View End */

            /* Feedback View start */
            isFeedbackViewShowing.observe(viewLifecycleOwner, { if (it) { binding.apply { clFeedbackView.visibility = View.VISIBLE } } })
            isFeedbackViewHidden.observe(viewLifecycleOwner, { if (it) { binding.apply { clFeedbackView.visibility = View.GONE } } })

            isMessageToEmployerShowing.observe(viewLifecycleOwner, { if (it) { binding.apply { clChatView.visibility = View.VISIBLE } } })
            isMessageToEmployerHidden.observe(viewLifecycleOwner, { if (it) { binding.apply { clChatView.visibility = View.GONE } } })

            isFeedbackSubmitted.observe(viewLifecycleOwner, { if (it) {
                findNavController().navigateUp()
         //       findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToLiveInterviewDetailsFragment(jobId,jobTitle))

            } })
            /* Feedback View end */
            canGoToDetailView.observe(viewLifecycleOwner, { if (it) { findNavController().navigateUp() } })
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
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
        val defaultVideoDecoderFactory = DefaultVideoDecoderFactory(eglBaseContext)

        val  audioDeviceModule =  JavaAudioDeviceModule.builder ( requireContext() )
            .setUseHardwareAcousticEchoCanceler(false)
            .setUseHardwareNoiseSuppressor(false)
            .createAudioDeviceModule ()


        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .setAudioDeviceModule(audioDeviceModule)
            .createPeerConnectionFactory()

        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true);

        localAudioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory?.createAudioTrack("101", localAudioSource)
        localAudioTrack?.setEnabled(true)

        videoCapturerAndroid = createVideoCapturer()
        videoCapturerAndroid?.let {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
            localVideoSource =
                peerConnectionFactory?.createVideoSource(videoCapturerAndroid!!.isScreencast)
            videoCapturerAndroid!!.initialize(
                surfaceTextureHelper,
                requireActivity(),
                localVideoSource?.capturerObserver
            )
        }
        videoCapturerAndroid?.startCapture(640, 480, 30)

        binding.previewSurfaceView.setMirror(true)
        binding.previewSurfaceView.init(eglBaseContext, null)

        binding.localJobseekerSurfaceView.setMirror(true)
        binding.localJobseekerSurfaceView.init(eglBaseContext, null)


        localVideoTrack = peerConnectionFactory?.createVideoTrack("100", localVideoSource)

        localMediaStream = peerConnectionFactory?.createLocalMediaStream("ARDAMS")
        localMediaStream?.apply {
            addTrack(localAudioTrack)
            addTrack(localVideoTrack)
        }

//        localVideoTrack?.addSink(binding.localJobseekerSurfaceView)
        localVideoTrack?.addSink(binding.previewSurfaceView)

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
                }else{
                    crashReport.setCustomKey("LI-createCameraCapturer","isFrontFacing-videoCapturer-null")
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }else{
                    crashReport.setCustomKey("LI-createCameraCapturer","isBack-videoCapturer-null")
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
        remoteMediaStream = stream
        remoteVideoTrack = stream.videoTracks[0]
        remoteAudioTrack = stream.audioTracks[0]

        runOnUiThread {
            try {
                remoteVideoTrack?.addSink(binding.remoteHostSurfaceView)
            } catch (e: Exception) {
                Timber.tag("live").d("Error:gotRemoteStream $e")
                crashReport.setCustomKey("LI-gotRemoteStream", e.toString())
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
        rtcConfig.enableCpuOveruseDetection = true


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
            crashReport.setCustomKey("LI-onDestroy",e.toString())
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
    override fun onInactiveUser(args: Array<Any?>?) {
        Timber.tag("live").d("onInactiveUser: %s", args?.get(0))
        runOnUiThread {
            try {
                interviewSessionViewModel.isEmployerArrived.postValue(false)
            } catch (e: Exception) {
                Timber.e("Error: onInactiveUser-isEmployerArrived make false: ${e}")
                crashReport.setCustomKey("LI-onInactiveUser-isEmployerArrived make false", e.toString())
            }
        }

        //    findNavController().navigateUp()
    }


    override fun onEventConnectionError(args: Array<Any>) {
        Timber.tag("live").d("ERROR: %s", args[0])
        FirebaseCrashlytics.getInstance().setUserId(userName)
        crashReport.setCustomKey("LI-onEventConnectionError", args[0].toString())
        findNavController().navigateUp()
      //  findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToLiveInterviewDetailsFragment(jobId,jobTitle))

    }

    override fun setLocalSocketID(id: String) {
        Timber.tag("live").d("setLocalSocketID: %s", id)
        mSocketId = id
    }

    override fun onParticipantCount(args: Array<Any?>?) {
        Timber.tag("live").d("onParticipantCount: %s", args?.get(0))

        runOnUiThread {
            try{
                val argument = args?.get(0) as JSONObject
                val totalUserInTheRoom = argument.getInt("participant")
                val totalUserInTheRoomString = argument.getString("participant")

                Timber.tag("live").d("totalUserInTheRoom: $totalUserInTheRoom")
                Timber.tag("live").d("totalUserInTheRoom: $totalUserInTheRoomString")


                if(totalUserInTheRoom>2){
                    Timber.tag("live").d("Show AlertDialog")

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Live Interview Room")
                    builder.setMessage("The interview room is closed as an interview is currently going on.")
                    builder.setPositiveButton("Okay") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    builder.show()
                }
            }catch (e:Exception){
                Timber.e("Error: onParticipantCount: ${e}")
                crashReport.setCustomKey("LI-onParticipantCount", e.toString())
            }
        }

    }

    override fun onNewUser(args: Array<Any?>?) {
        Timber.tag("live").d("onNewUser: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("socketId")
        getOrCreatePeerConnection(mRemoteSocketId, "R")
        runOnUiThread {
            try {
                interviewSessionViewModel.isEmployerArrived.postValue(true)
            } catch (e: Exception) {
                Timber.e("Error: onNewUser-isEmployerArrived: ${e}")
                crashReport.setCustomKey("LI-onNewUser-isEmployerArrived", e.toString())
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
                interviewSessionViewModel.isEmployerArrived.postValue(true)
            } catch (e: Exception) {
                Timber.e("Error: on1stUserCheck-isEmployerArrived: ${e}")
                crashReport.setCustomKey("LI-on1stUserCheck-isEmployerArrived", e.toString())
            }
        }

    }

    override fun onNewUserStartNew(args: Array<Any?>?) {
        Timber.tag("live").d("onNewUserStartNew: %s", args?.get(0))
        val argument = args?.get(0) as JSONObject
        mRemoteSocketId = argument.getString("sender")
        val isloginUser = argument.getString("isloginuser")
         runOnUiThread {
                try {
                    if (isloginUser.equals("true")){
                        interviewSessionViewModel.applicantJoinedOnGoingSession()
                    }else{
                        interviewSessionViewModel.isEmployerArrived.postValue(true)
                    }
                } catch (e: Exception) {
                    Timber.tag("live").d("Error:onNewUserStartNew applicantJoinedOnGoingSession - $e")
                    crashReport.setCustomKey("LI-onNewUserStartNew-applicantJoinedOnGoingSession", e.toString())

                }
            }
        getOrCreatePeerConnection(mRemoteSocketId, "R")
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
                interviewSessionViewModel.employerEndedCall()

            } catch (e: Exception) {
                Timber.tag("live").d("Error onEndemployerEndedCall: $e")
            }
        }
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
                interviewSessionViewModel.showCountDown()
            } catch (e: Exception) {
                Timber.tag("live").d("Error:showCountDown - $e")
            }
        }
    }


    override fun onReceiveChat(args: Array<Any?>?) {
        Timber.tag("live").d("onReceiveChat: %s", args?.get(0))
        runOnUiThread {
            try {
                interviewSessionViewModel.receivedChatData(args)
            } catch (e: Exception) {
                Timber.tag("live").d("Error:receivedChatData - $e")
                crashReport.setCustomKey("LI-onReceiveChat", e.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("live").d("onDestroyView")
        SignalingServer.get()?.destroy()
        try {
            if (localMediaStream != null) {
                if(localMediaStream?.videoTracks?.size!! > 0) localMediaStream?.videoTracks!![0].setEnabled(false)
                if(localMediaStream?.audioTracks?.size!! > 0) localMediaStream?.audioTracks!![0].setEnabled(false)
            }
            localVideoTrack?.removeSink(binding.localJobseekerSurfaceView)
            remoteVideoTrack?.removeSink(binding.remoteHostSurfaceView)
            videoCapturerAndroid?.stopCapture()
            localVideoTrack?.dispose()
            localAudioTrack?.dispose()
            remoteVideoTrack?.dispose()
            remoteAudioTrack?.dispose()
            localMediaStream?.dispose()
            remoteMediaStream?.dispose()
            binding.remoteHostSurfaceView.release()
            binding.localJobseekerSurfaceView.release()
        } catch (e: Exception) {
            Timber.tag("live").d(e.toString())
        }
    }
}