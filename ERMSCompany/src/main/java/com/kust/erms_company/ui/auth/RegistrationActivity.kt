package com.kust.erms_company.ui.auth

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kust.erms_company.R
import com.kust.ermslibrary.utils.NetworkChangeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private val networkChangeListener = NetworkChangeListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    override fun onStart() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, intentFilter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }
}