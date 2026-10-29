# 野性捕捉大師 (Wild Catcher: Jungle & Sky)

專為 **Android 平板 (強制橫向 16:10 / 16:9 顯示)** 設計之橫向動態野生動物捕捉、生態探索與輕量庇護所經營遊戲。以《潛水員戴夫》精緻圖文冒險筆記風格為視覺導向，搭載擬真 PBR 材質光影渲染、動態難度調節 (DDA) QTE 捕捉條與 30 分鐘健康護眼機制。

---

## 🎯 專案核心特色

1. **《潛水員戴夫》風格美學介面 (UI/UX)**
   - 仿皮質封面、泛黃紙張質感的圖文升級介面 (`DaveNotebookFrame`)。
   - 具有清脆翻頁與木質碰撞反饋音效，搭配小熊貓「波波」溫馨語音氣泡指引。

2. **熊貓槍 (Panda Gun) 與動態 QTE 捕捉條**
   - 點擊發射「夢幻泡泡網 / 竹香彈」，命中後觸發動態游標震盪捕捉條。
   - 透過點擊或長按在綠色「甜蜜點 (Sweet Spot)」內積累壓制進度，規避動物掙扎逃脫。
   - **動態難度調節 (DDA)**：連續失敗自動擴大甜蜜點 +6%~18% 並減緩動物掙脫速度，專為兒童打造友善、無挫折體驗。

3. **32 種寫實野生動物生態與互動圖鑑**
   - 100% 完整收錄 32 種真實動物（台灣黑熊、石虎、櫻花鉤吻鮭、金絲猴、大冠鷲等）。
   - 保育等級紅黃綠三色燈號、真實叫聲、生活習性與自然科普知識。

4. **自然生態庇護所經營 (Shelter Economy)**
   - 熱帶雨林、高山針葉林、晴空猛禽、清溪活水四大生態區。
   - 安置捕獲動物產生門票收益，持續升級熊貓槍氣壓馬達與彈藥容量。

5. **30 分鐘健康遊玩提醒與護眼模式 (Panda's Rest Time)**
   - 連續遊玩滿 30 分鐘，熊貓助手波波溫和提示：「太陽要下山囉，讓眼睛休息一下，明天再來探索！」
   - 自動切換暖色調護眼濾鏡，並預約「睡飽飽隔日登入獎勵 (+500竹幣、+10金竹種子)」，將健康休息轉化為正向反饋。

---

## 📂 專案架構目錄

```plaintext
wild_catcher_project/
├── app/src/main/
│   ├── AndroidManifest.xml              # 強制橫向 (sensorLandscape) 平板設定
│   ├── java/com/game/wildcatcher/
│   │   ├── WildCatcherApp.kt            # Application 進入點
│   │   ├── MainActivity.kt              # Android 橫向沉浸式主活動
│   │   ├── audio/
│   │   │   └── SoundManager.kt          # SoundPool 環繞音效與語音
│   │   ├── data/
│   │   │   ├── WildDatabase.kt          # Room Database 定義與型態轉換
│   │   │   ├── dao/                     # AnimalDao, PlayerDao, ShelterDao, QuizDao
│   │   │   ├── model/                   # 32種動物實體、玩家檔案、庇護園區、科普題庫
│   │   │   └── repository/              # WildRepository (全量 32 種動物數據注入)
│   │   ├── domain/
│   │   │   ├── capture/CaptureEngine.kt # QTE 捕捉計算、甜蜜點振盪、DDA 動態難度調節
│   │   │   ├── health/PlayTimeManager.kt# 30分鐘遊玩計時、波波護眼彈窗、隔日睡飽飽獎勵
│   │   │   ├── shelter/ShelterEconomyEngine.kt # 門票人流與收益計算
│   │   │   └── weather/WeatherDayNightEngine.kt # 日夜輪替 (晴空/雲霧/暴雨)
│   │   └── presentation/
│   │       ├── WildViewModel.kt         # 全局狀態與場景導航
│   │       ├── component/               # DaveNotebookFrame, QTECaptureBar, PandaAssistantOverlay, PbrShaderRenderer, EcoQuizDialog
│   │       ├── screen/                  # MainMenuScreen, CaptureBattleScreen, AdventureJournalScreen, ShelterManagementScreen, UpgradeShopScreen
│   │       └── theme/                   # WildTheme (皮質棕、泛黃紙張、竹綠色板)
│   └── res/                             # PBR 貼圖、drawable、values、xml
├── build.gradle.kts
└── settings.gradle.kts

# 伴隨即時體驗器 (可直接於任何瀏覽器雙擊開啟測試)
wild_catcher_preview.html
```

---

## 🚀 測試與執行方式

1. **Android Studio / Gradle 編譯**
   - 於 Android Studio 開啟 `wild_catcher_project`。
   - 連接 Android 平板或以橫向平板模擬器 (1920x1200 / 2560x1600) 點擊 Run 即可執行。

2. **跨平台即時預覽體驗**
   - 直接開啟根目錄下的 `wild_catcher_preview.html` 即可在瀏覽器中完整體驗：
     - 橫向平板比例介面
     - 熊貓槍 QTE 捕捉條與甜蜜點連續打擊
     - 點擊「⚡ 觸發30分提醒」查看護眼色溫模式與波波提醒彈窗
     - 查閱 32 種動物寫實圖鑑與 PBR 動態高光材質
