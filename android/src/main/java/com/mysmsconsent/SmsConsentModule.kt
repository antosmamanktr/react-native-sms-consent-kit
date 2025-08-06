package com.mysmsconsent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.android.gms.auth.api.phone.SmsRetriever

class SmsConsentModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    private var receiver: SmsConsentReceiver? = null
    private val resultListener = SmsConsentResultListener(this)

    companion object {
        const val EVENT_SMS_CONSENT_RECEIVED = "SMS_CONSENT_RECEIVED"
        const val EVENT_SMS_CONSENT_ERROR = "SMS_CONSENT_ERROR"
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Throws(SmsConsentException::class)
    private fun beginListening() {
        val activity = currentActivity ?: throw SmsConsentException(
            SmsConsentErrorType.ACTIVITY_UNAVAILABLE,
            "Activity is null"
        )

        SmsRetriever.getClient(activity).startSmsUserConsent(null)
        receiver = SmsConsentReceiver(activity, this)

        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= 34) {
            activity.registerReceiver(receiver, filter, SmsRetriever.SEND_PERMISSION, null, Context.RECEIVER_EXPORTED)
        } else {
            activity.registerReceiver(receiver, filter, SmsRetriever.SEND_PERMISSION, null)
        }

        reactContext.addActivityEventListener(resultListener)
    }

    @Throws(SmsConsentException::class)
    private fun stopListening() {
        val activity = currentActivity ?: throw SmsConsentException(
            SmsConsentErrorType.ACTIVITY_UNAVAILABLE,
            "Activity is null"
        )

        receiver?.let {
            try {
                activity.unregisterReceiver(it)
                receiver = null
                reactContext.removeActivityEventListener(resultListener)
            } catch (e: IllegalArgumentException) {
                throw SmsConsentException(SmsConsentErrorType.RECEIVER_NOT_REGISTERED, "Receiver not registered")
            }
        } ?: throw SmsConsentException(SmsConsentErrorType.RECEIVER_NOT_REGISTERED, "Receiver not found")
    }

    fun onSmsReceived(message: String) {
        val payload = Arguments.createMap()
        payload.putString("message", message)
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(EVENT_SMS_CONSENT_RECEIVED, payload)
    }

    fun onError(e: SmsConsentException) {
        val payload = Arguments.createMap()
        payload.putString(e.errorType.name, e.message)
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(EVENT_SMS_CONSENT_ERROR, payload)
    }

    @ReactMethod
    fun startSmsConsentWatcher(promise: Promise) {
        try {
            beginListening()
            promise.resolve(null)
        } catch (e: SmsConsentException) {
            promise.reject(e.errorType.name, e.message)
        }
    }

    @ReactMethod
    fun stopSmsConsentWatcher(promise: Promise) {
        try {
            stopListening()
            promise.resolve(null)
        } catch (e: SmsConsentException) {
            promise.reject(e.errorType.name, e.message)
        }
    }

    @ReactMethod fun addListener(eventName: String?) {}
    @ReactMethod fun removeListeners(count: Int) {}

    override fun getConstants(): Map<String, Any> {
        return SmsConsentErrorType.values().associate { it.name to it.name }
    }

    override fun getName(): String = "SmsConsent"
}
