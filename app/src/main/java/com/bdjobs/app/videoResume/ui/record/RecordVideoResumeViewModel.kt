package com.bdjobs.app.videoResume.ui.record

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.*
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.videoInterview.util.Event
import com.bdjobs.app.videoResume.data.models.VideoResumeManager
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
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


    private val _onVideouploadException = MutableLiveData<Event<String>>()
    val onVideoUploadException : LiveData<Event<String>> = _onVideouploadException

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
        _onVideoDoneEvent.postValue(true) //= true

    }

    fun uploadSingleVideoToServer(videoResumeManager: VideoResumeManager?) {
        _onVideoDoneEvent.postValue(false)
        Constants.createVideoResumeManagerDataForUpload(videoResumeManager)
        GlobalScope.launch {
                _onUploadStartEvent.postValue(Event(true))// = Event(true)
            try {
                val response = repository.postVideoResumeToRemote()
                if (response.isSuccessful && response.code()==200) {
                    if (response.body()?.statuscode == "4" || response.body()?.statuscode == 4)
                    {
                        _onUploadDoneEvent.postValue(Event(true)) //= Event(true)
                    }
                }

            } catch (e: Exception) {
                _onVideouploadException.postValue(Event((e.message.toString())))
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

}