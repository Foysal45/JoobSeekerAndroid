package com.bdjobs.app.sms.ui.payment

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.bdjobs.app.sms.data.model.PaymentInfoBeforeGateway
import com.bdjobs.app.sms.data.repository.SMSRepository
import com.sslwireless.sslcommerzlibrary.model.initializer.CustomerInfoInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.CurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener
import kotlinx.coroutines.launch
import timber.log.Timber

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

    lateinit var paymentInfoData : PaymentInfoBeforeGateway.PaymentInfoBeforeGatewayData

    init {
        Timber.d("called payment viewModel init")
        getPaymentInfoBeforeGateway()
    }

    private val storeIdTest = "bdjob5f0ad29f35834"
    private val storePasswordTest = "bdjob5f0ad29f35834"

    private val storeIdLive = "mybdjob02live"
    private val storePasswordLive = "5B4C5502A877419363"

    private fun getPaymentInfoBeforeGateway() {
        viewModelScope.launch {
            try {
                val response = repository.callPaymentInfoBeforeGatewayApi()
                if (response.statuscode == "0"){
                    paymentInfoData = response.data?.get(0)!!
//                    paymentInfoData?.apply{
//                        serviceId = response.data?.get(0)?.serviceId
//                        smsSubscribedId = response.data?.get(0)?.smsSubscribedId
//                        totalAmount = response.data?.get(0)?.totalAmount
//                        totalQuantity = response.data?.get(0)?.totalQuantity
//                        transactionId = response.data?.get(0)?.transactionId
//                        userEmail = response.data?.get(0)?.userEmail
//                        userFullName = response.data?.get(0)?.userFullName
//                        userMobileNo = response.data?.get(0)?.userMobileNo
//                    }
                }
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun onConfirmPaymentClick() {
        makePayment()
    }

    private fun makePayment() {

        Timber.d("${paymentInfoData.transactionId}")


        val transactionResponseListener = object : TransactionResponseListener{
            override fun transactionFail(p0: String?) {
                //_paymentStatus.value = Status.FAILURE
                Timber.d("payment failure : $p0")

            }

            override fun merchantValidationError(p0: String?) {
            }

            override fun transactionSuccess(p0: TransactionInfoModel?) {
                //_paymentStatus.value = Status.SUCCESS
                Timber.d("payment success : \n" +
                        "tran_id : ${p0?.tranId}\n" +
                        "card_type : ${p0?.cardType} \n" +
                        "store_amount : ${p0?.storeAmount} \n" +
                        "val_id : ${p0?.valId} \n" +
                        "status : ${p0?.status} \n" +
                        "currency_type : ${p0?.currencyType} \n"+
                        "tran_date : ${p0?.tranDate}"
                )
                callAfterPaymentApi(p0)
            }

        }

        val sslCommerzInitialization = SSLCommerzInitialization(
                storeIdTest, storePasswordTest,
                10.0, CurrencyType.BDT, paymentInfoData.transactionId,
                "Payment", SdkType.TESTBOX
        )

        val customerInfoInitializer = CustomerInfoInitializer(
                paymentInfoData.userFullName,
                paymentInfoData.userEmail,
                "",
                "",
                "",
                "",
                paymentInfoData.userMobileNo
        )

        IntegrateSSLCommerz
                .getInstance(fragment.requireContext())
                .addSSLCommerzInitialization(sslCommerzInitialization)
                .addCustomerInfoInitializer(customerInfoInitializer)
                .buildApiCall(transactionResponseListener)
    }

    fun callAfterPaymentApi(data : TransactionInfoModel?){
        viewModelScope.launch {
            try {
                val response = repository.callPaymentAfterReturningGatewayApi(data)
                Timber.d("after payment response $response")
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    enum class Status {
        SUCCESS, FAILURE
    }

}