package com.bdjobs.app.Utilities.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

class CameraXprovider(val viewLifecycleOwner : LifecycleOwner, val  previewView : PreviewView, val context : Context) : CameraProvider() {



    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imagePreview: Preview? = null
    private var videoCapture: VideoCapture? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    val RESOLUTION_WEIDTH = 640
    val RESOLUTION_HEIGHT = 480
    var callback: OutputCallBack? = null


//    fun setOutPutCallback(callback: OutputCallBack?) {
//        this.callback = callback
//    }

    override fun initlize() {
        startCamera()
    }



    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        cameraProviderFuture.addListener({
            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
            }.build()

            videoCapture = VideoCapture.Builder().apply {
                setTargetResolution(Size(abs(RESOLUTION_HEIGHT), abs(RESOLUTION_WEIDTH)))
                setBitRate(1*RESOLUTION_HEIGHT*RESOLUTION_WEIDTH)

            }.build()

            val cameraProvider = cameraProviderFuture.get()

            val camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                imagePreview,
                videoCapture
            )
            previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            imagePreview?.setSurfaceProvider(previewView.surfaceProvider)
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
        }, ContextCompat.getMainExecutor(context))
    }




    @SuppressLint("RestrictedApi")
    override fun stop() {
        videoCapture?.stopRecording()
    }

    override fun record(newFile: File) {

        startRecord(newFile)
    }

    @SuppressLint("RestrictedApi")
    private fun startRecord(newFile: File) {


        val outputFileOptions = VideoCapture.OutputFileOptions.Builder(newFile).build()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        videoCapture?.startRecording(outputFileOptions, cameraExecutor, object : VideoCapture.OnVideoSavedCallback {
            @SuppressLint("TimberArgCount", "BinaryOperationInTimber")
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                callback?.videoRecordSuccess(outputFileResults.savedUri!!.toFile())
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                callback?.videoRecordFailed(message)
            }
        })


    }
}