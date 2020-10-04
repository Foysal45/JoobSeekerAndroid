package com.bdjobs.app.transaction.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.databinding.TransactionFilterFragmentBinding
import kotlinx.android.synthetic.main.transaction_filter_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
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

    var startDate = ""
    var endDate = ""



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = TransactionFilterFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransactionFilterViewModel::class.java)
        // TODO: Use the ViewModel
        now = Calendar.getInstance()


        onClick()


    }

    private fun onClick() {
        packageTypeTIET.onClick {

            requireContext().selector("Select Transaction type", typeArray.toList()) { _, i ->
                packageTypeTIET.setText(typeArray[i])

                til_package_type.requestFocus()


            }



        }
        startDateTIET?.setOnClickListener {


            if (startDateTIET.text.toString().isEmpty())
                pickDate(requireContext(), cal, startDateSetListener)
            else {
                date = formatter.parse(startDate)
                cal.time = date
                pickDate(requireContext(), cal, startDateSetListener)
            }

        }
        endDateTIET?.setOnClickListener {


            if (endDateTIET.text.toString().isNotEmpty()) {
                date = formatter.parse(endDate)
                cal.time = date
                pickDate(requireContext(), cal, endDateSetListener)
            } else {
                pickDate(requireContext(), cal, endDateSetListener)
            }


        }
        transactionFilterFab?.onClick {

              if (dateValidationCheck()){
                  val action = TransactionFilterFragmentDirections.actionTransactionFilterFragmentToTransactionListFragment()
                  action.from = "filter"
                  action.startDate = startDate
                  action.endDate = endDate
                  action.transactionType = packageTypeTIET.text.toString()

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
            startDateTIET.setText(viewSdf.format(now.time))
            startDate = apiSdf.format(now.time)

        } else {
            endDateTIET.setText(viewSdf.format(now.time))
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