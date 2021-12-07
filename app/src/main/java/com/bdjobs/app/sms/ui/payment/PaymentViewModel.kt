package com.bdjobs.app.sms.ui.payment

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.bdjobs.app.utilities.Constants
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
                       private val isFree: String?,
                       val fragment: Fragment,
                       private val bonusSMS: Int?) : ViewModel() {

    private val _quantity = MutableLiveData<Int>().apply {
        value = 1
    }
    val quantityString = Transformations.map(_quantity) { quantity ->
        String.format("%02d", quantity)
    }

    private val _totalNumberOfSMS = MutableLiveData<Int>().apply {
        value = totalSMS
    }
    val totalNumberOfSMS = _totalNumberOfSMS

    private val _bonusNumberOfSMS = MutableLiveData<Int>().apply {
        value = bonusSMS
    }

    val bonusNumberOfSMS = _bonusNumberOfSMS

    private val _showBonusSMSView = MutableLiveData<Boolean>().apply {
        value = bonusSMS!=0
    }
    val showBonusSMSView : LiveData<Boolean> = _showBonusSMSView

    private val _totalAmountIntTaka = MutableLiveData<Int>().apply {
        value = totalTaka?.times(_quantity.value!!)
    }
    val totalAmountIntTaka = _totalAmountIntTaka

    private val _paymentStatus = MutableLiveData<Status>()
    val paymentStatus = _paymentStatus

    lateinit var paymentInfoData: PaymentInfoBeforeGateway.PaymentInfoBeforeGatewayData


    private val storeIdTest = "bdjob5f0ad29f35834"
    private val storePasswordTest = "bdjob5f0ad29f35834@ssl"

    private val storeIdLive = "mybdjob02live"
    private val storePasswordLive = "5B4C5502A877419363"


    private fun getPaymentInfoBeforeGateway() {
        viewModelScope.launch {
            try {
                val response = repository.callPaymentInfoBeforeGatewayApi(totalSMS, totalAmountIntTaka.value, isFree)
                if (response.statuscode == "0") {
                    paymentInfoData = response.data?.get(0)!!
                    Constants.isSMSFree = paymentInfoData.isSMSFree.toString()
                    if (totalAmountIntTaka.value!! == 0) {
                        _paymentStatus.value = Status.SUCCESS
                    } else {
                        makePaymentToSSL()
                    }
                } else {
                    _paymentStatus.value = Status.FAILURE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onConfirmPaymentClick() {
        getPaymentInfoBeforeGateway()
    }

    private fun makePaymentToSSL() {

        Timber.d("${paymentInfoData.transactionId}")


        val transactionResponseListener = object : TransactionResponseListener {

            override fun transactionFail(p0: String?) {
                _paymentStatus.value = Status.PENDING
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
                        "currency_type : ${p0?.currencyType} \n" +
                        "tran_date : ${p0?.tranDate}"
                )
                callAfterPaymentApi(p0)
            }

        }

        val sslCommerzInitialization = SSLCommerzInitialization(
                storeIdLive, storePasswordLive,
                totalAmountIntTaka.value!!.toDouble(), CurrencyType.BDT, paymentInfoData.transactionId,
                "Payment", SdkType.LIVE
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

    fun callAfterPaymentApi(data: TransactionInfoModel?) {
        viewModelScope.launch {
            try {
                val response = repository.callPaymentAfterReturningGatewayApi(data)
                Constants.isSMSFree = response.data?.get(0)?.isSMSFree.toString()
                if (response.statuscode == "4") {
                    _paymentStatus.value = Status.SUCCESS
                } else {
                    _paymentStatus.value = Status.FAILURE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    enum class Status {
        SUCCESS, FAILURE, PENDING
    }

}