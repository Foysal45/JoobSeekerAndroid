package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.repositories.CertificateRepository
import kotlinx.coroutines.launch

class CertificateViewModel(application: Application) : AndroidViewModel(application) {

    private val certificateRepository = CertificateRepository(application)

    private var certificateList: List<CertificateData?>? = null

    private val _certificates = MutableLiveData<List<CertificateData?>>()
    val certificates: LiveData<List<CertificateData?>>
        get() = _certificates

    private val _navigateToResultDetails = MutableLiveData<CertificateData>()
    val navigateToResultDetails: LiveData<CertificateData>
        get() = _navigateToResultDetails

    init {
        getCertificateList()
        //getPosts()
    }

    private fun getCertificateList() {
        Log.d("rakib", "called")
        viewModelScope.launch {
            try {
                certificateList = certificateRepository.getCertificateList().data
                _certificates.value = certificateList
                Log.d("rakib try", "${certificateList?.size}")
            } catch (e: Exception) {
                Log.d("rakib catch", e.message)
            }
        }
    }

    fun displayResultDetails(certificateData: CertificateData) {
        _navigateToResultDetails.value = certificateData
    }

    fun displayResultDetailsCompleted() {
        _navigateToResultDetails.value = null
    }

}