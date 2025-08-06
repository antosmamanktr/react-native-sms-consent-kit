import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const { SmsConsent } = NativeModules;
const SmsConsentEmitter = new NativeEventEmitter(SmsConsent);

export const Events = {
  SMS_CONSENT_RECEIVED: 'SMS_CONSENT_RECEIVED',
  SMS_CONSENT_ERROR: 'SMS_CONSENT_ERROR',
};

export function startSmsConsentWatcher() {
  if (Platform.OS === 'android') {
    return SmsConsent.startSmsConsentWatcher();
  } else {
    return Promise.resolve();
  }
}

export function stopSmsConsentWatcher() {
  if (Platform.OS === 'android') {
    return SmsConsent.stopSmsConsentWatcher();
  } else {
    return Promise.resolve();
  }
}

export { SmsConsentEmitter };
