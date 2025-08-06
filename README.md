# 📲 @react-native-community/sms-user-consent

A secure and lightweight **React Native module** for reading **one-time passwords (OTP)** using Android's **SMS User Consent API** — **without SMS permission**.

Perfect for apps needing secure OTP verification (e.g., login, signup, password reset).

---

## ✨ Features

- ✅ No `RECEIVE_SMS` permission needed
- ⚡ Automatically handles SMS consent dialog
- 📤 Emits success/error events
- 🎣 Hook-based API (`useSmsConsent`) or manual listener
- 💬 TypeScript definitions included
- ⚙️ Works with Android 5.0+ (API 21+)

---

## 📦 Installation

```bash
npm install @react-native-community/sms-user-consent
# or
yarn add @react-native-community/sms-user-consent
```

---

## 🛠️ Android Setup

### 1. Autolinking
No manual linking required if using React Native 0.60+.

### 2. Permissions
## 📱 Android Permissions Setup

To use the **SMS User Consent API**, your app must declare the following permissions in `AndroidManifest.xml`:

### 🔧 Required Permissions

```xml
<uses-permission android:name="android.permission.RECEIVE_SMS" />
<uses-permission android:name="android.permission.READ_SMS" />
```

---

## 🔧 Usage

### ✅ 1. Using `useSmsConsent` Hook

```tsx
import React from 'react';
import { Text, View } from 'react-native';
import { useSmsConsent } from '@react-native-community/sms-user-consent';

export default function OTPComponent() {
  const otp = useSmsConsent(); // autoStart defaults to true

  return (
    <View>
      <Text>OTP: {otp}</Text>
    </View>
  );
}
```

---

### 🕹️ 2. Manual Start/Stop with autoStart = false

```tsx
import React, { useEffect, useState } from 'react';
import {
  startSmsConsentWatcher,
  stopSmsConsentWatcher,
  SmsConsentEmitter,
  Events,
  useSmsConsent,
} from '@react-native-community/sms-user-consent';

export default function ManualOtpComponent() {
  const otp = useSmsConsent(false); // ❌ Don't auto-start
  const [code, setCode] = useState<string | null>(null);

  useEffect(() => {
    const received = SmsConsentEmitter.addListener(
      Events.SMS_CONSENT_RECEIVED,
      (event) => {
        console.log('✅ Message received', event.message);
        setCode(event.message);
        stopSmsConsentWatcher();
        received.remove();
      }
    );

    startSmsConsentWatcher();

    return () => {
      stopSmsConsentWatcher();
      received.remove();
    };
  }, []);

  return <Text>OTP Received: {code}</Text>;
}
```

---

## 📘 API Reference

### `useSmsConsent(autoStart?: boolean): string | null`

- **Returns**: the retrieved message or OTP
- **autoStart**: set to `false` if you want to trigger `startSmsConsentWatcher()` manually.

---

## 🚀 Event Emitter Constants

```ts
Events.SMS_CONSENT_RECEIVED // When message is successfully retrieved
Events.SMS_CONSENT_ERROR    // When an error occurs (e.g., USER_CANCELED)
```
---

## ✅ Supported Platforms

| Platform | Supported |
|----------|-----------|
| Android  | ✅ Yes (API 21+) |
| iOS      | ❌ No support (Android only) |

---

## 🤝 Contributing

Pull requests, bug reports, and feature suggestions welcome! [Open an issue](https://github.com/antosmamanktr/react-native-sms-user-consent/issues)

---

## 📄 References

- [Android SMS User Consent API](https://developers.google.com/identity/sms-retriever/overview#user-consent-api)
- [React Native Docs](https://reactnative.dev/)

---

## 🧑‍💻 Author

**Made with ❤️ by Antos Maman**

- GitHub: [@antosmamanktr](https://github.com/antosmamanktr)
- Email: [antosmamanktr@gmail.com](mailto\:antosmamanktr@gmail.com)

---

## 📄 License

MIT License
