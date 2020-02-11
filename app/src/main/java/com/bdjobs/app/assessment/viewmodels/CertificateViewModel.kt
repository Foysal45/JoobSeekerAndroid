package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.repositories.CertificateRepository
import kotlinx.coroutines.launch

class CertificateViewModel(application: Application) : AndroidViewModel(application) {

    private val certificateRepository = CertificateRepository(application)

    private var certificateList: List<CertificateData?>? = null

    private val _certificates = MutableLiveData<List<CertificateData?>>()
    val certificates: LiveData<List<CertificateData?>>
        get() = _certificates

    private val _navigateToResultDetails = MutableLiveData<Event<CertificateData>>()
    val navigateToResultDetails: LiveData<Event<CertificateData>>
        get() = _navigateToResultDetails

    private val _status = MutableLiveData<Status>()
    val status :LiveData<Status>
        get() = _status

    init {
        getCertificateList()
    }

    private fun getCertificateList() {
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
                _status.value = Status.LOADING
                certificateList = certificateRepository.getCertificateList().data
                _certificates.value = certificateList
                _status.value = Status.DONE
            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

    fun displayResultDetails(certificateData: CertificateData) {
        _navigateToResultDetails.value = Event(certificateData)
    }
}