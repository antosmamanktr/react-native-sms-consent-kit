import { EmitterSubscription } from "react-native";

/**
 * Constants representing native event names emitted by the SMS Consent Module.
 */
export const Events: {
  SMS_CONSENT_RECEIVED: "EVENT_SMS_CONSENT_RECEIVED";
  SMS_CONSENT_ERROR: "EVENT_SMS_CONSENT_ERROR";
};

/**
 * Starts listening for SMS messages using the Android SMS User Consent API.
 * @returns A promise that resolves when the listener is started.
 */
export function startSmsConsentWatcher(): Promise<void>;

/**
 * Stops listening for SMS messages.
 * @returns A promise that resolves when the listener is stopped.
 */
export function stopSmsConsentWatcher(): Promise<void>;

/**
 * Event emitter that emits SMS_CONSENT_RECEIVED and SMS_CONSENT_ERROR events.
 */
export const SmsConsentEmitter: {
  addListener: (
    eventName: (typeof Events)[keyof typeof Events],
    listener: (data: any) => void
  ) => EmitterSubscription;
};

/**
 * Custom hook that starts SMS Consent watcher and automatically stops after receiving the message.
 * @param autoStart Optional boolean (default: true) to auto-start the listener.
 * @returns The retrieved SMS message or null.
 */
export function useSmsConsent(autoStart?: boolean): string | null;
