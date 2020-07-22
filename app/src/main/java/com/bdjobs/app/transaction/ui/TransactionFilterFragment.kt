package com.bdjobs.app.transaction.ui

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_professional_ql_edit.*
import kotlinx.android.synthetic.main.transaction_filter_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.nio.channels.Selector
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransactionFilterFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFilterFragment()
    }

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
    private lateinit var itemSelector: Selector
    var startDate = ""
    var endDate = ""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.transaction_filter_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransactionFilterViewModel::class.java)
        // TODO: Use the ViewModel
        now = Calendar.getInstance()


        onClick()


    }

    private fun onClick() {
        et_package_type.onClick {

            requireContext().selector("Select Transaction type", typeArray.toList()) { _, i ->
                et_package_type.setText(typeArray[i])

                til_package_type.requestFocus()


            }

        }
        et_ts_start_date?.setOnClickListener {


            if (et_ts_start_date.text.toString().isEmpty())
                pickDate(requireContext(), cal, startDateSetListener)
            else {
                date = formatter.parse(startDate)
                cal.time = date
                pickDate(requireContext(), cal, startDateSetListener)
            }

        }
        et_ts_end_date?.setOnClickListener {


            if (et_ts_end_date.text.toString().isNotEmpty()) {
                date = formatter.parse(endDate)
                cal.time = date
                pickDate(requireContext(), cal, endDateSetListener)
            } else {
                pickDate(requireContext(), cal, endDateSetListener)
            }


        }
        fab_transaction_filter?.setOnClickListener {

              if (dateValidationCheck()){
                  val action = TransactionFilterFragmentDirections.actionTransactionFilterFragmentToTransactionListFragment()
                  action.from = "filter"
                  action.startDate = startDate
                  action.endDate = endDate
                  action.transactionType = et_package_type.text.toString()

                  findNavController().navigate(action)
              }

        }


    }

    private fun updateDateInView(c: Int) {
        val apiFormat = "MM/dd/yyyy" // mention the format you need
        val viewFormat = "dd MMMM yyyy"

        val apiSdf = SimpleDateFormat(apiFormat, Locale.US)
        val viewSdf = SimpleDateFormat(viewFormat, Locale.US)
        if (c == 0) {
            et_ts_start_date.setText(viewSdf.format(now.time))
            startDate = apiSdf.format(now.time)

        } else {
            et_ts_end_date.setText(viewSdf.format(now.time))
            endDate = apiSdf.format(now.time)

        }
    }


    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }

    private fun dateValidationCheck(): Boolean {
        val sdf1 = SimpleDateFormat("MM/dd/yyyy")
        try {


            if (startDate.isNotEmpty() && endDate.isNotEmpty() ){
                  val date1 = sdf1.parse(startDate)
                val date2 = sdf1.parse(endDate)


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