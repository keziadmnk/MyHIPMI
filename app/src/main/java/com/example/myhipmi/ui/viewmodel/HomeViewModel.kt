package com.example.myhipmi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhipmi.data.KasRepository
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TotalKasState {
    object Idle : TotalKasState()
    object Loading : TotalKasState()
    data class Success(val totalKas: Double) : TotalKasState()
    data class Error(val message: String) : TotalKasState()
}

class HomeViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()
    private val repository = KasRepository(apiService)

    private val _totalKasState = MutableStateFlow<TotalKasState>(TotalKasState.Idle)
    val totalKasState: StateFlow<TotalKasState> = _totalKasState.asStateFlow()

    fun fetchTotalKas() {
        viewModelScope.launch {
            _totalKasState.value = TotalKasState.Loading
            repository.getTotalKas().collect { result ->
                result.onSuccess { response ->
                    if (response.success) {
                         _totalKasState.value = TotalKasState.Success(response.totalKas)
                    } else {
                         _totalKasState.value = TotalKasState.Error(response.message ?: "Unknown error")
                    }
                }.onFailure { error ->
                    _totalKasState.value = TotalKasState.Error(error.message ?: "Network error")
                }
            }
        }
    }
}
