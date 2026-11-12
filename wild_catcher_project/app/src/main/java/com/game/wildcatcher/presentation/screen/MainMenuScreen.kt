package com.game.wildcatcher.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.data.model.PlayerProfile
import com.game.wildcatcher.presentation.theme.*

@Composable
fun MainMenuScreen(
    profile: PlayerProfile,
    capturedAnimalsCount: Int = profile.totalCaptures,
    onStartExpedition: () -> Unit,
    onOpenJungleExplore: () -> Unit = {},
    onOpenShelter: () -> Unit,
    onOpenJournal: () -> Unit,
    onOpenShop: () -> Unit,
    onTriggerQuiz: () -> Unit,
    onSimulateThirtyMinutes: () -> Unit,
    onOpenParentDashboard: () -> Unit = {},
    onSpeakVoice: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Gentle breathing animation for primary toddler action buttons
    val infiniteTransition = rememberInfiniteTransition(label = "toddler_menu_pulse")
    val heroButtonScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroScale"
    )

    var showParentHint by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LeatherDark)
            .padding(18.dp)
    ) {
        // Main Story-Book Frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(16.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF5D3A24), Color(0xFF23140E)),
                        radius = 1200f
                    )
                )
                .border(4.dp, VintageBrass, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // =========================================================================
                // LEFT PANEL: Toddler Visual Companion Card (Icon-First, De-cluttered Text)
                // =========================================================================
                Column(
                    modifier = Modifier
                        .weight(0.36f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ParchmentCard)
                        .border(2.dp, ParchmentBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Big Friendly Character Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(ParchmentBg)
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(BambooGreen)
                                    .border(3.dp, VintageBrass, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🤠", fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "小獵人 戴夫",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = LeatherDark
                                )
                                Text(
                                    text = "⭐ 森林好朋友探險家",
                                    fontSize = 13.sp,
                                    color = BambooGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Giant Visual Treasure Counters (No tiny text tables!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Bamboo Coins Visual Pill
                            Card(
                                onClick = {
                                    onSpeakVoice("你有 ${profile.coins} 個快樂竹幣喔！可以去換彩虹道具！")
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                border = BorderStroke(2.dp, SunsetAmber),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(78.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("🎋💰", fontSize = 24.sp)
                                    Text(
                                        text = "${profile.coins}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = SunsetAmber
                                    )
                                }
                            }

                            // Animal Stickers Visual Pill
                            Card(
                                onClick = {
                                    onSpeakVoice("你已經收集了 $capturedAnimalsCount 張動物好朋友貼紙囉！")
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(2.dp, BambooGreen),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(78.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("🐾⭐", fontSize = 24.sp)
                                    Text(
                                        text = "$capturedAnimalsCount 隻",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = BambooGreen
                                    )
                                }
                            }
                        }

                        // Interactive Bobo Voice Companion Box (Tap to Hear Voice!)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF9C4))
                                .border(2.dp, SunsetAmber, RoundedCornerShape(16.dp))
                                .clickable {
                                    onSpeakVoice("嗨！我是小熊貓波波！點綠色大按鈕，我們一起去森林吹大泡泡、交動物好朋友吧！")
                                }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🐼", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "點波波聽說話",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = LeatherDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🔊", fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "「點大圖案就能出發去森林玩喔！」",
                                        fontSize = 13.sp,
                                        color = InkBlack,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Subtle Parent Gate (Anti-Accidental-Touch for 4yo)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (showParentHint) {
                            Text(
                                text = "🔒 家長請長按按鈕 1 秒開啟",
                                fontSize = 11.sp,
                                color = DangerCoral,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFD7CCC8))
                                    .border(1.dp, ParchmentBorder, RoundedCornerShape(10.dp))
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { showParentHint = true },
                                            onLongPress = {
                                                showParentHint = false
                                                onSimulateThirtyMinutes()
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👀 護眼測試(長按)", fontSize = 11.sp, color = InkGrey, fontWeight = FontWeight.SemiBold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF5D4037))
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                // Parent Dashboard already has arithmetic lock inside
                                                onOpenParentDashboard()
                                            },
                                            onLongPress = {
                                                onOpenParentDashboard()
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛡️ 家長專區", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // =========================================================================
                // RIGHT PANEL: Giant Picture-Book Play Cards for 4-Year-Olds
                // =========================================================================
                Column(
                    modifier = Modifier
                        .weight(0.64f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎋", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "野性捕捉大師",
                                style = WildTypography.headlineLarge.copy(
                                    fontSize = 30.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrassHighlight
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(BambooGreen.copy(alpha = 0.35f))
                                .border(1.5.dp, BambooLight, RoundedCornerShape(14.dp))
                                .clickable {
                                    onSpeakVoice("歡迎來到野性捕捉大師！想要吹泡泡抓動物，還是坐探險車逛森林呢？")
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("🔊 語音導覽", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 1. GIANT HERO BUTTON: Blow Bubbles & Befriend Animals
                    Button(
                        onClick = onStartExpedition,
                        colors = ButtonDefaults.buttonColors(containerColor = BambooGreen),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(78.dp)
                            .scale(heroButtonScale)
                            .border(3.dp, BrassHighlight, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🫧🐼", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "吹泡泡 · 找小動物玩！",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "左手按住抱抱 · 右手拍拍吹大泡泡！",
                                        fontSize = 13.sp,
                                        color = BambooLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(BrassHighlight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("▶", fontSize = 24.sp, color = LeatherDark, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    // 2. INTERACTIVE JUNGLE SAFARI BUS (3800x2600 Tap-to-Move Map)
                    Button(
                        onClick = {
                            onSpeakVoice("坐上探險車，點點森林地面就能自動走過去找動物喔！")
                            onOpenJungleExplore()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00838F)),
                        shape = RoundedCornerShape(18.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp)
                            .border(2.dp, Color(0xFF80DEEA), RoundedCornerShape(18.dp))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🚙🌴", fontSize = 30.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "逛大森林地圖 (一指點地自動走)",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "在超大叢林裡散步、點動物直接吹泡泡！",
                                        fontSize = 12.sp,
                                        color = Color(0xFFE0F7FA)
                                    )
                                }
                            }
                            Text("GO 🐾", fontWeight = FontWeight.ExtraBold, color = BrassHighlight, fontSize = 18.sp)
                        }
                    }

                    // 3. 2x2 GIANT ICON CARDS: Shelter Park, Sticker Book, Rainbow Shop, Audio Quiz
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Shelter Park Card
                            Card(
                                onClick = {
                                    onSpeakVoice("歡迎來到動物好朋友樂園！快來餵小動物吃紅蘋果吧！")
                                    onOpenShelter()
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                border = BorderStroke(2.5.dp, SunsetAmber),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🏡🍎", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "動物遊樂園",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = LeatherDark
                                        )
                                        Text(
                                            text = "餵蘋果 · 摸摸頭",
                                            fontSize = 12.sp,
                                            color = SunsetAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Audio Sticker Book Card
                            Card(
                                onClick = {
                                    onSpeakVoice("打開彩色有聲貼紙簿，點貼紙聽動物叫聲和故事喔！")
                                    onOpenJournal()
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                border = BorderStroke(2.5.dp, Color(0xFF29B6F6)),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📖🔊", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "有聲貼紙簿",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = LeatherDark
                                        )
                                        Text(
                                            text = "點貼紙 · 聽故事",
                                            fontSize = 12.sp,
                                            color = Color(0xFF0277BD),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Rainbow Toy Shop
                            Card(
                                onClick = {
                                    onSpeakVoice("歡迎來到彩虹道具屋！換一把超酷的彩虹泡泡槍吧！")
                                    onOpenShop()
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                                border = BorderStroke(2.5.dp, Color(0xFFAB47BC)),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🌈🔫", fontSize = 30.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "彩虹道具屋",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = LeatherDark
                                        )
                                        Text(
                                            text = "升級魔法泡泡槍",
                                            fontSize = 12.sp,
                                            color = Color(0xFF7B1FA2),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Voice Animal Quiz
                            Card(
                                onClick = onTriggerQuiz,
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                border = BorderStroke(2.5.dp, BambooGreen),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🌟❓", fontSize = 30.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "動物大猜謎",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = LeatherDark
                                        )
                                        Text(
                                            text = "聽問題 · 拿竹幣",
                                            fontSize = 12.sp,
                                            color = BambooGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
