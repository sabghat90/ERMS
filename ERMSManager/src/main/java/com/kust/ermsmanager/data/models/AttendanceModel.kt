package com.kust.ermsmanager.data.models

data class AttendanceModel(
    // attendance model for an employee
    val id: String,
    val employeeId: String,
    val date: String,
    val time: String,
    val status: String,
    val remarks: String
) {
    constructor() : this("", "", "", "", "", "")

    companion object {
        const val STATUS_PRESENT = "Present"
        const val STATUS_ABSENT = "Absent"
        const val STATUS_LATE = "Late"
        const val STATUS_LEAVE = "Leave"
        const val STATUS_HOLIDAY = "Holiday"
        const val STATUS_HALF_DAY = "Half_day"
        const val STATUS_OVERTIME = "overtime"
        const val STATUS_PAID_LEAVE = "paid_leave"
    }
}