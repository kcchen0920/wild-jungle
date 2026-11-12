package com.game.wildcatcher.presentation.screen

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.game.wildcatcher.presentation.theme.BrassHighlight
import com.game.wildcatcher.presentation.theme.LeatherDark
import com.game.wildcatcher.presentation.theme.VintageBrass

private class AndroidJungleBridge(
    private val coinsProvider: () -> Long,
    private val capturedProvider: () -> Int,
    private val onAnimalEncounter: (String) -> Unit,
    private val onCrateRewardCallback: (Int, String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun getPlayerCoins(): Long = coinsProvider()

    @JavascriptInterface
    fun getCapturedCount(): Int = capturedProvider()

    @JavascriptInterface
    fun onAnimalHitByBubble(animalName: String) {
        mainHandler.post {
            onAnimalEncounter(animalName)
        }
    }

    @JavascriptInterface
    fun onCrateReward(coins: Int, crateName: String) {
        mainHandler.post {
            onCrateRewardCallback(coins, crateName)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun JungleExploreScreen(
    playerCoins: Long = 0L,
    capturedAnimalsCount: Int = 0,
    onEncounterAnimal: (String) -> Unit = {},
    onCrateReward: (Long, String) -> Unit = { _, _ -> },
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentCoins by rememberUpdatedState(playerCoins)
    val currentCaptured by rememberUpdatedState(capturedAnimalsCount)
    val currentOnEncounter by rememberUpdatedState(onEncounterAnimal)
    val currentOnCrate by rememberUpdatedState(onCrateReward)

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        mediaPlaybackRequiresUserGesture = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        databaseEnabled = true
                    }
                    addJavascriptInterface(
                        AndroidJungleBridge(
                            coinsProvider = { currentCoins },
                            capturedProvider = { currentCaptured },
                            onAnimalEncounter = { name -> currentOnEncounter(name) },
                            onCrateRewardCallback = { coins, crateName ->
                                currentOnCrate(coins.toLong(), crateName)
                            }
                        ),
                        "AndroidJungleBridge"
                    )
                    webChromeClient = WebChromeClient()
                    webViewClient = WebViewClient()
                    loadUrl("file:///android_asset/jungle/index.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Giant Floating Home Button for 4-Year-Olds
        Button(
            onClick = onBackToMenu,
            colors = ButtonDefaults.buttonColors(containerColor = LeatherDark.copy(alpha = 0.92f)),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 18.dp)
                .height(54.dp)
                .border(2.5.dp, VintageBrass, RoundedCornerShape(18.dp))
        ) {
            Text("🏠 回繪本首頁", color = BrassHighlight, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
