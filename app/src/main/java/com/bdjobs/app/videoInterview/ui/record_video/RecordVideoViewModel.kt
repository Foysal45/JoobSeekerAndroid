package com.bdjobs.app.videoInterview.ui.record_video

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import timber.log.Timber

class RecordVideoViewModel(private val repository: VideoInterviewRepository) : ViewModel() {

    private lateinit var timer: CountDownTimer

    private val _onVideoRecordingStartedEvent = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }
    val onVideoRecordingStartedEvent: LiveData<Event<Boolean>> = _onVideoRecordingStartedEvent

    private val _progressPercentage = MutableLiveData<Long>()
    val progressPercentage : LiveData<Long> = _progressPercentage

    fun onStartRecordingButtonClick() {
        _onVideoRecordingStartedEvent.value = Event(true)

        startTimer()


    }

    private fun startTimer() {
        val numberOfSeconds  = 11000/1000
        val factor = 100 / numberOfSeconds

        timer = object : CountDownTimer(11000, 1000) {
            override fun onFinish() {
                _progressPercentage.value = 100L
            }

            override fun onTick(millisUntilFinished: Long) {
                var secondsRemaining = millisUntilFinished / 1000
                Timber.d("$secondsRemaining")
                _progressPercentage.value = (numberOfSeconds - secondsRemaining) * factor

            }

        }.start()
    }
}