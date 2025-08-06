package com.jack

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.BaseActivityEventListener
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsConsentResultListener(private val module: SmsConsentModule) : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != SmsConsentReceiver.REQUEST_CODE) return

        if (resultCode == Activity.RESULT_OK && data != null) {
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let { module.onSmsReceived(it) }
        } else {
            module.onError(SmsConsentException(SmsConsentErrorType.USER_CANCELED, "User denied SMS consent"))
        }
    }
}
