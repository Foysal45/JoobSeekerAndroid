package com.bdjobs.demo_connect_employer.streaming

import org.webrtc.*
import timber.log.Timber

open class CustomPCObserver(): PeerConnection.Observer {


    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Timber.d("onSignalingChange: $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Timber.d("onIceConnectionChange: $p0")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Timber.d( "onIceConnectionReceivingChange: $p0")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Timber.d("onIceGatheringChange: $p0")
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        Timber.d( "onIceCandidate: $p0")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Timber.d("onIceCandidatesRemoved: $p0")
    }

    override fun onAddStream(p0: MediaStream?) {
        Timber.d("onAddStream: $p0")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Timber.d("onRemoveStream: $p0")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Timber.d("onDataChannel: $p0")
        Timber.d("onDataChannel state: %s", p0?.state())

    }

    override fun onRenegotiationNeeded() {
        Timber.d("onRenegotiationNeeded: ")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Timber.d("onAddTrack: ${p1?.size} rtpReceiver : ${p0?.id()}")
    }
}