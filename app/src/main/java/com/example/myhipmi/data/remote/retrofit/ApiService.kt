package com.example.myhipmi.data.remote.retrofit

import com.example.myhipmi.data.remote.request.EventRequest
import com.example.myhipmi.data.remote.request.LoginRequest
import com.example.myhipmi.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>



    @Multipart
    @POST("events")
    suspend fun createEvent(
        @Part("id_pengurus") idPengurus: RequestBody,
        @Part("nama_event") namaEvent: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody,
        @Part("tempat") tempat: RequestBody,
        @Part("penyelenggara") penyelenggara: RequestBody,
        @Part("dresscode") dresscode: RequestBody?,
        @Part("contact_person") contactPerson: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part poster: MultipartBody.Part?
    ): Response<EventResponse>

    @GET("events")
    suspend fun getEvents(): Response<ReadEventResponse>

    @DELETE("events/{id}")
    suspend fun deleteEvent(
        @Path("id") id: Int
    ): Response<EventResponse>

    @GET("events/{id}")
    suspend fun getEventById(
        @Path("id") id: Int
    ): Response<EventDetailResponse>

    @Multipart
    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Part("id_pengurus") idPengurus: RequestBody,
        @Part("nama_event") namaEvent: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("waktu") waktu: RequestBody,
        @Part("tempat") tempat: RequestBody,
        @Part("penyelenggara") penyelenggara: RequestBody,
        @Part("dresscode") dresscode: RequestBody?,
        @Part("contact_person") contactPerson: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part poster: MultipartBody.Part?
    ): Response<EventResponse>

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

    // ========== NOTIFICATION ENDPOINTS ==========

    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationResponse>
}
