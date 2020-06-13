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

class RecordVideoViewModel(private val repository: VideoInterviewRepository) : ViewModel() {

    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _progressPercentage = MutableLiveData<Double>()
    val progressPercentage : LiveData<Double> = _progressPercentage

    private val _videoManagerData = MutableLiveData<VideoManager?>()
    val videoManagerData : LiveData<VideoManager?> = _videoManagerData

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime : LiveData<Long> = _currentTime

    private val _shouldShowDoneButton = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldShowDoneButton : LiveData<Boolean> = _shouldShowDoneButton

    private val _onVideoDoneEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoDoneEvent : LiveData<Event<Boolean>> = _onVideoDoneEvent


    var secondsRemaining = 0L

    val elapsedTimeInString = Transformations.map(currentTime){ time->
        DateUtils.formatElapsedTime(time)

    }

    fun onStartRecordingButtonClick() {
        _onVideoRecordingStartedEvent.value = Event(true)
        sendVideoStartedInfoToRemote()
        startTimer()
    }

    fun onDoneButtonClick(){
        //sendVideoStartedInfoToRemote()
        timer.cancel()
        _onVideoDoneEvent.value = Event(true)
        //uploadSingleVideoToServer()
    }

    fun uploadSingleVideoToServer(videoManager: VideoManager?) {
        Log.d("rakib","$videoManager")
        //repository.setDataForUpload(videoManager)
        Constants.createVideoManagerDataForUpload(videoManager)
        viewModelScope.launch {
            val constraints = androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val request = OneTimeWorkRequestBuilder<UploadVideoWorker>().setConstraints(constraints).build()
            WorkManager.getInstance().enqueue(request)
        }
    }

    private fun sendVideoStartedInfoToRemote() {
        viewModelScope.launch {
            try {
                val response = repository.postVideoStartedInformationToRemote(videoManagerData.value!!)
            } catch (e:Exception){

            }
        }
    }

    private fun startTimer() {
        val numberOfSeconds  = videoManagerData.value!!.questionDuration!!.toLong().times(1000).div(1000)
        val factor : Double = (100.0 / numberOfSeconds.toDouble())
        timer = object : CountDownTimer(videoManagerData.value!!.questionDuration!!.toLong().times(1000), 1000) {
            override fun onFinish() {
                _progressPercentage.value = 100.toDouble()
                _onVideoDoneEvent.value = Event(true)
            }

            override fun onTick(millisUntilFinished: Long) {
                 secondsRemaining = millisUntilFinished / 1000
                _currentTime.value = secondsRemaining
                Timber.d("$secondsRemaining")
                _progressPercentage.value = (numberOfSeconds - secondsRemaining) * factor

//                if (numberOfSeconds -  secondsRemaining >= numberOfSeconds / 3){
//                    _shouldShowDoneButton.value = true
//                }

                _shouldShowDoneButton.value = true


            }

        }.start()
    }

    fun prepareData(videoManager: VideoManager?)
    {
        _videoManagerData.value = videoManager
    }

}