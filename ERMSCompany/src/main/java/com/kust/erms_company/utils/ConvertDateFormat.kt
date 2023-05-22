package com.kust.erms_company.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

class ConvertDateAndTimeFormat {
    fun formatDate(date: String): String {
        val parseDate = Timestamp(Date(date))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(parseDate.toDate())
    }

    fun formatTime(time: String): String {
        val parseTime = Timestamp(Date(time))
        val timeFormat = SimpleDateFormat("hh:mm a")
        return timeFormat.format(parseTime.toDate())
    }
}