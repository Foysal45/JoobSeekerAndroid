package com.bdjobs.app.transaction.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentTransactionListBinding
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel

import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil

import kotlinx.android.synthetic.main.fragment_transaction_list.*


class TransactionListFragment : Fragment() {


    private val transactionListViewModel: TransactionListViewModel by navGraphViewModels(R.id.transactionListFragment) { ViewModelFactoryUtil.provideTransactionListViewModelFactory(this) }
    lateinit var binding: FragmentTransactionListBinding
    var startDate = ""
    var endDate = ""
    var type = ""
    val args: TransactionListFragmentArgs by navArgs()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionListBinding.inflate(inflater).apply {
            transactionViewModel = transactionListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.lifecycleOwner = this
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            if (arguments != null) {

                if (args.from == "default") {
                    transactionListViewModel.getTransactionList("", "", "0")
                } else {
                    when {
                        args.transactionType.equals("Online Application") -> {
                            type = "3"
                        }
                        args.transactionType.equals("Employability Assessment") -> {
                            type = "1"
                        }
                        args.transactionType.equals("SMS Job Alert") -> {
                            type = "2"
                        }
                        args.transactionType.equals("Select") -> {
                            type = "0"
                        }
                    }

                    transactionListViewModel.getTransactionList(args.startDate!!, args.endDate!!, type)
                }


            }
        } catch (e: Exception) {

        }

        val adapter = TransactionListAdapter(requireContext())


        binding.lifecycleOwner = this


        rv_sms_transaction?.adapter = adapter
        rv_sms_transaction.apply {
            this.adapter = adapter
        }

        transactionListViewModel.apply {
            transactionListData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }


        iv_transaction_filter?.setOnClickListener {
            findNavController().navigate(TransactionListFragmentDirections.actionTransactionListFragmentToTransactionFilterFragment())
        }


    }


}