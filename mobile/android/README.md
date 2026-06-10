# SHNWAZ Helium Mobile

This Android app is a lightweight WebView browser companion for the SHNWAZ
Helium Windows fork. It is not a port of the Windows Chromium binary; Android
uses a separate app package and signing model.

## Build

From the repository root:

```powershell
powershell -ExecutionPolicy Bypass -File mobile/android/scripts/build-apk.ps1 `
  -AndroidSdkRoot C:\path\to\android-sdk `
  -JavaHome C:\path\to\jdk17 `
  -OutputApk build\helium-mobile-shnwaz_0.13.1.3.apk
```

The script generates a local development signing key under
`build/android-signing` when one does not already exist. Do not commit private
keystores to git.

Firebase App Distribution can distribute APKs, but Firebase does not sign APKs
for you. Android APKs are signed with a keystore, or by Google Play App Signing
when publishing through the Play Store.
