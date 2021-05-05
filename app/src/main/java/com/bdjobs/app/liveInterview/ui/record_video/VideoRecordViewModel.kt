package com.bdjobs.app.liveInterview.ui.record_video

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bdjobs.app.liveInterview.data.models.LiveInterviewVideo
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.util.Event
import timber.log.Timber

//
// Created by Soumik on 5/4/2021.
// piyal.developer@gmail.com
//

class VideoRecordViewModel : ViewModel() {


    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _onVideoDoneEvent = MutableLiveData<Boolean>()
    val onVideoDoneEvent: LiveData<Boolean> = _onVideoDoneEvent

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long> = _currentTime

    val _videoManagerData = MutableLiveData<LiveInterviewVideo?>()
    val videoManagerData: LiveData<LiveInterviewVideo?> = _videoManagerData

    val _navigateToViewVideoEvent = MutableLiveData<Boolean>()
    val navigateToViewVideoEvent : LiveData<Boolean> = _navigateToViewVideoEvent

    private val _shouldShowDoneButton = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldShowDoneButton: LiveData<Boolean> = _shouldShowDoneButton

    var secondsRemaining = 0L

    val elapsedTimeInString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)

    }

    val _progressPercentage = MutableLiveData<Double>()
    val progressPercentage: LiveData<Double> = _progressPercentage

    fun onStartRecordingButtonClick() {
        _onVideoRecordingStartedEvent.value = Event(true)
        startTimer()
    }

    fun onStopRecordingButtonClick() {
        timer.cancel()
        _onVideoDoneEvent.value = true
    }


    private fun startTimer() {
        val numberOfSeconds = TOTAL_TIME.toLong().times(1000).div(1000)
        val factor: Double = (100.0 / numberOfSeconds.toDouble())
        timer = object : CountDownTimer(TOTAL_TIME.toLong().times(1000).plus(1000), 1000) {
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

                if (numberOfSeconds - secondsRemaining >= numberOfSeconds / 3) {
                    _shouldShowDoneButton.value = true
                }

//                _shouldShowDoneButton.value = true

            }

        }.start()
    }

    companion object {
        const val TOTAL_TIME = "10"
    }
}