package com.kust.erms_company.api

import com.kust.erms_company.data.model.PushNotification
import com.kust.erms_company.utils.ServerConstants.CONTENT_TYPE
import com.kust.erms_company.utils.ServerConstants.SERVER_KEY
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