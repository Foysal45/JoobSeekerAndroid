package com.bdjobs.app.videoResume.ui.record

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.*
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoResume.data.models.VideoResumeManager
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class RecordVideoResumeViewModel(private val repository: VideoResumeRepository) : ViewModel() {

    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _onVideoStarted = MutableLiveData<Boolean>().apply{
        value = false
    }
    val onVideoStarted : LiveData<Boolean> = _onVideoStarted

    private val _progressPercentage = MutableLiveData<Double>()
    val progressPercentage: LiveData<Double> = _progressPercentage

    private val _videoResumeManagerData = MutableLiveData<VideoResumeManager?>()
    val videoResumeManagerData: LiveData<VideoResumeManager?> = _videoResumeManagerData

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
        _onVideoStarted.value = true
        startTimer()
    }

    fun onDoneButtonClick() {
        timer.cancel()
        _onVideoDoneEvent.value = true

    }

    fun uploadSingleVideoToServer(videoResumeManager: VideoResumeManager?) {
        _onVideoDoneEvent.postValue(false)// = false
        //Log.d("rakib", "$videoManager")
        //repository.setDataForUpload(videoManager)
        Constants.createVideoResumeManagerDataForUpload(videoResumeManager)
        GlobalScope.launch(Dispatchers.Main) {
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
            _onUploadStartEvent.postValue(Event(true))// = Event(true)
            val response = repository.postVideoResumeToRemote()
            if (response.statuscode == "4" || response.statuscode == 4)
            {
                _onUploadDoneEvent.postValue(Event(true)) //= Event(true)
            }
        }
    }



    private fun startTimer() {
        val numberOfSeconds = videoResumeManagerData.value!!.questionDuration!!.toLong().times(1000).div(1000)
        val factor: Double = (100.0 / numberOfSeconds.toDouble())
        timer = object : CountDownTimer(videoResumeManagerData.value!!.questionDuration!!.toLong().times(1000).plus(1000), 1000) {
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

    fun prepareData(videoResumeManager: VideoResumeManager?) {
        _videoResumeManagerData.value = videoResumeManager
    }

    override fun onCleared() {
        super.onCleared()
    }
}