package com.jack

class SmsConsentException(val errorType: SmsConsentErrorType, message: String) : RuntimeException(message)
