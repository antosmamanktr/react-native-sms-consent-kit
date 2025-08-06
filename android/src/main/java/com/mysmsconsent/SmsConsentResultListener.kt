package com.mysmsconsent

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.BaseActivityEventListener
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsConsentResultListener(private val module: SmsConsentModule) : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == SmsRetriever.SMS_CONSENT_REQUEST) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            if (message != null) {
                module.onSmsReceived(message)
            } else {
                module.onError(
                    SmsConsentException(
                        SmsConsentErrorType.MESSAGE_NOT_FOUND,
                        "No message found"
                    )
                )
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            module.onError(
                SmsConsentException(
                    SmsConsentErrorType.USER_CANCELED,
                    "User denied SMS consent"
                )
            )
        }

        // ✅ Always remove listener after receiving result
        module.reactContext.removeActivityEventListener(this)
    }
}

}
