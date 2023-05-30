package com.kust.ermslibrary.api

import com.kust.ermslibrary.api.FCMApiKey.SERVER_KEY
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.utils.ServerConstants.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @Headers("Authorization: $SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}