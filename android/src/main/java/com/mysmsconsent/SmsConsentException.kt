package com.mysmsconsent

class SmsConsentException(val errorType: SmsConsentErrorType, message: String) : RuntimeException(message)
