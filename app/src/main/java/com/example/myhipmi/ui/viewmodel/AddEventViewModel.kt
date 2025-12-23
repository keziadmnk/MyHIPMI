package com.example.myhipmi.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.utils.FileUtil
import com.example.myhipmi.utils.toPlainRequestBody
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddEventViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    fun submitEvent(
        context: Context,
        idPengurus: Int,
        namaEvent: String,
        tanggal: String,
        waktu: String,
        tempat: String,
        penyelenggara: String,
        dresscode: String?,
        contactPerson: String?,
        deskripsi: String?,
        fileUri: Uri?,
        onResult: (success: Boolean, message: String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val posterPart: MultipartBody.Part? = fileUri?.let { uri ->
                    val file: File = FileUtil.from(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaType())
                    MultipartBody.Part.createFormData("poster", file.name, requestFile)
                }

                val response = apiService.createEvent(
                    idPengurus.toString().toPlainRequestBody(),
                    namaEvent.toPlainRequestBody(),
                    tanggal.toPlainRequestBody(),
                    waktu.toPlainRequestBody(),
                    tempat.toPlainRequestBody(),
                    penyelenggara.toPlainRequestBody(),
                    dresscode?.toPlainRequestBody(),
                    contactPerson?.toPlainRequestBody(),
                    deskripsi?.toPlainRequestBody(),
                    posterPart
                )

                if (response.isSuccessful) {
                    onResult(true, "Event berhasil ditambahkan")
                } else {
                    val body = response.errorBody()?.string()
                    onResult(false, body ?: "Gagal: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("AddEventVM", "Error upload", e)
                onResult(false, e.localizedMessage ?: "Error tidak diketahui")
            }
        }
    }
}
