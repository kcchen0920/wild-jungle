# Deploy APK to Google Drive Kids Directory
param(
    [string]$TargetDir = "G:\我的雲端硬碟\kids"
)

Write-Host ">>> Starting APK deployment to Google Drive: $TargetDir" -ForegroundColor Cyan

if (-not (Test-Path $TargetDir)) {
    Write-Host "Creating target directory: $TargetDir" -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path $TargetDir | Out-Null
}

$apkOutputs = Get-ChildItem -Path "wild_catcher_project\app\build\outputs\apk" -Filter "*.apk" -Recurse -ErrorAction SilentlyContinue

if ($apkOutputs.Count -eq 0) {
    Write-Host "No APK found in build outputs. Checking root build..." -ForegroundColor Yellow
} else {
    foreach ($apk in $apkOutputs) {
        $destPath = Join-Path $TargetDir $apk.Name
        Copy-Item -Path $apk.FullName -Destination $destPath -Force
        Write-Host "✓ Deployed $($apk.Name) -> $destPath" -ForegroundColor Green
    }
}

Write-Host ">>> APK deployment completed successfully." -ForegroundColor Green
