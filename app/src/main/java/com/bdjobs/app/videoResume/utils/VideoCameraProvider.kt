package com.bdjobs.app.videoResume.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Size
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import kotlinx.android.synthetic.main.fragment_record_video_resume.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

class VideoCameraProvider(val context : Context, val camera_view : CameraView, val pre_view : PreviewView, val viewLifecycleOwner : LifecycleOwner) {


    var sdk =   Integer.valueOf(Build.VERSION.SDK_INT)
    lateinit var videoFile: File
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imagePreview: Preview? = null
    private var videoCapture: VideoCapture? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    val RESOLUTION_WEIDTH = 640
    val RESOLUTION_HEIGHT = 480

    fun initilizeCamera() {

        if (sdk < 23){
            camera_view.visibility = View.VISIBLE
            pre_view.visibility = View.GONE
            initializeCamera()
        }else{
            pre_view.visibility = View.VISIBLE
            camera_view.visibility = View.GONE
            startCamera()
        }

    }

    fun recordVideo( newFile : File)  : File{

        lateinit var video_file : File

        if (sdk < 23){
            camera_view.mode = Mode.VIDEO
            camera_view.takeVideoSnapshot(newFile)

            camera_view.addCameraListener(object : CameraListener() {
                override fun onVideoTaken(result: com.otaliastudios.cameraview.VideoResult) {
                    super.onVideoTaken(result)
                    Timber.d("video taken!")
                    video_file = result.file
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

        }else{
            video_file =   startRecord(newFile)
        }


        return video_file

    }


    private fun initializeCamera()  {

        camera_view.setLifecycleOwner(viewLifecycleOwner)

        try {
            camera_view.facing = Facing.FRONT
        } catch (e: Exception) {
            camera_view.facing = Facing.BACK
        }
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
            pre_view.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            imagePreview?.setSurfaceProvider(pre_view.surfaceProvider)
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
        }, ContextCompat.getMainExecutor(context))
    }



    @SuppressLint("RestrictedApi")
    private fun startRecord(newFile: File) : File {

        lateinit var video_file : File

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return video_file
        }

        val outputFileOptions = VideoCapture.OutputFileOptions.Builder(newFile).build()

        videoCapture?.startRecording(outputFileOptions, cameraExecutor, object : VideoCapture.OnVideoSavedCallback {
            @SuppressLint("TimberArgCount", "BinaryOperationInTimber")
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {

                video_file = outputFileResults.savedUri!!.toFile()


            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {

            }
        })

        return  video_file
    }


    interface VideoResultinterface{
        fun videoRecordresult(file : File)
    }

}