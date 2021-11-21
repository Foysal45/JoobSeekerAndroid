package com.bdjobs.app.transaction.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentTransactionListBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import java.text.SimpleDateFormat
import java.util.*


class TransactionListFragment : Fragment() {


    private val transactionListViewModel: TransactionListViewModel by navGraphViewModels(R.id.transactionListFragment) {
        ViewModelFactoryUtil.provideTransactionListViewModelFactory(this)
    }
    lateinit var binding: FragmentTransactionListBinding
    var startDate = ""
    var endDate = ""
    var type = ""
    val args: TransactionListFragmentArgs by navArgs()
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

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

    @RequiresApi(Build.VERSION_CODES.O)
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
                        args.transactionType!!.isEmpty() -> {
                            type = "0"
                        }
                    }

                    if (args.startDate!!.isNotEmpty()) {
                        val date1 = Date.parse(args.startDate.toString())
                        startDate = formatter.format(date1)


                    }
                    if (args.endDate!!.isNotEmpty()) {
                        val date2 = Date.parse(args.endDate.toString())
                        endDate = formatter.format(date2)
                    }

                    transactionListViewModel.getTransactionList(startDate, endDate, type)
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


            try {
                if (arguments != null) {
                    if (args.from == "default") {
                        val action = TransactionListFragmentDirections.actionTransactionListFragmentToTransactionFilterFragment()
                        action.from = ""
                        action.startDate = ""
                        action.endDate = ""
                        action.transactionType = ""
                        findNavController().navigate(action)
                    } else {
                        val action = TransactionListFragmentDirections.actionTransactionListFragmentToTransactionFilterFragment()
                        action.from = "list"
                        action.startDate = args.startDate
                        action.endDate = args.endDate
                        action.transactionType = args.transactionType
                        findNavController().navigate(action)
                    }


                }
            } catch (e: Exception) {

            }


        }


    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        textView10.text = "You don't have any transactions yet."
    }


}