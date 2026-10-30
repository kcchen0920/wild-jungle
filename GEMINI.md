# Local Development & GitHub Sync Rules

## 1. Mandatory GitHub Continuous Maintenance (GitHub 維護與即時同步規範)
- **Repository Remote**:
  - Web URL: `https://github.com/kcchen0920/wild-jungle`
  - Push URL (SSH, recommended): `git@github.com:kcchen0920/wild-jungle.git`
  - HTTPS: `https://github.com/kcchen0920/wild-jungle.git`
- **Default Main Branch**: `main`

## 2. Change & Push Workflow (每次改正後即時同步流程)
每次完成任何程式碼修正、功能調整、Bug 修正、新規格實作或文件修改並驗證後，必須立即執行以下三步驟將變更完整同步至 GitHub：
1. **Stage**: `git add .`
2. **Commit**: `git commit -m "<清楚描述改動項目與修正內容的 commit message>"`
3. **Push**: `git push origin main`

## 3. Pre-Push Safety Check (安全性與乾淨度審查)
- 推送前恪守 `.gitignore`，確保機密資訊（API token、金鑰、憑證等）與暫存編譯產物絕不被提交。
- 維護主幹（main）歷史清晰，確保遠端 GitHub 儲存庫隨時保持可建置、可運行的最新狀態。

## 4. Mandatory APK Dual-Storage to Google Drive (APK 雲端硬碟同步規範)
- **目標目錄**: `G:\我的雲端硬碟\kids\`
- **同步要求**: 每次編譯、產出或更新 Android 專案的 APK（包含 debug 與 release 版本）時，必須同時複製一份至 `G:\我的雲端硬碟\kids\`，確保兒童平板端隨時可存取最新安裝包。
