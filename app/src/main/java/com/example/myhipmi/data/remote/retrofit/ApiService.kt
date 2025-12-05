package com.example.myhipmi.data.remote.retrofit

import com.example.myhipmi.data.remote.request.EventRequest
import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("events")
    suspend fun createEvent(
        @Body request: EventRequest
    ): Response<EventResponse>

    @GET("events")
    suspend fun getEvents(): Response<ReadEventResponse>

    // ========== AGENDA RAPAT ENDPOINTS ==========

    @GET("agenda")
    suspend fun getAllAgenda(): Response<RapatListResponse>

    @GET("agenda/{id}")
    suspend fun getAgendaById(
        @Path("id") id: Int
    ): Response<RapatDetailResponse>

    @POST("agenda")
    suspend fun createAgenda(
        @Body request: CreateRapatRequest
    ): Response<RapatActionResponse>

    @PUT("agenda/{id}")
    suspend fun updateAgenda(
        @Path("id") id: Int,
        @Body request: CreateRapatRequest
    ): Response<RapatActionResponse>

    @DELETE("agenda/{id}")
    suspend fun deleteAgenda(
        @Path("id") id: Int
    ): Response<RapatActionResponse>

    // ========== ABSEN RAPAT ENDPOINTS ==========

    @POST("absen")
    suspend fun createAbsen(
        @Body request: CreateAbsenRequest
    ): Response<AbsenRapatResponse>

    @GET("absen/agenda/{id_agenda}")
    suspend fun getAbsenByAgenda(
        @Path("id_agenda") idAgenda: Int
    ): Response<AbsenListResponse>
}
