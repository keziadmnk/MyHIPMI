package com.example.myhipmi.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // StateFlow untuk memantau perubahan data secara reaktif
    private val _userId = MutableStateFlow<Int?>(getIdPengurus())
    val userId: Flow<Int?> = _userId.asStateFlow()

    private val _userName = MutableStateFlow<String?>(getNamaPengurus())
    val userName: Flow<String?> = _userName.asStateFlow()
    
    private val _userRole = MutableStateFlow<String?>(getJabatan())
    val userRole: Flow<String?> = _userRole.asStateFlow()

    companion object {
        private const val PREFS_NAME = "MyHIPMI_Prefs"
        private const val KEY_ID_PENGURUS = "id_pengurus"
        private const val KEY_NAMA_PENGURUS = "nama_pengurus"
        private const val KEY_EMAIL_PENGURUS = "email_pengurus"
        private const val KEY_JABATAN = "jabatan"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_HAS_UNREAD_NOTIFICATIONS = "has_unread_notifications"
    }

    fun saveUserSession(
        idPengurus: Int,
        namaPengurus: String,
        emailPengurus: String,
        jabatan: String?,
        token: String
    ) {
        val editor = prefs.edit()
        editor.putInt(KEY_ID_PENGURUS, idPengurus)
        editor.putString(KEY_NAMA_PENGURUS, namaPengurus)
        editor.putString(KEY_EMAIL_PENGURUS, emailPengurus)
        editor.putString(KEY_JABATAN, jabatan)
        editor.putString(KEY_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()

        // Update StateFlow
        _userId.value = idPengurus
        _userName.value = namaPengurus
        _userRole.value = jabatan
    }

    fun getIdPengurus(): Int? {
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.getInt(KEY_ID_PENGURUS, -1).takeIf { it != -1 }
        } else {
            null
        }
    }

    fun getNamaPengurus(): String? {
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.getString(KEY_NAMA_PENGURUS, null)
        } else {
            null
        }
    }

    fun getEmailPengurus(): String? {
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.getString(KEY_EMAIL_PENGURUS, null)
        } else {
            null
        }
    }

    fun getJabatan(): String? {
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.getString(KEY_JABATAN, null)
        } else {
            null
        }
    }

    fun getToken(): String? {
        return if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            prefs.getString(KEY_TOKEN, null)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        
        _userId.value = null
        _userName.value = null
        _userRole.value = null
    }
    
    // Fungsi untuk menyimpan timestamp absen per agenda
    fun saveAbsenTimestamp(idAgenda: Int, timestamp: String) {
        val editor = prefs.edit()
        editor.putString("absen_timestamp_$idAgenda", timestamp)
        editor.apply()
    }
    
    // Fungsi untuk mengambil timestamp absen per agenda
    fun getAbsenTimestamp(idAgenda: Int): String? {
        return prefs.getString("absen_timestamp_$idAgenda", null)
    }
    
    fun setHasUnreadNotifications(hasUnread: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(KEY_HAS_UNREAD_NOTIFICATIONS, hasUnread)
        editor.apply()
    }
    
    fun getHasUnreadNotifications(): Boolean {
        return prefs.getBoolean(KEY_HAS_UNREAD_NOTIFICATIONS, false)
    }
}
