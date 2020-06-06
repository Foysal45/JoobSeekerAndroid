package com.bdjobs.app.videoInterview.ui.record_video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdjobs.app.videoInterview.util.Event

class RecordVideoViewModel() : ViewModel() {

    private val _videoRecordingStartedEvent = MutableLiveData<Event<Boolean>>()
    val videoRecordingStartedEvent : LiveData<Event<Boolean>> = _videoRecordingStartedEvent

    private fun onStartRecordingButtonClick()
    {
        _videoRecordingStartedEvent.value = Event(true)
    }
}