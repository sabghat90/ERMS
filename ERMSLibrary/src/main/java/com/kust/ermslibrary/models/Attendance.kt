package com.kust.ermslibrary.models

data class Attendance(
    // attendance model for an employee
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "",
    val extraBonus: Double = 0.00,
    val advanceOrLoan: Double = 0.00,
    val year: String = "",
    val month: String = "",
    val day: String = ""
) {

    companion object {
        const val STATUS_PRESENT = "Present"
        const val STATUS_ABSENT = "Absent"
        const val STATUS_LEAVE = "Leave"
        const val STATUS_HOLIDAY = "Holiday"
        const val STATUS_HALF_DAY = "Half_day"
        const val STATUS_OVERTIME = "Overtime"
    }
}