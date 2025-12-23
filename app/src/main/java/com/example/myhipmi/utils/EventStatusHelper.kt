package com.example.myhipmi.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class EventStatus {
    UPCOMING,  // Belum dimulai
    ONGOING,   // Sedang berlangsung
    PAST       // Sudah selesai
}

object EventStatusHelper {
    fun getEventStatus(eventDate: String, eventTime: String): EventStatus {
        try {
            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val eventDateTime = dateTimeFormat.parse("$eventDate $eventTime")
            
            if (eventDateTime == null) return EventStatus.PAST
            
            val eventCalendar = Calendar.getInstance().apply {
                time = eventDateTime
            }
            val currentCalendar = Calendar.getInstance()
            val eventDay = eventCalendar.get(Calendar.DAY_OF_YEAR)
            val eventYear = eventCalendar.get(Calendar.YEAR)
            val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
            val currentYear = currentCalendar.get(Calendar.YEAR)
            
            return when {
                eventYear == currentYear && eventDay == currentDay -> EventStatus.ONGOING
                eventDateTime.before(currentCalendar.time) -> EventStatus.PAST
                else -> EventStatus.UPCOMING
            }
        } catch (e: Exception) {
            return EventStatus.PAST
        }
    }
    fun getStatusText(status: EventStatus): String {
        return when (status) {
            EventStatus.UPCOMING -> "Upcoming"
            EventStatus.ONGOING -> "Ongoing"
            EventStatus.PAST -> "Past"
        }
    }
    fun getStatusColor(status: EventStatus): Long {
        return when (status) {
            EventStatus.UPCOMING -> 0xFF3B82F6  // Biru
            EventStatus.ONGOING -> 0xFF10B981   // Hijau
            EventStatus.PAST -> 0xFF6B7280      // Abu-abu
        }
    }
}
