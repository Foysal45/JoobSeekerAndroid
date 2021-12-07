package com.bdjobs.app.utilities.camera

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import timber.log.Timber
import java.io.File

class Camera2Provider (val viewLifecycleOwner : LifecycleOwner, val  camera_view : CameraView, val context : Context) : CameraProvider() {

    var callback: OutputCallBack? = null


    override fun initlize() {
        camera_view.setLifecycleOwner(viewLifecycleOwner)

        try {
            camera_view.facing = Facing.FRONT
        } catch (e: Exception) {
            camera_view.facing = Facing.BACK
        }
    }

    override fun stop() {
        camera_view.stopVideo()
    }

    override fun record(newFile: File) {
        camera_view.mode = Mode.VIDEO
        camera_view.takeVideoSnapshot(newFile)

        camera_view.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: com.otaliastudios.cameraview.VideoResult) {
                super.onVideoTaken(result)
                Timber.d("video taken!")
                callback?.videoRecordSuccess( result.file)
            }

            override fun onVideoRecordingStart() {
                Timber.d("video recording start!")
                super.onVideoRecordingStart()
            }

            override fun onVideoRecordingEnd() {
                Timber.d("video recording start!")
                super.onVideoRecordingEnd()
            }
        })
    }






}