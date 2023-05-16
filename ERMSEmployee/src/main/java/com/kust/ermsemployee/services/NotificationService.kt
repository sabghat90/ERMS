package com.kust.ermsemployee.services

import android.util.Log
import com.kust.ermsemployee.data.model.PushNotification
import com.kust.ermsemployee.api.RetrofitInstance
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
//        val serverKey = ServerConstants.SERVER_KEY
//        val fcmToken = notification.targetEmployeeFCMToken
//        val notificationTitle = notification.title
//        val notificationBody = notification.body
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val url = URL("https://fcm.googleapis.com/fcm/send")
//            val conn = url.openConnection() as HttpURLConnection
//            conn.requestMethod = "POST"
//            conn.setRequestProperty("Content-Type", "application/json")
//            conn.setRequestProperty("Authorization", "key=$serverKey")
//            conn.doOutput = true
//
//            val jsonPayload = """
//            {
//                "to": "$fcmToken",
//                "notification": {
//                    "title": "$notificationTitle",
//                    "body": "$notificationBody"
//                }
//            }
//        """.trimIndent()
//
//            val input = jsonPayload.toByteArray(StandardCharsets.UTF_8)
//            conn.outputStream.use { outputStream: OutputStream ->
//                outputStream.write(input, 0, input.size)
//            }
//
//            val responseCode = conn.responseCode
//            println("FCM Legacy API Response Code: $responseCode")
//            Log.d("FCM", responseCode.toString())
//
//        }
//    }


//        try {
//            val queue = Volley.newRequestQueue(notification.context)
//            val url = "https://fcm.googleapis.com/fcm/send"
//            val data = JSONObject()
//            data.put("title", notification.title)
//            data.put("body", notification.body)
//            val notificationData = JSONObject()
//            notificationData.put("data", data)
//            notificationData.put("to", notification.targetEmployeeFCMToken)
//            val request = JsonObjectRequest(url, notificationData,
//                { response ->
//                    println("Response: %s".format(response.toString()))
//                },
//                { error ->
//                    println("Error: %s".format(error.toString()))
//                }
//            )
//            // create headers through HashMap
//            val headers = HashMap<String, String>()
//            headers["Authorization"] = ServerKey.KEY
//            headers["Content-Type"] = "application/json"
//            // add headers to request
//            queue.add(request)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
}