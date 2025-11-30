package com.example.myhipmi.data.local

import android.content.Context
import android.content.SharedPreferences

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "MyHIPMI_Prefs"
        private const val KEY_ID_PENGURUS = "id_pengurus"
        private const val KEY_NAMA_PENGURUS = "nama_pengurus"
        private const val KEY_EMAIL_PENGURUS = "email_pengurus"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserSession(
        idPengurus: Int,
        namaPengurus: String,
        emailPengurus: String,
        token: String
    ) {
        val editor = prefs.edit()
        editor.putInt(KEY_ID_PENGURUS, idPengurus)
        editor.putString(KEY_NAMA_PENGURUS, namaPengurus)
        editor.putString(KEY_EMAIL_PENGURUS, emailPengurus)
        editor.putString(KEY_TOKEN, token)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
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
    }
}

