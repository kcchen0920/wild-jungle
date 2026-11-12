package com.game.wildcatcher.presentation.screen

import android.annotation.SuppressLint
import android.view.View
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.game.wildcatcher.presentation.theme.BrassHighlight
import com.game.wildcatcher.presentation.theme.LeatherDark
import com.game.wildcatcher.presentation.theme.VintageBrass

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun JungleExploreScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(top = 60.dp, end = 16.dp)
                .height(52.dp)
                .border(2.5.dp, VintageBrass, RoundedCornerShape(18.dp))
        ) {
            Text("🏠 回繪本首頁", color = BrassHighlight, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
