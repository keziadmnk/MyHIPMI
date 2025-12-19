package com.example.myhipmi.data.remote.request

import com.google.gson.annotations.SerializedName

data class UpdatePengurusRequest(
    @field:SerializedName("nomor_hp")
    val nomorHp: String?,

    @field:SerializedName("alamat")
    val alamat: String?
)
