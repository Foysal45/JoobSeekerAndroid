package com.bdjobs.app.ajkerDeal.ui.video_shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.ajkerDeal.api.PagingModel
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogRequest
import com.bdjobs.app.ajkerDeal.repository.AppRepository
import com.bdjobs.app.ajkerDeal.utilities.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class VideoShoppingViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    val pagingState = MutableLiveData<PagingModel<List<CatalogData>>>()
    private var isLoaded: Boolean = false

    fun getCatalogListByCat(index: Int = 0, categoryId: Int = 0, subCat: Int = 0, subSubCat: Int = 0): LiveData<List<CatalogData>> {

        val responseData: MutableLiveData<List<CatalogData>> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.getVideoShoppingList(CatalogRequest(0, index, categoryId = categoryId, subCategoryId = subCat, subSubCategoryId = subSubCat))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val dataList = response.body.data
                        if (dataList != null) {
                            responseData.value = dataList!!
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

    fun checkIsCustomerBlock(customerId: Int): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseData = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.checkIsCustomerBlock(customerId)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        responseData.value = response.body.data == 1
                    }
                    is NetworkResponse.ServerError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        //val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        //viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        //val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        //viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
    }

}