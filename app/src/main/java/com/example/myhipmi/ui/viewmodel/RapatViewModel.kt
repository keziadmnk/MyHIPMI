package com.example.myhipmi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myhipmi.data.remote.response.*
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject

class RapatViewModel : ViewModel() {

    private val _apiService = ApiConfig.getApiService()
    val apiService = _apiService // Expose apiService untuk diakses dari screen

    // State untuk list agenda rapat
    private val _rapatBerlangsung = MutableStateFlow<List<AgendaRapatData>>(emptyList())
    val rapatBerlangsung: StateFlow<List<AgendaRapatData>> = _rapatBerlangsung

    private val _rapatSelesai = MutableStateFlow<List<AgendaRapatData>>(emptyList())
    val rapatSelesai: StateFlow<List<AgendaRapatData>> = _rapatSelesai

    // State untuk loading dan error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        loadAllAgenda()
    }

    // Load semua agenda dari backend
    fun loadAllAgenda() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.getAllAgenda()
                if (response.isSuccessful && response.body()?.success == true) {
                    val allData = response.body()?.data ?: emptyList()

                    // Pisahkan berdasarkan isDone dan waktu yang sudah lewat
                    val currentTime = Calendar.getInstance()

                    val (selesaiList, berlangsung) = allData.partition { agenda ->
                        // Agenda masuk ke "Selesai" jika:
                        // 1. isDone = true (sudah absen), ATAU
                        // 2. Waktu rapat sudah lewat (sudah melewati endAt)

                        // Cek 1: isDone = true (backend sudah set ini kalau ada absensi)
                        if (agenda.isDone) {
                            android.util.Log.d("RapatViewModel", "Agenda ${agenda.idAgenda} isDone = true")
                            return@partition true
                        }

                        // Cek 2: Waktu rapat sudah lewat
                        try {
                            val endParts = agenda.endTimeDisplay.replace(" WIB", "").split(":")
                            val endHour = endParts[0].toInt()
                            val endMinute = endParts[1].toInt()

                            // Parse tanggal agenda
                            val agendaDateParts = agenda.dateDisplay.split(" ")
                            val day = agendaDateParts[0].toInt()
                            val monthName = agendaDateParts[1]
                            val year = agendaDateParts[2].toInt()

                            // Konversi nama bulan Indonesia ke angka
                            val monthNumber = when (monthName.lowercase()) {
                                "januari" -> Calendar.JANUARY
                                "februari" -> Calendar.FEBRUARY
                                "maret" -> Calendar.MARCH
                                "april" -> Calendar.APRIL
                                "mei" -> Calendar.MAY
                                "juni" -> Calendar.JUNE
                                "juli" -> Calendar.JULY
                                "agustus" -> Calendar.AUGUST
                                "september" -> Calendar.SEPTEMBER
                                "oktober" -> Calendar.OCTOBER
                                "november" -> Calendar.NOVEMBER
                                "desember" -> Calendar.DECEMBER
                                else -> currentTime.get(Calendar.MONTH)
                            }

                            val agendaEndTime = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, monthNumber)
                                set(Calendar.DAY_OF_MONTH, day)
                                set(Calendar.HOUR_OF_DAY, endHour)
                                set(Calendar.MINUTE, endMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }

                            // Rapat sudah selesai jika waktu sekarang > waktu selesai rapat
                            val isTimeExpired = currentTime.after(agendaEndTime)
                            if (isTimeExpired) {
                                android.util.Log.d("RapatViewModel", "Agenda ${agenda.idAgenda} waktu sudah lewat")
                            }
                            return@partition isTimeExpired
                        } catch (e: Exception) {
                            android.util.Log.e("RapatViewModel", "Error parsing agenda time: ${e.message}")
                            return@partition false
                        }
                    }

                    _rapatSelesai.value = selesaiList
                    _rapatBerlangsung.value = berlangsung

                    android.util.Log.d("RapatViewModel", "Agenda berlangsung: ${berlangsung.size}, selesai: ${selesaiList.size}")

                    // Debug: tampilkan isDone untuk setiap agenda
                    allData.forEach { agenda ->
                        android.util.Log.d("RapatViewModel", "Agenda ${agenda.idAgenda} - isDone: ${agenda.isDone}")
                    }
                } else {
                    _errorMessage.value = "Gagal memuat data rapat"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                android.util.Log.e("RapatViewModel", "Error loading agenda: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Buat agenda rapat baru
    fun createAgenda(
        idPengurus: Int,
        title: String,
        creatorId: Int,
        creatorName: String,
        dateDisplay: String,
        dateSelectedMillis: Long,
        startTimeDisplay: String,
        endTimeDisplay: String,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Format tanggal untuk backend (YYYY-MM-DD)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.format(Date(dateSelectedMillis))

                // Parse waktu dari display format (HH:mm WIB)
                val startParts = startTimeDisplay.replace(" WIB", "").trim().split(":")
                val endParts = endTimeDisplay.replace(" WIB", "").trim().split(":")

                // Buat Calendar untuk tanggal yang dipilih
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateSelectedMillis

                // Set jam mulai (WIB) - MySQL akan menyimpan sebagai local time
                calendar.set(Calendar.HOUR_OF_DAY, startParts[0].toInt())
                calendar.set(Calendar.MINUTE, startParts[1].toInt())
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Format untuk MySQL DATETIME (tanpa timezone conversion)
                val datetimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                datetimeFormat.timeZone = TimeZone.getTimeZone("UTC")

                // Kurangi 7 jam untuk konversi WIB ke UTC
                calendar.add(Calendar.HOUR_OF_DAY, -7)
                val startAt = datetimeFormat.format(calendar.time)

                // Reset calendar untuk end time
                calendar.timeInMillis = dateSelectedMillis
                calendar.set(Calendar.HOUR_OF_DAY, endParts[0].toInt())
                calendar.set(Calendar.MINUTE, endParts[1].toInt())
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.HOUR_OF_DAY, -7)
                val endAt = datetimeFormat.format(calendar.time)

                val request = CreateRapatRequest(
                    idPengurus = idPengurus,
                    title = title,
                    creatorId = creatorId,
                    creatorName = creatorName,
                    date = date,
                    startAt = startAt,
                    endAt = endAt,
                    dateDisplay = dateDisplay,
                    startTimeDisplay = startTimeDisplay,
                    endTimeDisplay = endTimeDisplay,
                    location = location,
                    description = description
                )

                // Log untuk debugging
                android.util.Log.d("RapatViewModel", "Request: $request")

                val response = apiService.createAgenda(request)

                // Log response
                android.util.Log.d("RapatViewModel", "Response code: ${response.code()}")
                android.util.Log.d("RapatViewModel", "Response body: ${response.body()}")
                android.util.Log.d("RapatViewModel", "Response error: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = response.body()?.message ?: "Agenda berhasil dibuat"
                    loadAllAgenda() // Reload data
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gagal membuat agenda rapat: ${errorBody ?: "Unknown error"}"
                    android.util.Log.e("RapatViewModel", "Error creating agenda: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                android.util.Log.e("RapatViewModel", "Exception creating agenda", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update agenda rapat
    fun updateAgenda(
        idAgenda: Int,
        title: String,
        dateDisplay: String,
        dateSelectedMillis: Long,
        startTimeDisplay: String,
        endTimeDisplay: String,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Format tanggal untuk backend (YYYY-MM-DD)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.format(Date(dateSelectedMillis))

                // Parse waktu dari display format (HH:mm WIB)
                val startParts = startTimeDisplay.replace(" WIB", "").trim().split(":")
                val endParts = endTimeDisplay.replace(" WIB", "").trim().split(":")

                // Buat Calendar untuk tanggal yang dipilih
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateSelectedMillis

                // Set jam mulai (WIB) - MySQL akan menyimpan sebagai local time
                calendar.set(Calendar.HOUR_OF_DAY, startParts[0].toInt())
                calendar.set(Calendar.MINUTE, startParts[1].toInt())
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Format untuk MySQL DATETIME (tanpa timezone conversion)
                val datetimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                datetimeFormat.timeZone = TimeZone.getTimeZone("UTC")

                // Kurangi 7 jam untuk konversi WIB ke UTC
                calendar.add(Calendar.HOUR_OF_DAY, -7)
                val startAt = datetimeFormat.format(calendar.time)

                // Reset calendar untuk end time
                calendar.timeInMillis = dateSelectedMillis
                calendar.set(Calendar.HOUR_OF_DAY, endParts[0].toInt())
                calendar.set(Calendar.MINUTE, endParts[1].toInt())
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.add(Calendar.HOUR_OF_DAY, -7)
                val endAt = datetimeFormat.format(calendar.time)

                // Ambil data agenda yang lama untuk creatorId dan creatorName
                val existingAgenda = apiService.getAgendaById(idAgenda)
                if (!existingAgenda.isSuccessful || existingAgenda.body()?.success != true) {
                    _errorMessage.value = "Gagal mengambil data agenda"
                    return@launch
                }

                val oldData = existingAgenda.body()?.data!!

                val request = CreateRapatRequest(
                    idPengurus = oldData.idPengurus,
                    title = title,
                    creatorId = oldData.creatorId,
                    creatorName = oldData.creatorName,
                    date = date,
                    startAt = startAt,
                    endAt = endAt,
                    dateDisplay = dateDisplay,
                    startTimeDisplay = startTimeDisplay,
                    endTimeDisplay = endTimeDisplay,
                    location = location,
                    description = description
                )

                android.util.Log.d("RapatViewModel", "Update Request: $request")

                val response = apiService.updateAgenda(idAgenda, request)

                android.util.Log.d("RapatViewModel", "Update Response code: ${response.code()}")
                android.util.Log.d("RapatViewModel", "Update Response body: ${response.body()}")

                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = response.body()?.message ?: "Agenda berhasil diupdate"
                    loadAllAgenda() // Reload data
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gagal mengupdate agenda rapat: ${errorBody ?: "Unknown error"}"
                    android.util.Log.e("RapatViewModel", "Error updating agenda: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                android.util.Log.e("RapatViewModel", "Exception updating agenda", e)
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update agenda (mark as done)
    fun moveToSelesai(idAgenda: Int) {
        viewModelScope.launch {
            try {
                // Cari agenda yang akan diupdate
                val agenda = _rapatBerlangsung.value.find { it.idAgenda == idAgenda }
                if (agenda != null) {
                    val request = CreateRapatRequest(
                        idPengurus = agenda.idPengurus,
                        title = agenda.title,
                        creatorId = agenda.creatorId,
                        creatorName = agenda.creatorName,
                        date = agenda.date,
                        startAt = agenda.startAt,
                        endAt = agenda.endAt,
                        dateDisplay = agenda.dateDisplay,
                        startTimeDisplay = agenda.startTimeDisplay,
                        endTimeDisplay = agenda.endTimeDisplay,
                        location = agenda.location,
                        description = agenda.description
                    )

                    // Update isDone menjadi true (ini akan dilakukan di backend saat absen)
                    // Untuk sementara kita langsung reload data
                    loadAllAgenda()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    // Hapus agenda
    fun deleteAgenda(idAgenda: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteAgenda(idAgenda)
                if (response.isSuccessful && response.body()?.success == true) {
                    _successMessage.value = "Agenda berhasil dihapus"
                    loadAllAgenda() // Reload data
                } else {
                    _errorMessage.value = "Gagal menghapus agenda"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Buat absen rapat
    fun createAbsen(
        idAgenda: Int,
        idPengurus: Int,
        photoUrl: String?,
        status: String = "present",
        onSuccess: (AbsenRapatResponse?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CreateAbsenRequest(
                    idAgenda = idAgenda,
                    idPengurus = idPengurus,
                    photobuktiUrl = photoUrl,
                    status = status
                )

                android.util.Log.d("RapatViewModel", "Creating absen with status: $status for agenda: $idAgenda")

                val response = apiService.createAbsen(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val responseBody = response.body()
                    _successMessage.value = responseBody?.message ?: "Absen berhasil dicatat"
                    
                    android.util.Log.d("RapatViewModel", "Absen berhasil dengan timestamp: ${responseBody?.data?.timestamp}")
                    
                    // Single refresh - data akan di-refresh lagi oleh screen saat kembali
                    android.util.Log.d("RapatViewModel", "Refreshing data after successful absen...")
                    loadAllAgenda()

                    // Panggil onSuccess dengan response data
                    onSuccess(responseBody)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Gagal mencatat absen: ${errorBody ?: "Unknown error"}"
                    android.util.Log.e("RapatViewModel", "Error creating absen: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                android.util.Log.e("RapatViewModel", "Exception in createAbsen: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear messages
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
