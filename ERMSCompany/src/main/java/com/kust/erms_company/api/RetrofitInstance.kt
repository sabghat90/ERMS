package com.kust.erms_company.api

import com.kust.erms_company.utils.ServerConstants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    /**
     The retrofit property is a lazily-initialized instance of the Retrofit client. It is configured with a base URL and a GsonConverterFactory for JSON serialization/deserialization.

     The api property is a lazily-initialized instance of the API interface. It is created by calling the create() method on the retrofit instance, passing the NotificationAPI::class.java as the argument.

     The companion object ensures that the retrofit and api instances are created only once and are shared across the class.
     */
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api: NotificationAPI by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}