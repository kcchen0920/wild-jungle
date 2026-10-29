# Deploy APK to Google Drive Kids Directory
Write-Host ">>> Starting APK deployment to Google Drive..." -ForegroundColor Cyan

$rootDirs = Get-ChildItem -Path "G:\" -Directory -ErrorAction SilentlyContinue

if ($rootDirs.Count -gt 0) {
    $driveFolder = $rootDirs | Where-Object { $_.Name -like "*硬碟*" -or $_.Name -like "*Drive*" } | Select-Object -First 1
    if (-not $driveFolder) {
        $driveFolder = $rootDirs[0]
    }
    $targetDir = Join-Path $driveFolder.FullName "kids"
} else {
    $targetDir = "G:\kids"
}

if (-not (Test-Path $targetDir)) {
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
}

$apkOutputs = Get-ChildItem -Path "wild_catcher_project\app\build\outputs\apk" -Filter "*.apk" -Recurse -ErrorAction SilentlyContinue

if ($apkOutputs.Count -eq 0) {
    Write-Host "No APK found in build outputs." -ForegroundColor Yellow
} else {
    foreach ($apk in $apkOutputs) {
        $destDebug = Join-Path $targetDir "wild_catcher-debug.apk"
        $destRelease = Join-Path $targetDir "wild_catcher-v1.0.0.apk"
        Copy-Item -Path $apk.FullName -Destination $destDebug -Force
        Copy-Item -Path $apk.FullName -Destination $destRelease -Force
        Write-Host "Deployed APK successfully to:" -ForegroundColor Green
        Write-Host "  -> $destDebug" -ForegroundColor Green
        Write-Host "  -> $destRelease" -ForegroundColor Green
    }
}

Write-Host ">>> APK deployment completed successfully." -ForegroundColor Green
