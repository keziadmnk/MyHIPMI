package com.example.myhipmi.data.remote.retrofit


import com.example.myhipmi.data.remote.request.EventRequest
import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.response.EventResponse
import com.example.myhipmi.data.remote.response.LoginResponse
import com.example.myhipmi.data.remote.response.ReadEventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("events") // Sesuaikan dengan route di backend
    suspend fun createEvent(
        @Body request: EventRequest
    ): Response<EventResponse>

    @GET("events") // <-- TAMBAHKAN INI
    suspend fun getEvents(): Response<ReadEventResponse>
}

