package com.bdjobs.app.videoResume.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoResume.data.models.Question
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import com.loopj.android.http.AsyncHttpClient.log
import kotlinx.coroutines.launch
import timber.log.Timber

class VideoResumeLandingViewModel(
    private val videoResumeRepository: VideoResumeRepository
) : ViewModel() {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean> = _isDataLoading

    private val _isSubmitStatusLoading = MutableLiveData<Boolean>()
    val isSubmitStatusLoading: LiveData<Boolean> = _isSubmitStatusLoading

    private val _statusPercentage = MutableLiveData<String?>().apply {
        value = "0"
    }
    val statusPercentage: LiveData<String?> = _statusPercentage

    private val _lastUpdate = MutableLiveData<String?>().apply {
        value = "--"
    }
    val lastUpdate: LiveData<String?> = _lastUpdate

    private val _rating = MutableLiveData<String?>().apply {
        value = "0"
    }
    val rating: LiveData<String?> = _rating

    private val _overallRating = MutableLiveData<Int>()
    val overallRating: LiveData<Int> = _overallRating

    private val _totalView = MutableLiveData<String?>().apply {
        value = "0"
    }
    val totalView: LiveData<String?> = _totalView

    private val _totalCompanyView = MutableLiveData<String?>().apply {
        value = "0"
    }
    val totalCompanyView: LiveData<String?> = _totalCompanyView

    private val _maxProgress = MutableLiveData<Int>()
    val maxProgress: LiveData<Int> = _maxProgress

    private val _totalProgress = MutableLiveData<Int>()
    val totalProgress: LiveData<Int> = _totalProgress

    private val _totalAnswered = MutableLiveData<String?>().apply {
        value = "0"
    }
    val totalAnswered: LiveData<String?> = _totalAnswered

    private val _totalQuestions = MutableLiveData<String?>().apply { value = "0" }
    val totalQuestions: LiveData<String?> get() = _totalQuestions

    private val _threshold = MutableLiveData<String?>().apply {
        value = "0"
    }
    val threshold: LiveData<String?> = _threshold

    private val _showStat = MutableLiveData<Boolean>()
    val showStat: LiveData<Boolean> = _showStat

    private var _showResumeVisibilityView = MutableLiveData<Boolean>()
    val showResumeVisibilityView: LiveData<Boolean> = _showResumeVisibilityView

    private var _showNoAnimatorView = MutableLiveData<Boolean>()
    val showNoAnimatorView: LiveData<Boolean> = _showNoAnimatorView

    private val _isAlertOn = MutableLiveData<String?>()
    val isAlertOn: LiveData<String?> = _isAlertOn

    val eventYesClicked = MutableLiveData<Event<Boolean>>()
    val yesSelected = MutableLiveData<Boolean>()

    val eventNoClicked = MutableLiveData<Event<Boolean>>()
    val noSelected = MutableLiveData<Boolean>()

    private val _openTurnOffVisibilityDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openTurnOffVisibilityDialogEvent: LiveData<Event<Boolean>> =
        _openTurnOffVisibilityDialogEvent

    private val _openTurnOnVisibilityDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openTurnOnVisibilityDialogEvent: LiveData<Event<Boolean>> = _openTurnOnVisibilityDialogEvent

    private val _openMessageDialogEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val openMessageDialogEvent: LiveData<Event<Boolean>> = _openMessageDialogEvent

    private val _statusCode = MutableLiveData<String>()
    val statusCode: LiveData<String> = _statusCode

    var showVideoResumeToEmployers = MutableLiveData<Boolean>()

    fun onCheckedChanged(checked: Boolean) {
        try {
            if (totalAnswered.value!!.toInt() < threshold.value!!.toInt()) {
                _isAlertOn.value = "0"
                yesSelected.value = false
                noSelected.value = true
                _openMessageDialogEvent.value = Event(true)
            } else if (!checked) {
                _isAlertOn.value = "0"
                yesSelected.value = false
                noSelected.value = true
                _openTurnOffVisibilityDialogEvent.value = Event(true)

                updateResumeVisibility()
            } else {
                _isAlertOn.value = "1"
                noSelected.value = false
                yesSelected.value = true
                _openTurnOnVisibilityDialogEvent.value = Event(true)
//                updateResumeVisibility()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun onYesClicked() {
        onCheckedChanged(true)
    }

    fun onNoClicked() {
        onCheckedChanged(false)
    }

    fun onHideResumeVisibility() {
        _showNoAnimatorView.value = true
        updateResumeVisibility()
    }

    fun onShowResumeVisibility() {
        _showNoAnimatorView.value = false
        updateResumeVisibility()
    }

    fun notChangeResumeVisibility() {
        if (_openTurnOnVisibilityDialogEvent.equals("true")) {
            _isAlertOn.value = "1"
            noSelected.value = false
            yesSelected.value = true
        }
        if (_openTurnOffVisibilityDialogEvent.equals("true")) {
            _isAlertOn.value = "0"
            noSelected.value = true
            yesSelected.value = false
        }

    }

    fun getAllQuestions(): List<Question> {
        return videoResumeRepository.getAllQuestionsFromDBInBn()
    }

    fun getStatistics() {
        _isDataLoading.value = true
        viewModelScope.launch {
            try {
                val response = videoResumeRepository.getStatisticsFromRemote()

                if (response.isSuccessful && response.code()==200) {
                    val data = response.body()?.data?.get(0)
                    _isDataLoading.value = false
                    _statusPercentage.value = data?.statusPercentage
                    _lastUpdate.value = data?.lastUpdateDate
                    _totalView.value = data?.totalView
                    _totalCompanyView.value = data?.totalCompanyView
                    _rating.value = data?.rating
                    _overallRating.value = rating.value?.toInt()
                    _totalAnswered.value = data?.totalAnswered
                    _showStat.value = totalAnswered.value!! != "0"
                    Timber.d("ShowStat: ${_showStat.value}")
                    _isAlertOn.value = if (_showStat.value!!) data?.resumeVisibility else "2"
                    _totalQuestions.value = data?.totalQuestion
                    _totalProgress.value = statusPercentage.value?.toInt()
                    _threshold.value = data?.threshold
                    _maxProgress.value = 100
                    _statusCode.value = response.body()?.statuscode!!

                    Timber.d("isAlertOn: ${_isAlertOn.value}")

                    if (_showStat.value == true) {
                        _showResumeVisibilityView.value =
                            totalAnswered.value!!.toInt() >= threshold.value!!.toInt()
                    } else {
                        _showResumeVisibilityView.value = false
                    }

                    if (_showResumeVisibilityView.value == true)
                        _showNoAnimatorView.value = _isAlertOn.value == "0"
                    else _showNoAnimatorView.value = false

                    showVideoResumeToEmployers.value = totalAnswered.value!!.toInt() >= threshold.value!!.toInt()

                    when {
                        _isAlertOn.value!!.equalIgnoreCase("0") -> {
                            yesSelected.value = false
                            noSelected.value = true
                        }
                        _isAlertOn.value!!.equalIgnoreCase("1") -> {
                            noSelected.value = false
                            yesSelected.value = true
                        }
                        else -> {
                            yesSelected.value = false
                            noSelected.value = true
                        }
                    }
                }



            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateResumeVisibility() {
        _isSubmitStatusLoading.value = true
        viewModelScope.launch {
            try {
                val response = videoResumeRepository.submitStatusVisibility(
                    isVisible = isAlertOn.value
                )

                if (response.isSuccessful && response.code()==200) {
                    _isSubmitStatusLoading.value = false
                } else {
                    _isSubmitStatusLoading.value = false
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}