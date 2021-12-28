package com.bdjobs.app.liveInterview.data.socketClient

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import timber.log.Timber

open class CustomSdpObserver : SdpObserver {

    override fun onSetFailure(p0: String?) {
        Timber.d("CustomSdpObserver $p0")
    }

    override fun onSetSuccess() {
        Timber.d("CustomSdpObserver onSetSuccess ")

    }

    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
        Timber.d("CustomSdpObserver onCreateSuccess sessionDescription ${sessionDescription?.description}")
    }

    override fun onCreateFailure(p0: String?) {
        Timber.d("CustomSdpObserver onCreateFailure $p0")

    }
}