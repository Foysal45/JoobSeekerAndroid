package com.bdjobs.app.liveInterview.ui.audio_record

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.util.Event
import timber.log.Timber

//
// Created by Soumik on 5/5/2021.
// piyal.developer@gmail.com
//

class AudioRecordViewModel:ViewModel() {

    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _onVideoDoneEvent = MutableLiveData<Boolean>()
    val onVideoDoneEvent: LiveData<Boolean> = _onVideoDoneEvent

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long> = _currentTime
    private val _shouldShowDoneButton = MutableLiveData<Boolean>().apply {
        value = false
    }
    val shouldShowDoneButton: LiveData<Boolean> = _shouldShowDoneButton
    var secondsRemaining = 0L
    val elapsedTimeInString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }


    private val _progressPercentage = MutableLiveData<Double>()
    val progressPercentage: LiveData<Double> = _progressPercentage

    private fun startRecording() {
        _onVideoRecordingStartedEvent.value = Event(true)
//        startTimer()
    }

    fun stopRecording() {
        timer.cancel()
        _onVideoDoneEvent.value = true
    }

    init {
        startRecording()
    }

    private fun startTimer() {
        val numberOfSeconds = TOTAL_TIME.toLong().times(1000).div(1000)
        val factor: Double = (100.0 / numberOfSeconds.toDouble())
        timer = object : CountDownTimer(TOTAL_TIME.toLong().times(1000).plus(1000), 1000) {
            override fun onFinish() {
                _progressPercentage.value = 100.toDouble()
                _onVideoDoneEvent.postValue(true)
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
        const val TOTAL_TIME = "180"
    }
}