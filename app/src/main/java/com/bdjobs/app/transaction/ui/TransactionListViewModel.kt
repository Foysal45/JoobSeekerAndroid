package com.bdjobs.app.transaction.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.transaction.data.model.TransactionData
import com.bdjobs.app.transaction.data.model.TransactionList
import kotlinx.coroutines.launch

class TransactionListViewModel(private val repository: TransactionRepository) : ViewModel() {


    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _transactionListData = MutableLiveData<List<TransactionData?>>()
    val transactionListData: LiveData<List<TransactionData?>> = _transactionListData

    private val _totalTransaction = MutableLiveData<String?>()
    val totalTransaction: LiveData<String?> = _totalTransaction

    init {
      /*  getTransactionList()*/
    }

    fun getTransactionList(startDate :String,endDate :String,type:String) {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                    val response = repository.getTransactionList(startDate,endDate ,type)
                    when (response.statuscode) {
                        "0" -> {
                               _transactionListData.value = response.data
                             _totalTransaction.value = response.data!!.size.toString()
                            _dataLoading.value = false
                        }
                        "3" -> {
                            _transactionListData.value = response.data
                            _totalTransaction.value = "0"
                            _dataLoading.value = false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }


        }
    }




}