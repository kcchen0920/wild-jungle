package com.game.wildcatcher.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.wildcatcher.presentation.theme.*

@Composable
fun DaveNotebookFrame(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LeatherDark)
            .padding(16.dp)
    ) {
        // Outer Leather Bound Cover with brass border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(12.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(LeatherBrown, LeatherDark)
                    )
                )
                .border(3.dp, VintageBrass, RoundedCornerShape(18.dp))
                .padding(12.dp)
        ) {
            // Aged Parchment Paper Inner Layer
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ParchmentBg)
                    .border(1.dp, ParchmentBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Header Bar (Dave the Diver notebook style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(VintageBrass)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                style = WildTypography.headlineMedium.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = LeatherDark
                                )
                            )
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = WildTypography.bodyMedium.copy(
                                    color = InkGrey,
                                    fontSize = 13.sp
                                ),
                                modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                            )
                        }
                    }

                    // Action buttons / tabs
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        content = actions
                    )
                }

                // Decorative Divider Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    VintageBrass.copy(alpha = 0.2f),
                                    VintageBrass,
                                    VintageBrass.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Main Page Content
                content()
            }
        }
    }
}
