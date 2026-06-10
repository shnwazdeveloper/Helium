param(
  [string] $AndroidSdkRoot = $env:ANDROID_SDK_ROOT,
  [string] $JavaHome = $env:JAVA_HOME,
  [string] $OutputApk = "build\helium-mobile-shnwaz_0.13.1.3.apk",
  [string] $VersionName = "0.13.1.3",
  [int] $VersionCode = 13
)

$ErrorActionPreference = "Stop"

if (-not $AndroidSdkRoot) {
  throw "AndroidSdkRoot is required."
}
if (-not $JavaHome) {
  throw "JavaHome is required."
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..\..")
$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$appRoot = Join-Path $projectRoot "app"
$buildRoot = Join-Path $repoRoot "build\mobile-android"
$signingRoot = Join-Path $repoRoot "build\android-signing"
$packageName = "dev.shnwaz.helium"

$env:JAVA_HOME = (Resolve-Path $JavaHome).Path
$env:ANDROID_SDK_ROOT = (Resolve-Path $AndroidSdkRoot).Path
$env:ANDROID_HOME = $env:ANDROID_SDK_ROOT
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_SDK_ROOT\platform-tools;$env:Path"

$androidJar = Join-Path $AndroidSdkRoot "platforms\android-35\android.jar"
$buildTools = Join-Path $AndroidSdkRoot "build-tools\35.0.0"
$aapt = Join-Path $buildTools "aapt.exe"
$aapt2 = Join-Path $buildTools "aapt2.exe"
$d8 = Join-Path $buildTools "d8.bat"
$zipalign = Join-Path $buildTools "zipalign.exe"
$apksigner = Join-Path $buildTools "apksigner.bat"
$javac = Join-Path $JavaHome "bin\javac.exe"
$keytool = Join-Path $JavaHome "bin\keytool.exe"

foreach ($tool in @($androidJar, $aapt, $aapt2, $d8, $zipalign, $apksigner, $javac, $keytool)) {
  if (-not (Test-Path -LiteralPath $tool)) {
    throw "Missing required tool: $tool"
  }
}

Remove-Item -LiteralPath $buildRoot -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $buildRoot, $signingRoot | Out-Null

$compiledRes = Join-Path $buildRoot "compiled-res"
$classesDir = Join-Path $buildRoot "classes"
$dexDir = Join-Path $buildRoot "dex"
$generatedDir = Join-Path $buildRoot "generated"
$unsignedApk = Join-Path $buildRoot "unsigned.apk"
$alignedApk = Join-Path $buildRoot "aligned.apk"
$manifest = Join-Path $appRoot "src\main\AndroidManifest.xml"
$resDir = Join-Path $appRoot "src\main\res"
$javaDir = Join-Path $appRoot "src\main\java"

New-Item -ItemType Directory -Force -Path $compiledRes, $classesDir, $dexDir, $generatedDir | Out-Null

$resFiles = Get-ChildItem -Path $resDir -Recurse -File |
  Where-Object { $_.Extension -in ".xml", ".png", ".webp" }

foreach ($file in $resFiles) {
  & $aapt2 compile --dir $resDir -o (Join-Path $compiledRes "resources.zip") | Out-Null
  break
}

& $aapt2 link `
  -o $unsignedApk `
  -I $androidJar `
  --manifest $manifest `
  --java $generatedDir `
  --min-sdk-version 23 `
  --target-sdk-version 35 `
  --version-code $VersionCode `
  --version-name $VersionName `
  --auto-add-overlay `
  (Join-Path $compiledRes "resources.zip")
if ($LASTEXITCODE -ne 0) { throw "aapt2 link failed." }

$javaFiles = Get-ChildItem -Path $javaDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -source 1.8 -target 1.8 `
  -bootclasspath $androidJar `
  -d $classesDir `
  $javaFiles
if ($LASTEXITCODE -ne 0) { throw "javac failed." }

$classFiles = Get-ChildItem -Path $classesDir -Recurse -Filter "*.class" | ForEach-Object { $_.FullName }
& $d8 --min-api 23 --lib $androidJar --output $dexDir $classFiles
if ($LASTEXITCODE -ne 0) { throw "d8 failed." }

Push-Location $dexDir
try {
  & $aapt add $unsignedApk "classes.dex" | Out-Null
} finally {
  Pop-Location
}
if ($LASTEXITCODE -ne 0) { throw "adding classes.dex failed." }

& $zipalign -f -p 4 $unsignedApk $alignedApk
if ($LASTEXITCODE -ne 0) { throw "zipalign failed." }

$keystore = Join-Path $signingRoot "shnwaz-mobile-dev.keystore"
$alias = "shnwaz-mobile"
$storePass = "changeit-shnwaz-dev"
$keyPass = "changeit-shnwaz-dev"

if (-not (Test-Path -LiteralPath $keystore)) {
  & $keytool -genkeypair `
    -keystore $keystore `
    -storepass $storePass `
    -keypass $keyPass `
    -alias $alias `
    -keyalg RSA `
    -keysize 4096 `
    -validity 10000 `
    -dname "CN=SHNWAZ Developer, OU=Mobile, O=SHNWAZ Developer, L=Patna, ST=Bihar, C=IN"
  if ($LASTEXITCODE -ne 0) { throw "keytool failed." }
}

$resolvedOutput = Join-Path $repoRoot $OutputApk
New-Item -ItemType Directory -Force -Path (Split-Path $resolvedOutput -Parent) | Out-Null

& $apksigner sign `
  --ks $keystore `
  --ks-key-alias $alias `
  --ks-pass "pass:$storePass" `
  --key-pass "pass:$keyPass" `
  --out $resolvedOutput `
  $alignedApk
if ($LASTEXITCODE -ne 0) { throw "apksigner failed." }

& $apksigner verify --verbose --print-certs $resolvedOutput
if ($LASTEXITCODE -ne 0) { throw "apksigner verify failed." }

Get-Item -LiteralPath $resolvedOutput
