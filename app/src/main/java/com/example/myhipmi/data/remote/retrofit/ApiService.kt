package com.example.myhipmi.data.remote.retrofit

import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}

