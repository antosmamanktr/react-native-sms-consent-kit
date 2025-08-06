package com.mysmsconsent

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsConsentReceiver(
    private val activity: Activity,
    private val module: SmsConsentModule
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return

        val extras = intent.extras ?: return
        val status = extras.get(SmsRetriever.EXTRA_STATUS) as? com.google.android.gms.common.api.Status

        when (status?.statusCode) {
            com.google.android.gms.common.api.CommonStatusCodes.SUCCESS -> {
                val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                if (consentIntent != null) {
                    activity.startActivityForResult(consentIntent, SmsConsentResultListener.SMS_CONSENT_REQUEST)
                }
            }

            com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT -> {
                module.onError(
                    SmsConsentException(
                        SmsConsentErrorType.MESSAGE_NOT_FOUND,
                        "Timeout waiting for SMS"
                    )
                )
            }
        }
    }
}
