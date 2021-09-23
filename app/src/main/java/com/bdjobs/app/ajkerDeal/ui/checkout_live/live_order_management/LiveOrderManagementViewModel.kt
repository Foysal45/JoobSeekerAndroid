package com.bdjobs.app.ajkerDeal.ui.checkout_live.live_order_management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livevideoshopping.api.PagingModel
import com.example.livevideoshopping.api.models.live_order_management.LiveOrderManagementResponseBody
import com.example.livevideoshopping.repository.AppRepository
import com.example.livevideoshopping.utilities.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveOrderManagementViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    val pagingState: MutableLiveData<PagingModel<List<LiveOrderManagementResponseBody>>> = MutableLiveData()

    fun fetchLiveOrderManagementList(customerId: Int, index: Int, count: Int): LiveData<List<LiveOrderManagementResponseBody>> {

        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<List<LiveOrderManagementResponseBody>>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.fetchLiveOrderManagementList(customerId, index, count)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val productList = response.body.data
                        if (productList != null) {
                            if (index == 0) {
                                Timber.d("PagingDebug pagingState set value with true $index")
                                pagingState.value = PagingModel(true, response.body.totalCount, productList)
                            } else {
                                Timber.d("PagingDebug pagingState set value with false $index")
                                pagingState.value = PagingModel(false, response.body.totalCount, productList)
                            }
                            Timber.d("TestingModel $productList")
                        }
                    }
                    is NetworkResponse.ServerError -> {

                        pagingState.value = PagingModel(true, 0, listOf())
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
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