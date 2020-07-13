package com.bdjobs.app.sms.ui.payment

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.sslwireless.sslcommerzlibrary.model.initializer.CustomerInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.CurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener

class PaymentViewModel(val repository: SMSRepository,
                       private val totalSMS: Int?,
                       private val totalTaka: Int?,
                       val fragment: Fragment) : ViewModel(){

    private val _quantity = MutableLiveData<Int>().apply {
        value = 1
    }
    val quantityString = Transformations.map(_quantity){quantity->
        String.format("%02d", quantity)
    }

    private val _totalNumberOfSMS = MutableLiveData<Int>().apply {
        value = totalSMS
    }
    val totalNumberOfSMS = _totalNumberOfSMS


    private val _totalAmountIntTaka = MutableLiveData<Int>().apply {
        value = totalTaka?.times(_quantity.value!!)
    }
    val totalAmountIntTaka = _totalAmountIntTaka


    private val _paymentStatus = MutableLiveData<Status>()
    val paymentStatus = _paymentStatus


    fun onConfirmPaymentClick() {
        makePayment()
    }

    private fun makePayment() {

        val transactionResponseListener = object : TransactionResponseListener{
            override fun transactionFail(p0: String?) {
                _paymentStatus.value = Status.FAILURE
            }

            override fun merchantValidationError(p0: String?) {
            }

            override fun transactionSuccess(p0: TransactionInfoModel?) {
                _paymentStatus.value = Status.SUCCESS
            }

        }

        val sslCommerzInitialization = SSLCommerzInitialization(
                "bdjob5f0ad29f35834", "bdjob5f0ad29f35834@ssl",
                totalAmountIntTaka.value!!.toDouble(), CurrencyType.BDT, "transactionID" + "123456789",
                "Payment", SdkType.TESTBOX
        )

        val customerInfoInitializer = CustomerInfoInitializer(
                "Rakibul Huda",
                "rakib10rr3@gmail.com",
                "Dhaka",
                "Dhaka",
                "1200",
                "Bangladesh",
                "123456789"
        )

        IntegrateSSLCommerz
                .getInstance(fragment.requireContext())
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .addCustomerInfoInitializer(customerInfoInitializer)
                .buildApiCall(transactionResponseListener)
    }

    enum class Status {
        SUCCESS, FAILURE
    }

}