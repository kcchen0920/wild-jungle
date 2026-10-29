# APK Distribution Rule to Google Drive

每次產出、打包或更新 Android APK 檔時，必須同步執行以下步驟：
1. 確保產出的 APK 命名規範為 `wild_catcher-debug.apk` 或 `wild_catcher-v<version>.apk`。
2. 自動複製一份至 Google 雲端硬碟目標路徑：
   - 目標路徑：`G:\我的雲端硬碟\kids\`
3. 執行驗證確保檔案完整寫入，供 Android 平板直接下載安裝。
