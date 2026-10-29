# GitHub Continuous Sync Rule

每次完成任何程式碼修正、功能調整或文件更新後，必須執行以下同步流程：
```bash
git add .
git commit -m "<清楚描述改動與修復內容的 commit message>"
git push origin main
```

- 遠端網址：`https://github.com/kcchen0920/wild-catcher-project`
- SSH 推送網址：`git@github.com:kcchen0920/wild-catcher-project.git`
- 目的：確保 GitHub 上的原始碼與本地工作區隨時保持 100% 一致與最新狀態。
