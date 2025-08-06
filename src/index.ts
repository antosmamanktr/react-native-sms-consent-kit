import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  EmitterSubscription,
} from "react-native";
import { useEffect, useRef, useState } from "react";

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
  SMS_CONSENT_RECEIVED: "EVENT_SMS_CONSENT_RECEIVED",
  SMS_CONSENT_ERROR: "EVENT_SMS_CONSENT_ERROR",
};

export function startSmsConsentWatcher(): Promise<void> {
  if (Platform.OS === "android" && SmsConsentTyped?.startSmsConsentWatcher) {
    return SmsConsentTyped.startSmsConsentWatcher();
  }
  return Promise.resolve();
}

export function stopSmsConsentWatcher(): Promise<void> {
  if (Platform.OS === "android" && SmsConsentTyped?.stopSmsConsentWatcher) {
    return SmsConsentTyped.stopSmsConsentWatcher();
  }
  return Promise.resolve();
}

export function useSmsConsent(autoStart = true): string | null {
  const [retrievedCode, setRetrievedCode] = useState<string | null>(null);
  const didReceive = useRef(false);
  const receivedSub = useRef<EmitterSubscription | null>(null);
  const errorSub = useRef<EmitterSubscription | null>(null);

  useEffect(() => {
    if (!autoStart || Platform.OS !== "android") return;

    didReceive.current = false;

    startSmsConsentWatcher()
      .then(() => {
        receivedSub.current = SmsConsentEmitter.addListener(
          Events.SMS_CONSENT_RECEIVED,
          (event: { message: string }) => {
            if (didReceive.current) return; // Ignore duplicates
            didReceive.current = true;

            setRetrievedCode(event.message);
            stopSmsConsentWatcher();
            receivedSub.current?.remove();
            errorSub.current?.remove();
          }
        );

        errorSub.current = SmsConsentEmitter.addListener(
          Events.SMS_CONSENT_ERROR,
          (error: any) => {
            console.warn("[SMS_CONSENT_ERROR]", error);
            stopSmsConsentWatcher();
            receivedSub.current?.remove();
            errorSub.current?.remove();
          }
        );
      })
      .catch((err) => {
        console.error("Failed to start SMS Consent:", err);
      });

    return () => {
      stopSmsConsentWatcher();
      receivedSub.current?.remove();
      errorSub.current?.remove();
    };
  }, [autoStart]);

  return retrievedCode;
}
