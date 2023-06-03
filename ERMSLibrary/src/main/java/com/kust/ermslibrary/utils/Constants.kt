package com.kust.ermslibrary.utils

object FireStoreCollectionConstants {
    const val USERS = "users"
    const val TASKS = "tasks"
    const val EVENTS = "events"
    const val COMPLAINTS = "complaints"
    const val COMPLAINT_HISTORY = "complaint_history"
}

object FirebaseStorageConstants {
    const val COMPANY_PROFILE = "company_profile"
    const val EMPLOYEE_PROFILE = "employee_profile"
}

object FirebaseRealtimeDatabaseConstants {
    const val ATTENDANCE = "attendance"
    const val CHAT = "chats"
}

object Role {
    const val COMPANY = "Company"
    const val EMPLOYEE = "Employee"
    const val MANAGER = "Manager"
}

object TaskStatus {
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val COMPLETED = "Completed"
    const val SUBMITTED = "Submitted"
    const val APPROVED = "Approved"
    const val REJECTED = "Rejected"
    const val RESUBMITTED = "Resubmitted"
}

object TaskPoints {
    const val PENDING = 0
    const val IN_PROGRESS = 0
    const val COMPLETED = 0
    const val SUBMITTED = 10.00
    const val APPROVED = 10.00
    const val REJECTED = 0
    const val RESUBMITTED = 10.00
}

object ComplaintStatus {
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val RESOLVED = "Resolved"
    const val REJECTED = "Rejected"
    const val CLOSED = "Closed"
}

object AttendancePoints {
    const val PRESENT = 10.00
    const val ABSENT = 0.00
    const val LATE = 5.00
    const val HALF_DAY = 5.00
    const val LEAVE = 10.00
    const val HOLIDAY = 0.00
    const val OVERTIME = 15.00
}

object SharedPreferencesConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "manager_session"
    const val BIOMETRIC = "biometric"
}

object ServerConstants {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val CONTENT_TYPE = "application/json"
}