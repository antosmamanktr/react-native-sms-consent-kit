package com.mysmsconsent

class SmsConsentException(
    val errorType: SmsConsentErrorType,
    override val message: String
) : Exception(message)
