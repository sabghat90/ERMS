package com.kust.ermsmanager.utils

object FireStoreCollectionConstants {
    const val COMPANY = "company"
    const val EMPLOYEE = "employee"
    const val USERS = "users"
    const val TASKS = "tasks"
    const val EVENTS = "events"
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
    const val PENDING = "pending"
    const val IN_PROGRESS = "in_progress"
    const val COMPLETED = "completed"
    const val SUBMITTED = "submitted"
    const val APPROVED = "approved"
    const val REJECTED = "rejected"
    const val RESUBMITTED = "resubmitted"
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