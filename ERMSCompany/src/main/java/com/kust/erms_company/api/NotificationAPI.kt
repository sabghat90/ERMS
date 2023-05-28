package com.kust.erms_company.api

import com.kust.erms_company.api.FCMApiKey.SERVER_KEY
import com.kust.erms_company.data.model.PushNotification
import com.kust.erms_company.utils.ServerConstants.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    /**
    @Headers("Authorization: $SERVER_KEY", "Content-Type: $CONTENT_TYPE"): Specifies the headers to be included in the request. The values of SERVER_KEY and CONTENT_TYPE are interpolated into the header values.

    @POST("fcm/send"): Indicates that the function corresponds to an HTTP POST request to the "fcm/send" endpoint.

    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>: Defines the function for sending a notification. It takes a PushNotification object as the request body and suspends until the response is received. The response type is Response<ResponseBody>.
     */
    @Headers("Authorization: $SERVER_KEY", "Content-Type: $CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}