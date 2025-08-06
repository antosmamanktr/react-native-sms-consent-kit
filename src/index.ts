import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  EmitterSubscription,
  NativeModule,
} from 'react-native';
import { useEffect, useState } from 'react';

type SmsConsentType = {
  startSmsConsentWatcher: () => Promise<void>;
  stopSmsConsentWatcher: () => Promise<void>;
  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
};

const { SmsConsent } = NativeModules;
const SmsConsentTyped = SmsConsent as SmsConsentType;

export const SmsConsentEmitter = new NativeEventEmitter(SmsConsentTyped);

export const Events = {
  SMS_CONSENT_RECEIVED: 'EVENT_SMS_CONSENT_RECEIVED',
  SMS_CONSENT_ERROR: 'EVENT_SMS_CONSENT_ERROR',
};

export function startSmsConsentWatcher(): Promise<void> {
  if (Platform.OS === 'android' && SmsConsentTyped?.startSmsConsentWatcher) {
    return SmsConsentTyped.startSmsConsentWatcher();
  }
  return Promise.resolve();
}

export function stopSmsConsentWatcher(): Promise<void> {
  if (Platform.OS === 'android' && SmsConsentTyped?.stopSmsConsentWatcher) {
    return SmsConsentTyped.stopSmsConsentWatcher();
  }
  return Promise.resolve();
}

export function useSmsConsent(autoStart = true): string | null {
  const [retrievedCode, setRetrievedCode] = useState<string | null>(null);

  useEffect(() => {
    if (!autoStart || Platform.OS !== 'android') return;

    let receivedSub: EmitterSubscription;
    let errorSub: EmitterSubscription;

    startSmsConsentWatcher()
      .then(() => {
        receivedSub = SmsConsentEmitter.addListener(
          Events.SMS_CONSENT_RECEIVED,
          (event: { message: string }) => {
            setRetrievedCode(event.message);
            stopSmsConsentWatcher();
            receivedSub?.remove();
            errorSub?.remove();
          }
        );

        errorSub = SmsConsentEmitter.addListener(
          Events.SMS_CONSENT_ERROR,
          (error: any) => {
            console.warn('[SMS_CONSENT_ERROR]', error);
            stopSmsConsentWatcher();
            receivedSub?.remove();
            errorSub?.remove();
          }
        );
      })
      .catch((err) => {
        console.error('Failed to start SMS Consent:', err);
      });

    return () => {
      stopSmsConsentWatcher();
      receivedSub?.remove();
      errorSub?.remove();
    };
  }, [autoStart]);

  return retrievedCode;
}
