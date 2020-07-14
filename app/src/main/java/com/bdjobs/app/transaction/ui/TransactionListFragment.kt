package com.bdjobs.app.transaction.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentTransactionListBinding
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.ui.interview_list.ClickListener
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListAdapter
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListViewModel
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.fragment_video_interview_list.*
import kotlinx.android.synthetic.main.fragment_video_interview_list.tool_bar
import java.util.Observer

class TransactionListFragment : Fragment() {

    private val transactionListViewModel: TransactionListViewModel by navGraphViewModels(R.id.transactionListFragment){ ViewModelFactoryUtil.provideTransactionListViewModelFactory(this) }
    lateinit var binding: FragmentTransactionListBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionListBinding.inflate(inflater).apply {
            viewModel = transactionListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val time : String = requireArguments().getString("activityDate","0")

        //Log.d("rakib activity date " ,"${arguments?.get("activityDate")}")

        val navController = findNavController()
      /*  val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
     */   //setSupportActionBar(tool_bar)
     /*   tool_bar?.setupWithNavController(navController, appBarConfiguration)*/

        transactionListViewModel.getVideoInterviewList(time)

        val adapter = TransactionListAdapter(requireContext())
        rv_sms_transaction?.adapter = adapter

        rv_sms_transaction.apply {
            this.adapter = adapter
        }

       /* transactionListViewModel.apply {
            videoInterviewListData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }*/

    }


}