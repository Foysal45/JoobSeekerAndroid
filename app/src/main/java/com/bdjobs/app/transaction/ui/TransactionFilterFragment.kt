package com.bdjobs.app.transaction.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.databinding.TransactionFilterFragmentBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.transaction_filter_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionFilterFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFilterFragment()
    }

    private val transactionFilterModel: TransactionFilterViewModel by navGraphViewModels(R.id.transactionFilterFragment) {
        ViewModelFactoryUtil.provideTransactionFilterViewModelFactory(this)
    }
    lateinit var binding: TransactionFilterFragmentBinding
    private val typeArray = arrayOf("Employability Assessment", "SMS Job Alert")
    private lateinit var now: Calendar
    private lateinit var viewModel: TransactionFilterViewModel
    private var date: Date? = null
    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    val cal = Calendar.getInstance()
    private val endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    var startDate = ""
    var endDate = ""
    val viewFormat = "dd MMMM yyyy"
    val args: TransactionFilterFragmentArgs by navArgs()
    val viewSdf = SimpleDateFormat(viewFormat, Locale.US)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = TransactionFilterFragmentBinding.inflate(inflater).apply {
            transactionFilterViewModel = transactionFilterModel
            lifecycleOwner = viewLifecycleOwner
        }
        binding.lifecycleOwner = this
        now = Calendar.getInstance()
        try {
            if (arguments != null) {
                if (args.transactionType!!.isNotEmpty()) {
                    binding.packageTypeTIET.setText(args.transactionType.toString())
                }
                if (args.startDate!!.isNotEmpty()) {
                    val date = Date.parse(args.startDate.toString())
                    startDate = formatter.format(date)
                    binding.startDateTIET.setText(viewSdf.format(date))
                }
                if (args.endDate!!.isNotEmpty()) {
                    val date = Date.parse(args.endDate.toString())
                    endDate = formatter.format(date)
                    binding.endDateTIET.setText(viewSdf.format(date))

                }

            }

        } catch (e: Exception) {

            Timber.e("Exception onTransaction: ${e.localizedMessage}")
        }

        binding.packageTypeTIET.onClick {
            requireContext().selector("Select Transaction type", typeArray.toList()) { _, i ->
                packageTypeTIET.setText(typeArray[i])
                til_package_type.requestFocus()

            }
        }
        binding.startDateTIET.setOnClickListener {
            if (startDateTIET.text.toString().isEmpty())
                pickDate(requireContext(), cal, startDateSetListener)
            else {
                date = formatter.parse(startDate)
                cal.time = date
                pickDate(requireContext(), cal, startDateSetListener)
            }


        }
        binding.endDateTIET.setOnClickListener {
            if (endDateTIET.text.toString().isNotEmpty()) {
                date = formatter.parse(endDate)
                cal.time = date
                pickDate(requireContext(), cal, endDateSetListener)
            } else {
                pickDate(requireContext(), cal, endDateSetListener)
            }


        }
        binding.transactionFilterFab.onClick {

            var startDate = ""
            var endDate = ""
            var type = ""

            startDate = if (startDateTIET.text!!.isNotEmpty() && transactionFilterModel.startDateNTime.isEmpty()) {
                args.startDate.toString()
            } else
                transactionFilterModel.startDateNTime

            endDate = if (endDateTIET.text!!.isNotEmpty() && transactionFilterModel.endDateNTime.isEmpty()) {
                args.endDate.toString()
            } else
                transactionFilterModel.endDateNTime


            type = if (packageTypeTIET.text.toString() != "Select" && transactionFilterModel.transactionType.isEmpty()) {

                args.transactionType.toString()

            } else
                transactionFilterModel.transactionType

            if (dateValidationCheck()) {
                val action = TransactionFilterFragmentDirections.actionTransactionFilterFragmentToTransactionListFragment()
                action.from = "filter"
                action.startDate = startDate
                action.endDate = endDate
                action.transactionType = type
                findNavController().navigate(action)
            }

        }

        return binding.root
    }


    private fun updateDateInView(c: Int) {

        if (c == 0) {
            startDateTIET.setText(viewSdf.format(now.time))
            transactionFilterModel.startDateNTime = now.time.toString()
            startDate = formatter.format(now.time)

        } else {

            transactionFilterModel.endDateNTime = formatter.format(now.time)
            endDateTIET.setText(viewSdf.format(now.time))
            endDate = formatter.format(now.time)

        }
    }


    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }

    private fun dateValidationCheck(): Boolean {

        try {
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val date1 = formatter.parse(startDate)
                val date2 = formatter.parse(endDate)
                if (date1!!.after(date2)) {

                    requireContext().toast("Start Date cannot be greater than End Date!")
                } else {
                    return if (date1 == date2) {

                        requireContext().toast("Start Date and End Date cannot be equal!")

                        false
                    } else {
                        true
                    }


                }
            } else
                return true


        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false

    }
}