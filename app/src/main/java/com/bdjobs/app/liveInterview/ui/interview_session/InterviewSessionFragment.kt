package com.bdjobs.app.liveInterview.ui.interview_session

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.changeColor
import com.bdjobs.app.databinding.FragmentInterviewSessionBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_interview_invitation_details.*
import timber.log.Timber


class InterviewSessionFragment : Fragment(),ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding:FragmentInterviewSessionBinding
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private val interviewSessionViewModel : InterviewSessionViewModel by navGraphViewModels(R.id.interviewSessionFragment)

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
        binding.toolBar.title = "Senior Software Analyst"

        setUpObservers()

        initializeCamera()
    }

    private fun setUpObservers() {
        interviewSessionViewModel.apply {
            messageButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToChatFragment())
            })

            instructionButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) findNavController().navigate(InterviewSessionFragmentDirections.actionInterviewSessionFragmentToInstructionLandingFragment())
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
        }
    }


    private fun initializeCamera() {
        binding.cameraLocalReady.setLifecycleOwner(viewLifecycleOwner)

        try {
            binding.cameraLocalReady.facing = Facing.FRONT
        } catch (e: Exception) {
            binding.cameraLocalReady.facing = Facing.BACK
        } finally {

        }

        binding.cameraLocalReady.addCameraListener(object : CameraListener() {
            override fun onVideoRecordingStart() {
                Timber.d("Video Recording Start")
                super.onVideoRecordingStart()
            }

            override fun onVideoRecordingEnd() {
                Timber.d("Video Recording End")
                super.onVideoRecordingEnd()
            }

            override fun onCameraOpened(options: CameraOptions) {
                Timber.d("Camera Opened")
                super.onCameraOpened(options)
            }

            override fun onCameraClosed() {
                Timber.d("Camera Closed")
                super.onCameraClosed()
            }

            override fun onVideoTaken(result: VideoResult) {
                Timber.d("Video Taken")
                super.onVideoTaken(result)
            }

        })
    }


    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        interviewSessionViewModel.networkCheck(isConnected)
    }
}