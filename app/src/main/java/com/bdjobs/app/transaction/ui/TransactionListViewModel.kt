package com.bdjobs.app.transaction.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.transaction.data.TransactionRepository
import com.bdjobs.app.transaction.data.model.Transaction
import kotlinx.coroutines.launch

class TransactionListViewModel(private val repository: TransactionRepository) : ViewModel() {


    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _transactionListData = MutableLiveData<List<Transaction.TransactionData?>>()
    val transactionListData: LiveData<List<Transaction.TransactionData?>> = _transactionListData

    private val _commonData = MutableLiveData<Transaction.Common?>()
    val commonData: LiveData<Transaction.Common?> = _commonData

    init {
        //getVideoInterviewList()
    }

    fun getTransactionList() {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getTransactionList()
                _transactionListData.value = response.data
                _commonData.value = response.common
                _dataLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




}