# 野性捕捉大師 (Wild Catcher: Jungle & Sky)

專為 **Android 平板 (強制橫向 16:10 / 16:9 顯示)** 設計之橫向動態野生動物捕捉、生態探索與輕量庇護所經營遊戲。以《潛水員戴夫》精緻圖文冒險筆記風格為視覺導向，搭載擬真 PBR 材質光影渲染、動態難度調節 (DDA) 雙手幼兒指肌訓練 QTE 捕捉條與 30 分鐘健康護眼機制。

---

## 🎯 幼兒教育目標與核心功能特色

### 1. 幼兒雙手雙側協同與精細動作訓練 (Bimanual Early Motor Training)
- **左手側（持續施力與本體感覺穩定度）**：
  - 設計超大圓形安撫壓力閥（支援大拇指與掌心持續穩定長按）。
  - 當左手穩定施力時，野生動物掙扎震盪減緩 50%，訓練幼兒非慣用手的肌肉耐力與穩定控制。
- **右手側（指尖獨立性與節奏靈巧度）**：
  - 設計回彈手感按鈕，支援食指/雙指交替連擊（像小跑步一般交替點擊收網）。
  - 訓練指尖分離度與敏捷度，命中綠色甜蜜區時觸發「微物理彈性壓縮形變 (Squish & Stretch Physics)」與清脆泡泡音效。
- **雙手協同共振加成**：
  - 當左手穩住、右手同時節奏點擊時，收網效率獲得 **+45% 協同加成**，即時呈現「小肌肉活力評級（🌟 雙手協同大師）」，讓幼兒在成就感中完成雙側神經整合。

### 2. 《潛水員戴夫》風格美學介面 (UI/UX)
- 仿皮質封面、泛黃紙張質感的圖文升級介面 (`DaveNotebookFrame`)。
- 具有清脆翻頁與木質碰撞反饋音效，搭配小熊貓「波波」溫馨語音氣泡指引。

### 3. 100% 全量收錄 32 種寫實野生動物生態與互動圖鑑
- 完整收錄台灣黑熊、石虎、櫻花鉤吻鮭、穿山甲、帝雉、川金絲猴、大冠鷲等 32 種真實物種。
- 保育等級紅黃綠三色燈號、真實叫聲、生活習性與自然科普知識。

### 4. 自然生態庇護所經營 (Shelter Economy)
- 熱帶雨林、高山針葉林、晴空猛禽、清溪活水四大生態區。
- 安置捕獲動物產生門票收益，持續升級熊貓槍氣壓馬達與彈藥容量。

### 5. 30 分鐘健康遊玩提醒與護眼模式 (Panda's Rest Time)
- 連續遊玩滿 30 分鐘，熊貓助手波波溫和提示：「*太陽要下山囉，讓眼睛休息一下，明天再來探索！*」
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
│   │   │   ├── capture/CaptureEngine.kt # 雙手協同評分、左手施力穩定、右手節奏點擊、彈性物理形變
│   │   │   ├── health/PlayTimeManager.kt# 30分鐘遊玩計時、波波護眼彈窗、隔日睡飽飽獎勵
│   │   │   ├── shelter/ShelterEconomyEngine.kt # 門票人流與收益計算
│   │   │   └── weather/WeatherDayNightEngine.kt # 日夜輪替 (晴空/雲霧/暴雨)
│   │   └── presentation/
│   │       ├── WildViewModel.kt         # 全局狀態與場景導航
│   │       ├── component/
│   │       │   ├── QTECaptureBar.kt     # 幼兒雙手協同專用控制器 (左手按壓+右手連擊)
│   │       │   ├── DaveNotebookFrame.kt # 戴夫風格皮質筆記本外框
│   │       │   ├── PbrShaderRenderer.kt # PBR 寫實材質與即時光影渲染
│   │       │   ├── PandaAssistantOverlay.kt # 波波助手語音氣泡與健康提醒
│   │       │   └── EcoQuizDialog.kt     # 自然科普小學堂互動題庫
│   │       ├── screen/                  # MainMenu, CaptureBattle, Journal, Shelter, Shop
│   │       └── theme/                   # WildTheme (皮質棕、泛黃紙張、竹綠色板)
│   └── res/                             # PBR 貼圖、drawable、values、xml
├── build.gradle.kts
└── settings.gradle.kts

# 伴隨即時體驗器 (可直接於任何瀏覽器雙擊開啟測試雙手操作與物理彈性)
wild_catcher_preview.html
```

---

## 🚀 測試與執行方式

1. **Android 平板設備執行**
   - 於 Android Studio 開啟 `wild_catcher_project`，在 Android 橫向平板上點擊 Run。
2. **瀏覽器雙手操作即時驗證**
   - 雙擊開啟 `wild_catcher_preview.html`：
     - 左側按鈕【長按住】測試左手持續施力穩定游標（游標晃動速度立即降低 50%）。
     - 右側按鈕【節奏輕點】測試右手敏捷連擊與動物彈簧擠壓形變 (Squish & Stretch)。
     - 觀察上方「雙手協調評級」由「探索小萌手」晉升為「🌟 雙手協同大師」。
