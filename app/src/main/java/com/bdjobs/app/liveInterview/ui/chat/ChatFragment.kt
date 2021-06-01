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
import kotlin.properties.Delegates

class ChatFragment : Fragment(), SignalingEvent {


    private lateinit var binding: FragmentChatBinding
    private var mAdapter: ChatAdapter = ChatAdapter()
    private lateinit var imageLocal: String
    private lateinit var imageRemote: String
    private var messageCount = 0


    private val args: ChatFragmentArgs by navArgs()

    private val chatViewModel: ChatViewModel by viewModels{
        ChatViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                args.processID
        )
    }
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val messageList:ArrayList<Messages> = ArrayList()

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

            chatLogFetchSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                if (it) {
                    var messages: Messages?

                    val data = chatLogData.value?.arrChatdata
                    if (data!!.isNotEmpty()) {
                        for (i in data.indices) {
                            val d = data[i]
                            if (d?.hostType=="A" || d?.hostType=="R") {
                                val time = d.chatTime?.split(" ")!![1].split(":")[0] +
                                        ":${d.chatTime.split(" ")[1].split(":")[1]} ${d.chatTime.split(" ")[2]}"
                                messages = Messages(d.contactName,d.chatText, time,if (d.hostType =="A") 0 else 1)
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
                    SignalingServer.get()?.sendChatMessage(postMessage.value.toString(), imageLocal, imageRemote, messageCount)
                    binding.etWriteMessage.setText("")
                }
            })

        }
    }

    private fun sendMessage() {
        if (binding.etWriteMessage.text.isNotEmpty()) {
            chatViewModel.postChatMessage(binding.etWriteMessage.text.toString())
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


    @SuppressLint("SimpleDateFormat")
    override fun onReceiveChat(args: Array<Any?>?) {

        runOnUiThread {
            val data = args?.get(0) as JSONObject
            try {
                //extract data from fired event
                val nickname = "Employer"
                val message = data.getString("msg")
                imageLocal = data.getString("imgLocal")
                imageRemote = data.getString("imgRemote")
                messageCount = data.getInt("newCount")

                val simpleDateFormat = SimpleDateFormat("h:mm a")
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


    override fun onEventDisconnected() {
    }

    override fun onEventConnectionError(args: Array<Any>) {
    }

    override fun setLocalSocketID(id: String) {
        Timber.d("TAG: setLocalSocketID: %s", id)
    }

    override fun on1stUserCheck(args: Array<Any?>?) {
        Timber.d("TAG: on1stUserCheck: %s", args?.get(0))
    }

    override fun onNewUserStartNew(args: Array<Any?>?) {
        Timber.d("TAG: onNewUserStartNew: %s", args?.get(0))
    }

    override fun onReceiveIceCandidate(args: Array<Any?>?) {
        Timber.d("TAG: onReceiveIceCandidate: %s", args?.get(0))
    }

    override fun onReceiveCall(args: Array<Any?>?) {
        Timber.d("TAG: onReceiveCall: %s", args?.get(0))
    }

    override fun onReceiveSDP(args: Array<Any?>?) {
        Timber.d("TAG: onReceiveSDP: %s", args?.get(0))
    }

}