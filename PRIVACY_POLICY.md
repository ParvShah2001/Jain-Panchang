# Privacy Policy for Jain Panchang

**Last updated:** October 2026

**Jain Panchang** is a free, completely ad-free, 100% offline Jain religious calendar and daily spiritual companion application for Android.

### 1. No Data Collection or Transmission
- **No Personal Data Collected:** The application does not collect, store, transmit, or share any personal identity, account, email, or device identifier.
- **No Analytics & No Advertisements:** The app contains zero tracking libraries, zero third-party telemetry, and zero ads.
- **Offline Guarantee:** Core features function entirely offline. No data ever leaves your device.

### 2. Device Permissions Used
The application requests only necessary permissions strictly to enable local features:
- **Location (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`):** Optional. Used exclusively on-device to determine your latitude, longitude, and elevation for calculating accurate local astronomical sunrise, sunset, and tithi timings. Your location is never sent to any server. You may also select your city manually from the bundled offline cities list without granting location permission.
- **Exact Alarms (`SCHEDULE_EXACT_ALARM`):** Used to trigger precise notifications for sunrise, sunset (Chauvihar), Pachkhan timings, and user-defined tithi events while the device is in low-power idle mode.
- **Post Notifications (`POST_NOTIFICATIONS`):** Used strictly to deliver local reminders requested by you.
- **Boot Completed (`RECEIVE_BOOT_COMPLETED`):** Used to reschedule local alarms if your device is restarted or if your timezone changes.
- **Calendar (`READ_CALENDAR` / `WRITE_CALENDAR`):** Optional. Used only when you explicitly tap to synchronize a Jain festival or tithi event to your personal phone calendar via Android's local CalendarContract.

### 3. Local Storage
All your custom notes, saved favorite cities, and niyam completion records are saved solely on your local device in a private SQLite database. Uninstalling the application completely removes all locally saved data.

### 4. Contact & Open Inquiries
For questions or suggestions regarding religious calculations or the app, please submit an issue via the official repository.
