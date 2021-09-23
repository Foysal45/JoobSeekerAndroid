package com.bdjobs.app.ajkerDeal.ui.checkout_live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderRequest
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderResponse
import com.bdjobs.app.ajkerDeal.api.models.order.DeliveryInfoModel
import com.bdjobs.app.ajkerDeal.repository.AppRepository
import com.bdjobs.app.ajkerDeal.utilities.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class CheckoutLiveViewModel(private val repository: AppRepository): ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun insertLiveOrder(requestBody: List<LiveOrderRequest>): LiveData<List<LiveOrderResponse>> {

        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<LiveOrderResponse>>()
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.insertLiveOrder(requestBody)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        responseData.value = response.body.data!!
                    }
                    is NetworkResponse.ServerError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                        responseData.value = listOf()
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }

        return responseData
    }

    fun fetchDeliveryInfo(): LiveData<DeliveryInfoModel> {

        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<DeliveryInfoModel>()
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.fetchDeliveryInfo()
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body.data != null) {
                            responseData.value = response.body.data!!
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }

        return responseData

    }

}