package com.bdjobs.app.training.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.Resource
import com.bdjobs.app.training.data.models.TrainingList
import com.bdjobs.app.training.data.repository.TrainingRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class UpcomingTrainingViewModel(private val repository: TrainingRepository) : ViewModel() {

    private var _trainingInfo = MutableLiveData<Resource<TrainingList>>()
    val trainingInfo: LiveData<Resource<TrainingList>> = _trainingInfo

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var _allSelected = MutableLiveData<Boolean>()
    val allSelected: LiveData<Boolean> = _allSelected


    private var _suggestedSelected = MutableLiveData<Boolean>()
    val suggestedSelected: LiveData<Boolean> = _suggestedSelected

    init {
        if (Constants.matchedTraining) {
            _allSelected.value = false
            _suggestedSelected.value = true
        } else {
            _suggestedSelected.value = false
            _allSelected.value = true
        }
    }

    fun fetchTrainingList(trainingId: String) {

        _trainingInfo.value = Resource.loading()
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.fetchTrainingList(trainingId)

                if (response.isSuccessful && response.code() == 200) {
                    if (response.body()?.message == "Success" && response.body()?.statuscode == "0") {

                        _trainingInfo.value = Resource.success(response.body())
                        _isLoading.value = false
                    } else {
                        _trainingInfo.value = Resource.error(response.body()?.message)
                    }
                } else {
                    // unsuccessful response
                    _trainingInfo.value = Resource.error(response.message())
                }

            } catch (e: Throwable) {
                _isLoading.value = false
                Timber.e("Exception while fetching training list: ${e.localizedMessage}")
                if (e is IOException) _trainingInfo.value = Resource.error("Please check your internet connection & try again")
                else _trainingInfo.value = Resource.error(e.localizedMessage)
            }
        }
    }

    fun onAllClicked() {
        _allSelected.value = true
        _suggestedSelected.value = false

        Constants.matchedTraining = false

    }

    fun onSuggestedClicked() {
        _suggestedSelected.value = true
        _allSelected.value = false

        Constants.matchedTraining = true
    }

}