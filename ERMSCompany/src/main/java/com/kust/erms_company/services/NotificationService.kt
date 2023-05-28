package com.kust.erms_company.services

import android.util.Log
import com.kust.erms_company.api.RetrofitInstance
import com.kust.erms_company.data.model.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationService {
    fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("FCM", "Response: ${response.body()}")
                } else {
                    Log.e("FCM", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("FCM", e.toString())
            }
        }
}