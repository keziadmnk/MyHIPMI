package com.example.myhipmi.data.remote.response

import com.google.gson.annotations.SerializedName

// Response untuk create absen piket
data class AbsenPiketResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AbsenPiketData?
)

// Data absen piket
data class AbsenPiketData(
    @SerializedName("id_absen_piket")
    val idAbsenPiket: Int,

    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("id_jadwal_piket")
    val idJadwalPiket: Int,

    @SerializedName("tanggal_absen")
    val tanggalAbsen: String,

    @SerializedName("jam_mulai")
    val jamMulai: String,

    @SerializedName("jam_selesai")
    val jamSelesai: String,

    @SerializedName("keterangan")
    val keterangan: String,

    @SerializedName("foto_bukti")
    val fotoBukti: String,

    @SerializedName("status_absen")
    val statusAbsen: String,

    @SerializedName("Pengurus")
    val pengurus: PengurusData? = null,

    @SerializedName("Jadwal")
    val jadwal: JadwalData? = null
)

data class PengurusData(
    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("nama_pengurus")
    val namaPengurus: String,

    @SerializedName("email_pengurus")
    val emailPengurus: String
)

data class JadwalData(
    @SerializedName("id_jadwal_piket")
    val idJadwalPiket: Int,

    @SerializedName("hari_piket")
    val hariPiket: String
)

// Request untuk membuat absen piket
data class CreateAbsenPiketRequest(
    @SerializedName("id_pengurus")
    val idPengurus: Int,

    @SerializedName("id_jadwal_piket")
    val idJadwalPiket: Int,

    @SerializedName("tanggal_absen")
    val tanggalAbsen: String,

    @SerializedName("jam_mulai")
    val jamMulai: String,

    @SerializedName("jam_selesai")
    val jamSelesai: String,

    @SerializedName("keterangan")
    val keterangan: String,

    @SerializedName("foto_bukti")
    val fotoBukti: String
)

// Response untuk list absen piket
data class AbsenPiketListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<AbsenPiketData>
)

