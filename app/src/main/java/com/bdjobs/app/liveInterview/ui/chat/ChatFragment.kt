package com.bdjobs.app.liveInterview.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentChatBinding

class ChatFragment : Fragment() {


    private lateinit var binding:FragmentChatBinding
    private val chatViewModel:ChatViewModel by navGraphViewModels(R.id.chatFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}