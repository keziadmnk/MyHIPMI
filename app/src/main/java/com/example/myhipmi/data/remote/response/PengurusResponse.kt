package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

data class PengurusResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("data")
    val data: PengurusDetailData?
)

data class PengurusDetailData(
    @SerializedName("id_pengurus")
    val idPengurus: Int,
    
    @SerializedName("nama_pengurus")
    val namaPengurus: String,
    
    @SerializedName("email_pengurus")
    val emailPengurus: String,
    
    @SerializedName("jabatan")
    val jabatan: String,
    
    @SerializedName("nomor_hp")
    val nomorHp: String?,
    
    @SerializedName("alamat")
    val alamat: String?,
    
    @SerializedName("id_jadwal_piket")
    val idJadwalPiket: Int?,
    
    @SerializedName("JadwalPiket")
    val jadwalPiket: JadwalPiketData?
)

data class JadwalPiketData(
    @SerializedName("id_jadwal_piket")
    val idJadwalPiket: Int,
    
    @SerializedName("hari_piket")
    val hariPiket: String
)

