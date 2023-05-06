package com.kust.erms_company.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kust.erms_company.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }
}