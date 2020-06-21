package com.bdjobs.app.videoInterview.ui.record_video

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoInterview.worker.UploadVideoWorker
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RecordVideoViewModel(private val repository: VideoInterviewRepository) : ViewModel() {

    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _progressPercentage = MutableLiveData<Double>()
    val progressPercentage: LiveData<Double> = _progressPercentage

    private val _videoManagerData = MutableLiveData<VideoManager?>()
    val videoManagerData: LiveData<VideoManager?> = _videoManagerData

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long> = _currentTime

    private val _shouldShowDoneButton = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldShowDoneButton: LiveData<Boolean> = _shouldShowDoneButton

    private val _onVideoDoneEvent = MutableLiveData<Boolean>()
    val onVideoDoneEvent : LiveData<Boolean> = _onVideoDoneEvent

//    private val _onVideoDoneEvent = MutableLiveData<Event<Boolean>>().apply {
//        value = Event(false)
//    }
//    val onVideoDoneEvent: LiveData<Event<Boolean>> = _onVideoDoneEvent

    private val _onUploadStartEvent = MutableLiveData<Event<Boolean>>()
    val onUploadStartEvent : LiveData<Event<Boolean>> = _onUploadStartEvent

    private val _onUploadDoneEvent = MutableLiveData<Event<Boolean>>()
    val onUploadDoneEvent : LiveData<Event<Boolean>> = _onUploadDoneEvent

    var secondsRemaining = 0L

    val elapsedTimeInString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)

    }

    fun onStartRecordingButtonClick() {
        _onVideoRecordingStartedEvent.value = Event(true)
        sendVideoStartedInfoToRemote()
        startTimer()
    }

    fun onDoneButtonClick() {
        timer.cancel()
        _onVideoDoneEvent.value = true
    }

    fun uploadSingleVideoToServer(videoManager: VideoManager?) {
        _onVideoDoneEvent.value = false
        Log.d("rakib", "$videoManager")
        //repository.setDataForUpload(videoManager)
        Constants.createVideoManagerDataForUpload(videoManager)
        viewModelScope.launch {
//            val constraints = androidx.work.Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            val request = OneTimeWorkRequestBuilder<UploadVideoWorker>()
//                    .setConstraints(constraints)
//                    .setBackoffCriteria(BackoffPolicy.LINEAR,
//                            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
//                            TimeUnit.MILLISECONDS)
//                    .build()
//            WorkManager.getInstance().enqueue(request)
            _onUploadStartEvent.value = Event(true)
            val response = repository.postVideoToRemote()
            if (response.statuscode == "4" || response.statuscode == 4)
            {
                _onUploadDoneEvent.value = Event(true)
            }
        }
    }

    private fun sendVideoStartedInfoToRemote() {
        viewModelScope.launch {
            try {
                val response = repository.postVideoStartedInformationToRemote(videoManagerData.value!!)
                Constants.recordingStarted = true
            } catch (e: Exception) {

            }
        }
    }

    private fun startTimer() {
        val numberOfSeconds = videoManagerData.value!!.questionDuration!!.toLong().times(1000).div(1000)
        val factor: Double = (100.0 / numberOfSeconds.toDouble())
        timer = object : CountDownTimer(videoManagerData.value!!.questionDuration!!.toLong().times(1000).plus(1000), 1000) {
            override fun onFinish() {
                _progressPercentage.value = 100.toDouble()
                _onVideoDoneEvent.value = (true)
                _currentTime.value = 0L
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                _currentTime.value = secondsRemaining
                Timber.d("$secondsRemaining")
                _progressPercentage.value = (numberOfSeconds - secondsRemaining) * factor

                if (numberOfSeconds -  secondsRemaining >= numberOfSeconds / 3){
                    _shouldShowDoneButton.value = true
                }

//                _shouldShowDoneButton.value = true

            }

        }.start()
    }

    fun prepareData(videoManager: VideoManager?) {
        _videoManagerData.value = videoManager
    }

}