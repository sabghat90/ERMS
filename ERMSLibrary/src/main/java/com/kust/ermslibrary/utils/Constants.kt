package com.kust.ermslibrary.utils

object FireStoreCollectionConstants {
    const val COMPANY = "company"
    const val EMPLOYEE = "employee"
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
}

object Role {
    const val COMPANY = "company"
    const val EMPLOYEE = "employee"
    const val MANAGER = "manager"
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

object ComplaintStatus {
    const val PENDING = "Pending"
    const val IN_PROGRESS = "In Progress"
    const val RESOLVED = "Resolved"
    const val REJECTED = "Rejected"
    const val CLOSED = "Closed"
}

object SharedPreferencesConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "manager_session"
    const val BIOMETRIC = "biometric"
}

object ServerConstants {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val CONTENT_TYPE = "application/json"
    const val TOPIC = "/topics/myTopic"
}