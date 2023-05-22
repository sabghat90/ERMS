package com.kust.ermsmanager.utils

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import com.google.android.material.button.MaterialButton
import com.kust.ermsmanager.R

class NetworkChangeListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!(CheckInternetConnection().isConnectedToInternet(context!!))) {
            val builder = AlertDialog.Builder(context)
            val dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null)
            builder.setView(dialog)

            val btnRetry = dialog.findViewById<Button>(R.id.btnRetry)

            // show dialog
            val alertDialog = builder.create()
            alertDialog.show()
            alertDialog.setCancelable(false)

            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.window?.setGravity(Gravity.CENTER)

            btnRetry.setOnClickListener {
                if (CheckInternetConnection().isConnectedToInternet(context)) {
                    alertDialog.dismiss()
                    onReceive(context, intent)
                }
            }

        }
    }
}