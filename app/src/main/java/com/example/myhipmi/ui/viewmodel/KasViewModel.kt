package com.example.myhipmi.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhipmi.data.KasRepository
import com.example.myhipmi.data.remote.response.KasItem
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.utils.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class KasState {
    object Idle : KasState()
    object Loading : KasState()
    data class Success(val message: String) : KasState()
    data class Error(val error: String) : KasState()
}

class KasViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()
    private val repository = KasRepository(apiService)

    private val _kasList = MutableStateFlow<List<KasItem>>(emptyList())
    val kasList: StateFlow<List<KasItem>> = _kasList.asStateFlow()
    
    // Untuk detail edit
    private val _selectedKas = MutableStateFlow<KasItem?>(null)
    val selectedKas: StateFlow<KasItem?> = _selectedKas.asStateFlow()

    private val _kasState = MutableStateFlow<KasState>(KasState.Idle)
    val kasState: StateFlow<KasState> = _kasState.asStateFlow()

    fun getKas(userId: Int? = null) {
        viewModelScope.launch {
            _kasState.value = KasState.Loading
            repository.getKas(userId).collect { result ->
                result.onSuccess { response ->
                    _kasList.value = response.data ?: emptyList()
                    _kasState.value = KasState.Idle // Kembali ke idle agar tidak memicu toast berulang
                }.onFailure {
                    _kasState.value = KasState.Error(it.message ?: "Terjadi kesalahan")
                }
            }
        }
    }
    
    fun getKasDetail(id: Int) {
        viewModelScope.launch {
            _kasState.value = KasState.Loading
            repository.getKasDetail(id).collect { result ->
                result.onSuccess { response ->
                    _selectedKas.value = response.data
                    _kasState.value = KasState.Idle
                }.onFailure {
                    _kasState.value = KasState.Error(it.message ?: "Gagal mengambil detail")
                }
            }
        }
    }

    fun createKas(
        context: Context,
        userId: Int,
        deskripsi: String,
        nominal: Double,
        fileUri: Uri?
    ) {
        viewModelScope.launch {
            _kasState.value = KasState.Loading
            try {
                val file: File? = fileUri?.let { FileUtil.from(context, it) }
                repository.createKas(userId, deskripsi, nominal, file).collect { result ->
                    result.onSuccess {
                        _kasState.value = KasState.Success(it.message ?: "Pembayaran berhasil dikirim")
                        getKas(userId)
                    }.onFailure {
                        _kasState.value = KasState.Error(it.message ?: "Gagal mengirim data")
                    }
                }
            } catch (e: Exception) {
                _kasState.value = KasState.Error(e.message ?: "Error memproses file")
            }
        }
    }
    
    fun updateKas(
        context: Context,
        id: Int,
        userId: Int, // Untuk refresh list
        deskripsi: String?,
        nominal: Double?,
        status: String?,
        fileUri: Uri?
    ) {
        viewModelScope.launch {
            _kasState.value = KasState.Loading
            try {
                val file: File? = fileUri?.let { FileUtil.from(context, it) }
                repository.updateKas(id, deskripsi, nominal, status, file).collect { result ->
                    result.onSuccess {
                        _kasState.value = KasState.Success(it.message ?: "Data berhasil diupdate")
                        getKas(userId) 
                    }.onFailure {
                        _kasState.value = KasState.Error(it.message ?: "Gagal update data")
                    }
                }
            } catch (e: Exception) {
                _kasState.value = KasState.Error(e.message ?: "Error memproses file")
            }
        }
    }
    
    fun deleteKas(id: Int, userId: Int) {
        viewModelScope.launch {
            _kasState.value = KasState.Loading
            repository.deleteKas(id).collect { result ->
                result.onSuccess {
                    _kasState.value = KasState.Success(it.message ?: "Data berhasil dihapus")
                    getKas(userId)
                }.onFailure {
                    _kasState.value = KasState.Error(it.message ?: "Gagal menghapus data")
                }
            }
        }
    }

    fun resetState() {
        _kasState.value = KasState.Idle
    }
}
