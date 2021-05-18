package com.bdjobs.app.liveInterview.ui.chat

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.FragmentChatBinding
import com.bdjobs.app.liveInterview.data.models.Messages
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.liveInterview.data.socketClient.SignalingEvent
import com.bdjobs.app.liveInterview.data.socketClient.SignalingServer
import com.bdjobs.app.liveInterview.ui.interview_details.LiveInterviewDetailsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment(), SignalingEvent {


    private lateinit var binding: FragmentChatBinding
    private var mAdapter: ChatAdapter = ChatAdapter()

    private val args: ChatFragmentArgs by navArgs()

    private val chatViewModel: ChatViewModel by viewModels{
        ChatViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                args.processID
        )
    }
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val messageList:ArrayList<Messages> = ArrayList()

    private val testNickname = "Soumik"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = chatViewModel
            adapter = mAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(requireContext())

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = "Chat"

        initSocketClient()

        Timber.d("Process ID: ${args.processID}")

        binding.ivSendMessage.setOnClickListener {
            chatViewModel.sendButtonClickedEvent()
        }

        setUpObservers()

    }

    private fun setUpObservers() {
        chatViewModel.apply {
            sendButtonClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    sendMessage()
                }
            })


        }
    }

    private fun sendMessage() {
        if (binding.etWriteMessage.text.isNotEmpty()) {
            SignalingServer.get()?.messageDetection(bdjobsUserSession.userName!!,binding.etWriteMessage.text.toString())
            binding.etWriteMessage.setText("")
        }
    }

    private fun initSocketClient() {
        SignalingServer.get()?.init(this)

        try {
            SignalingServer.get()?.joinToChatRoom(bdjobsUserSession.userName!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SignalingServer.get()?.destroy()
    }


    override fun onUserJoined(args: Array<Any>) {
        if (isAdded) {
            runOnUiThread {
                val data = args[0] as String
                Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onUserDisconnected(args: Array<Any>) {
        if (isAdded) {
            runOnUiThread {
                val data = args[0] as String
                Toast.makeText(requireContext(), data, Toast.LENGTH_SHORT).show()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onMessageReceived(args: Array<Any>) {

        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                //extract data from fired event
                val nickname = data.getString("senderNickname")
                val message = data.getString("message")

                val simpleDateFormat = SimpleDateFormat("HH:mm a")
                val formattedTime = simpleDateFormat.format(Date())
                // make instance of message
                val itemType = if (nickname==bdjobsUserSession.userName!!) 0 else 1
                val messages = Messages(nickname, message,formattedTime,itemType)

                messageList.add(messages)

                // notify the adapter to update the recycler view
                mAdapter.differ.submitList(messageList)
                mAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onEventConnected() {
    }

    override fun onEventIPADDR() {
    }

    override fun onEventCreated(args: Array<Any?>?) {
    }

    override fun onEventFull() {
    }

    override fun onEventJoin(args: Array<Any>) {
    }

    override fun onEventJoined(args: Array<Any>) {
    }

    override fun onEventLog(args: Array<Any>) {
    }

    override fun onEventMessage(args: Array<Any>) {
    }

    override fun onEventMessage2(args: Array<Any?>?) {
    }

    override fun onEventDisconnected() {
    }

    override fun onEventConnectionError(args: Array<Any>) {
    }

}