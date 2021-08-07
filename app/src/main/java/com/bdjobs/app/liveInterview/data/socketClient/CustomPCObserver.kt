package com.bdjobs.demo_connect_employer.streaming

import org.webrtc.*
import timber.log.Timber

open class CustomPCObserver(): PeerConnection.Observer {


    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
        Timber.d("CustomPCO onSignalingChange: $p0")
    }

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
        Timber.d("CustomPCO onIceConnectionChange: $p0")
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
        Timber.d( "CustomPCO onIceConnectionReceivingChange: $p0")
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
        Timber.d("CustomPCO onIceGatheringChange: $p0")
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        Timber.d( "CustomPCO onIceCandidate: $p0")
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
        Timber.d("CustomPCO onIceCandidatesRemoved: $p0")
    }

    override fun onAddStream(p0: MediaStream?) {
        Timber.d("CustomPCO onAddStream: $p0")
    }

    override fun onRemoveStream(p0: MediaStream?) {
        Timber.d("CustomPCO onRemoveStream: $p0")
    }

    override fun onDataChannel(p0: DataChannel?) {
        Timber.d("CustomPCO onDataChannel: $p0")
        Timber.d("CustomPCO onDataChannel state: %s", p0?.state())

    }

    override fun onRenegotiationNeeded() {
        Timber.d("onRenegotiationNeeded: ")
    }

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
        Timber.d("onAddTrack: ${p1?.size} rtpReceiver : ${p0?.id()}")
    }
}