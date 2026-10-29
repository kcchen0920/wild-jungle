# Local Development & GitHub Sync Rules

## 1. Mandatory GitHub Continuous Sync (GitHub 維護與即時同步規範)
- **Repository Remote**: `https://github.com/kcchen0920/wild-catcher-project.git`
- **Default Main Branch**: `main`
- **Auto-Sync Policy**: 每次完成程式碼、資源、設定或文件修改與驗證後，必須將變更加入 git (`git add .`)、建立簡明規範的 commit message，並即時推送至 GitHub (`git push origin <branch>`)。
- **Pre-Push Security Check**: 推送前確保機密資訊（API token、密鑰等）不被提交（嚴格恪守 `.gitignore`）。
