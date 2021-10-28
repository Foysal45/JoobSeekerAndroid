package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.video_reply_comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.ajkerDeal.api.models.video_comments.VideoInsertReplyCommentsRequest
import com.bdjobs.app.ajkerDeal.utilities.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class VideoReplyCommentsViewModel (private val repository: com.bdjobs.app.ajkerDeal.api.ApiInterfaceAPI) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    fun insertVideoReplyComments(customerId: Int, replyComment: String, insertedOn: String, vsCatalogId: Int, commentId: Int): LiveData<Boolean> {
        viewState.value = ViewState.ProgressState(true)
        val responseBody = MutableLiveData<Boolean>()

        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.insertVideoReplyComments(VideoInsertReplyCommentsRequest(customerId, replyComment, insertedOn, vsCatalogId, commentId))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (response.body != null) {
                            val message = "Successfully added"
                            viewState.value = ViewState.ShowMessage(message)
                            responseBody.value = response.body.data!! > 0
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
        return responseBody
    }
}