param()
$ErrorActionPreference = "Stop"

$scriptRoot = $PSScriptRoot
$javaHome = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"
$androidHome = Join-Path (Split-Path -Parent $scriptRoot) "android車機\tools\android-sdk"
if (-not (Test-Path -LiteralPath $androidHome)) {
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
