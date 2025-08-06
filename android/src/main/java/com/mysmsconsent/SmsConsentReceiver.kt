package com.jack

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes

class SmsConsentReceiver(
    private val activity: Activity,
    private val module: SmsConsentModule
) : BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 2025
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val extras: Bundle? = intent?.extras
        if (extras == null) {
            module.onError(SmsConsentException(SmsConsentErrorType.BROADCAST_FAILURE, "Intent extras are null"))
            return
        }

        val status = extras.get(SmsRetriever.EXTRA_STATUS) as? com.google.android.gms.common.api.Status
        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)

        when (status?.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                try {
                    activity.startActivityForResult(consentIntent, REQUEST_CODE)
                } catch (e: Exception) {
                    module.onError(SmsConsentException(SmsConsentErrorType.BROADCAST_FAILURE, "Failed to launch consent intent"))
                }
            }
            CommonStatusCodes.TIMEOUT -> {
                module.onError(SmsConsentException(SmsConsentErrorType.RETRIEVAL_TIMEOUT, "SMS retrieval timed out"))
            }
            else -> {
                module.onError(SmsConsentException(SmsConsentErrorType.BROADCAST_FAILURE, "Unhandled status"))
            }
        }
    }
}
