param()
$ErrorActionPreference = "Stop"

$scriptRoot = $PSScriptRoot
$javaHome = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"

# 自動定位本機共用 Android SDK 與 Gradle 工具目錄
$parent = Split-Path -Parent $scriptRoot
$sdkFolder = Get-ChildItem -Path $parent -Directory | Where-Object { Test-Path (Join-Path $_.FullName "tools\android-sdk") } | Select-Object -First 1

if ($sdkFolder) {
    $androidHome = Join-Path $sdkFolder.FullName "tools\android-sdk"
    $toolsGradle = Join-Path $sdkFolder.FullName "tools\gradle\bin"
    if (Test-Path $toolsGradle) {
        $env:PATH = "$toolsGradle;$env:PATH"
    }
} else {
    $androidHome = "$env:LOCALAPPDATA\Android\Sdk"
}

$projectDir = Join-Path $scriptRoot "wild_catcher_project"
$gradleBat = Join-Path $projectDir "gradlew.bat"

Write-Host "Starting WildCatcher Build..." -ForegroundColor Cyan

$env:JAVA_HOME = $javaHome
$env:ANDROID_HOME = $androidHome
$env:ANDROID_SDK_ROOT = $androidHome
$env:JAVA_TOOL_OPTIONS = ""
$env:PATH = "$javaHome\bin;$env:PATH"

# 自動配置 local.properties 供 Gradle 讀取 Android SDK
$localProp = Join-Path $projectDir "local.properties"
$forwardSlashSdk = $androidHome.Replace('\', '/')
[System.IO.File]::WriteAllText($localProp, "sdk.dir=$forwardSlashSdk`n", [System.Text.Encoding]::ASCII)

Set-Location -LiteralPath $projectDir

& $gradleBat assembleDebug --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build FAILED! Gradle exited with code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}

$apkOutput = Join-Path $projectDir "app\build\outputs\apk\debug\app-debug.apk"
$targetApk = Join-Path $scriptRoot "wild_catcher-debug.apk"

if (Test-Path -LiteralPath $apkOutput) {
    Copy-Item -LiteralPath $apkOutput -Destination $targetApk -Force
    Write-Host "Build SUCCESS! Output: $targetApk" -ForegroundColor Green

    # 自動同步至 Google 雲端硬碟 (G:\我的雲端硬碟\kids\)
    $cloudDirs = @(
        (Join-Path "G:\" "我的雲端硬碟\kids"),
        (Join-Path "G:\" "kids"),
        (Join-Path "D:\" "kids")
    )
    foreach ($cDir in $cloudDirs) {
        try {
            if (-not (Test-Path -LiteralPath $cDir)) { 
                New-Item -ItemType Directory -Path $cDir -Force -ErrorAction SilentlyContinue | Out-Null
            }
            if (Test-Path -LiteralPath $cDir) {
                $destFile = Join-Path $cDir "wild_catcher-debug.apk"
                Copy-Item -LiteralPath $targetApk -Destination $destFile -Force
                Write-Host "Synced to Cloud Drive: $destFile" -ForegroundColor Green
            }
        } catch {
            Write-Host "Sync to $cDir failed: $_" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "Error: APK not found at $apkOutput" -ForegroundColor Red
    exit 1
}
