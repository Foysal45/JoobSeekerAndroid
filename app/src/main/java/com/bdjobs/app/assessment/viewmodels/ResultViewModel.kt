package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.assessment.Event
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.assessment.models.ResultData
import com.bdjobs.app.assessment.repositories.ResultRepository
import kotlinx.coroutines.launch


class ResultViewModel(certificateData: CertificateData, application: Application) : AndroidViewModel(application) {

    private var resultRepository: ResultRepository = ResultRepository(application, certificateData)

    var downloadLink : String? = ""

    private val _result = MutableLiveData<ResultData?>()
    val result: LiveData<ResultData?>
        get() = _result

    private val _resultMessage = MutableLiveData<String>()
    val resultMessage : LiveData<String>
        get() = _resultMessage

    private val _status = MutableLiveData<Status>()
    val status :LiveData<Status>
        get() = _status

    private val _reportLink = MutableLiveData<String>()
    val reportLink : LiveData<String>
        get() = _reportLink

    var isUpdate = false

    init {
        getResults()
    }

    private fun getResults() {
        _status.value = Status.LOADING
        viewModelScope.launch {
            try {
                _result.value = resultRepository.getResult().data?.get(0)
                downloadLink = _result.value?.reportLink
                _status.value = Status.DONE

                if (result.value?.isShowIncv!!.equalIgnoreCase("False"))
                    isUpdate = true

            } catch (e: Exception) {
                _status.value = Status.ERROR
            }
        }
    }

    fun onCheckedChanged(checked: Boolean) {
        Log.d("rakiv", "$checked")
        viewModelScope.launch {
            try {
                if (isUpdate) {
                    val result = resultRepository.updateResult(
                            when (checked) {
                                true -> "i"
                                false -> "d"
                            }
                    )
                    _resultMessage.value = result.message
                    Log.d("rakib", "$checked ${resultMessage.value}")
                } else
                    isUpdate = true
            } catch (e: Exception) {
            }
        }
    }
}