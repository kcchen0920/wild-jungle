# Local Development & GitHub Sync Rules

## 1. Mandatory GitHub Continuous Maintenance (GitHub 維護與即時同步規範)
- **Repository Remote**:
  - Web URL: `https://github.com/kcchen0920/wild-catcher-project`
  - Push URL (SSH, recommended): `git@github.com:kcchen0920/wild-catcher-project.git`
  - HTTPS: `https://github.com/kcchen0920/wild-catcher-project.git`
- **Default Main Branch**: `main`

## 2. Change & Push Workflow (每次改正後即時同步流程)
每次完成任何程式碼修正、功能調整、Bug 修正、新規格實作或文件修改並驗證後，必須立即執行以下三步驟將變更完整同步至 GitHub：
1. **Stage**: `git add .`
2. **Commit**: `git commit -m "<清楚描述改動項目與修正內容的 commit message>"`
3. **Push**: `git push origin main`

## 3. Pre-Push Safety Check (安全性與乾淨度審查)
- 推送前恪守 `.gitignore`，確保機密資訊（API token、金鑰、憑證等）與暫存編譯產物絕不被提交。
- 維護主幹（main）歷史清晰，確保遠端 GitHub 儲存庫隨時保持可建置、可運行的最新狀態。
