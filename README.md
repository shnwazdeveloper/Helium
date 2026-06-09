# SHNWAZ Helium Windows

Unofficial Windows packaging mirror for the Helium browser, maintained by
SHNWAZ Developer.

This repository publishes a Windows installer for PC users and keeps owner links,
release details, and build notes in one place.

## Download latest EXE

Latest release:
[0.13.1.1](https://github.com/shnwazdeveloper/helium-windows/releases/tag/0.13.1.1)

Direct installer:
[helium_0.13.1.1_x64-installer.exe](https://github.com/shnwazdeveloper/helium-windows/releases/download/0.13.1.1/helium_0.13.1.1_x64-installer.exe)

SHA-256:

```text
c6a6ce986077fa097a509ef5188b26ae0f42646fa5a6ea08ecc0d355f60f005c
```

## Owner

SHNWAZ Developer is the owner of this fork and release page.

- Owner profile: https://shnwazdeveloper.github.io/owner.html
- GitHub: https://github.com/shnwazdeveloper
- GitHub avatar: https://avatars.githubusercontent.com/u/271950542?v=4
- Telegram: https://t.me/Syntaxpy
- X: https://x.com/shnwazdev
- LinkedIn: https://www.linkedin.com/in/shnwazdev/
- Instagram: https://instagram.com/sexyshnwaz
- SayaProject: https://github.com/SayaProject
- SayaGram: https://github.com/SayaGram

More owner details are in [OWNER.md](OWNER.md).

## Where to change things

Use this map when editing the repo:

- GitHub repo README and owner text: `README.md`
- Owner social/profile page in this repo: `OWNER.md`
- Windows installer wrapper metadata: `installer/helium.nsi`
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

Suggested homepage:

```text
https://shnwazdeveloper.github.io/owner.html
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
gh workflow run main.yml --repo shnwazdeveloper/helium-windows -f runner=windows-2022 -f do-release=false -f sign_binaries=false
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
