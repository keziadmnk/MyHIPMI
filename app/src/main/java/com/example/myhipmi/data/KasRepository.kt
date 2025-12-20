package com.example.myhipmi.data

import com.example.myhipmi.data.remote.response.KasDetailResponse
import com.example.myhipmi.data.remote.response.KasResponse
import com.example.myhipmi.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class KasRepository(private val apiService: ApiService) {

    fun getKas(userId: Int? = null): Flow<Result<KasResponse>> = flow {
        try {
            val response = apiService.getKas(userId)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getKasDetail(id: Int): Flow<Result<KasDetailResponse>> = flow {
        try {
            val response = apiService.getKasDetail(id)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun createKas(
        userId: Int,
        deskripsi: String,
        nominal: Double,
        file: File?
    ): Flow<Result<KasDetailResponse>> = flow {
        try {
            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = deskripsi.toRequestBody("text/plain".toMediaTypeOrNull())
            val nominalBody = nominal.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val filePart = if (file != null) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            } else {
                null
            }

            val response = apiService.createKas(userIdBody, deskripsiBody, nominalBody, filePart)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun updateKas(
        id: Int,
        deskripsi: String?,
        nominal: Double?,
        status: String?,
        file: File?
    ): Flow<Result<KasDetailResponse>> = flow {
        try {
            val deskripsiBody = deskripsi?.toRequestBody("text/plain".toMediaTypeOrNull())
            val nominalBody = nominal?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = status?.toRequestBody("text/plain".toMediaTypeOrNull())

            val filePart = if (file != null) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            } else {
                null
            }

            val response = apiService.updateKas(id, deskripsiBody, nominalBody, statusBody, filePart)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun deleteKas(id: Int): Flow<Result<KasResponse>> = flow {
        try {
            val response = apiService.deleteKas(id)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
