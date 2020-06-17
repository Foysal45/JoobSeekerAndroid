package com.bdjobs.app.videoInterview.worker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException

class UploadVideoWorker(context: Context, params : WorkerParameters) : CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {
        Log.d("rakib", " in worker")
        try {
            VideoInterviewRepository(applicationContext as Application).postVideoToRemote()
        } catch (e: HttpException){
            Result.retry()
        }
        return Result.success()
    }

}