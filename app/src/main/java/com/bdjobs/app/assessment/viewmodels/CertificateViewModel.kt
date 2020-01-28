package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.bdjobs.app.assessment.models.Certificate
import com.bdjobs.app.assessment.models.Data
import com.bdjobs.app.assessment.models.Post
import com.bdjobs.app.assessment.network.AssessmentApi
import com.bdjobs.app.assessment.repositories.CertificateRepository
import kotlinx.coroutines.launch
import java.security.cert.CertificateFactory

class CertificateViewModel(application: Application) : AndroidViewModel(application) {

    private val certificateRepository = CertificateRepository(application)

    private var certificateList: List<Data?>? = null

    private var postList : List<Post>? = null

    private val _certificates = MutableLiveData<List<Data?>?>()
    val certificates: LiveData<List<Data?>?>
        get() = _certificates

    init {
        //getCertificateList()
        getPosts()
    }

    private fun getCertificateList() {
        Log.d("rakib", "called")
        viewModelScope.launch {
            try {
                certificateList =  certificateRepository.getCertificateList().data
                Log.d("rakib", "${certificateList?.size}")
            } catch (e: Exception) {
                Log.d("rakib", e.message)
            }
        }
    }

    private fun getPosts()
    {
        viewModelScope.launch {
            Log.d("rakib", "called post")
            try {

                postList = AssessmentApi.retrofitService.getPosts()

                Log.d("rakib", "called try")

            } catch (e: Exception) {

                Log.d("rakib", "called catch ${e.message}")

            }
        }
    }
}