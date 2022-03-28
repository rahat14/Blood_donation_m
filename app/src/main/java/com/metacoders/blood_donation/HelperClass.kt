package com.metacoders.blood_donation

import android.app.ProgressDialog
import android.content.Context

class HelperClass {
    companion object {

        fun createProgressDialog(context: Context, msg: String): ProgressDialog {
            val progress: ProgressDialog = ProgressDialog(
                context
            )
            progress.setMessage(msg)
            progress.setCancelable(false)
            return progress
        }

    }
}