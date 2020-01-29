package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.models.Result
import com.bdjobs.app.assessment.models.ResultData
import com.bdjobs.app.assessment.repositories.ResultRepository
import kotlinx.coroutines.launch

class ResultViewModel(certificateData: CertificateData, application: Application) : AndroidViewModel(application) {

    private var resultRepository : ResultRepository


    private val _result = MutableLiveData<ResultData?>()
    val result : LiveData<ResultData?>
        get() = _result

    private val _selectedCertificate = MutableLiveData<CertificateData>()
    val selectedCertificate: LiveData<CertificateData>
        get() = _selectedCertificate



    init {
        _selectedCertificate.value = certificateData
        resultRepository = ResultRepository(application,certificateData)
        getResults()
    }

    private fun getResults() {
        viewModelScope.launch {
            try {
                _result.value = resultRepository.getResult().data?.get(0)
            } catch (e: Exception) {
            }
        }
    }


}