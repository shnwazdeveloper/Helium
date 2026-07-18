# Helium 

Unofficial Windows packaging mirror for the Helium browser, maintained by
SHNWAZ Developer.

This repository publishes a Windows installer for PC users, a lightweight
Android APK companion, and owner links, release details, and build notes in one
place.

## Download latest

Latest release:
[0.13.1.3-shnwaz](https://github.com/shnwazdeveloper/helium-windows/releases/tag/0.13.1.3-shnwaz)

Windows x64 installer:
[helium_0.13.1.3-shnwaz_x64-installer.exe](https://github.com/shnwazdeveloper/helium-windows/releases/download/0.13.1.3-shnwaz/helium_0.13.1.3-shnwaz_x64-installer.exe)

The `0.13.1.3-shnwaz` Windows installer uses the normal Helium NSIS installer
layout with Chromium setup payloads. It replaces the older custom self-extracting
wrapper from `0.13.1.2-shnwaz`, which triggered heuristic detections on some
antivirus services.

## Where to change things

Use this map when editing the repo:

- GitHub repo README and owner text: `README.md`
- Owner social/profile page in this repo: `OWNER.md`
- Windows installer wrapper metadata: `installer/helium.nsi`
- Android APK companion app: `mobile/android`
- Windows branding patch used during Chromium packaging:
  `patches/helium/windows/change-branding.patch`
- In-browser About page owner line:
  `patches/helium/windows/about-page-dev-by-shnwaz.patch`
- Version patch used during Chromium packaging:
  `patches/helium/windows/helium-versioning.patch`

The in-browser page shown at `chrome://settings/help` / "About Helium" is not
generated from `README.md` or `installer/helium.nsi`. It comes from the Chromium
source checkout created under `build/src` during the full browser build.

After fetching the Chromium source, find the exact files with:

```powershell
python3 helium-chromium\utils\clone.py -o build\src
rg -n "About Chromium|About Helium|The Helium Authors|IDS_ABOUT_VERSION|open source project" build\src\chrome build\src\components
```

Then add the required source changes as a patch in this repo before running the
full build. Do not change only the installer if you want the browser's internal
About page to change; the installer wrapper and the Chromium UI are separate.

## GitHub About text

Suggested repository description:

```text
SHNWAZ Developer Windows EXE releases for Helium browser, with owner profile and social links.
```

## Building

Run in `Developer Command Prompt for VS` as administrator:

```cmd
git clone --recurse-submodules https://github.com/shnwazdeveloper/helium-windows.git
cd helium-windows
git checkout --recurse-submodules main
python3 build.py
python3 package.py
```

A zip archive and an installer will be created under `build`.

GitHub Actions can build an unsigned installer artifact from the patched source:

```powershell
gh workflow run main.yml --repo shnwazdeveloper/helium-windows -f runner=windows-2022 -f do-release=false -f sign_binaries=false -f build_arm=false -f publish_x64_release=true
```

### Required setup

- Visual Studio with the Chromium Windows build components installed.
- 7-Zip.
- Python 3.8 or above.
- Python modules: `httplib2==0.22.0` and `Pillow`.
- Git.
- Windows long path support enabled.

Install Python modules with:

```powershell
python -m pip install httplib2==0.22.0 Pillow
```

Follow Chromium's official Windows build instructions for Visual Studio:
https://chromium.googlesource.com/chromium/src/+/refs/heads/main/docs/windows_build_instructions.md#visual-studio

## Android APK

The Android app in `mobile/android` is a lightweight WebView browser companion
with SHNWAZ owner and social links. It is not a direct conversion of the Windows
Chromium EXE, because Android apps use APK packaging and Android signing.

Build it with:

```powershell
powershell -ExecutionPolicy Bypass -File mobile\android\scripts\build-apk.ps1 `
  -AndroidSdkRoot C:\path\to\android-sdk `
  -JavaHome C:\path\to\jdk17 `
  -OutputApk build\helium-mobile-shnwaz_0.13.1.3.apk
```

The build script signs the APK with a local SHNWAZ Developer development
keystore under `build/android-signing`. For Play Store publishing, use Google
Play App Signing or a production release keystore. Firebase App Distribution can
distribute APKs, but it does not replace APK signing.

## Signing and antivirus notes

The Windows installer in this repository is unsigned unless a real Authenticode
code-signing certificate is configured. Firebase does not sign Windows EXE files.
For Windows trust reputation, use an Authenticode certificate and sign the EXE
with Microsoft's signing tools.

## Credits

This repo is based on the Helium Windows packaging project and
ungoogled-chromium-windows. Helium is based on Chromium and other open source
software. Keep Chromium, Helium, and third-party license notices intact when
building or publishing modified binaries.

## License

All code, patches, modified portions of imported code or patches, and any other
content that is unique to Helium and not imported from other repositories is
licensed under GPL-3.0. See [LICENSE](LICENSE).

Any content imported from other projects retains its original license, for
example original unmodified code imported from ungoogled-chromium remains
licensed under the BSD 3-Clause license in [LICENSE.ungoogled_chromium](LICENSE.ungoogled_chromium).
