package com.perfumevault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perfumevault.R
import com.perfumevault.ui.theme.PerfumeVaultTheme

/**
 * Diese Komponente dient als Vorlage für die Play Store "Feature Graphic" (1024x500).
 * Du kannst in Android Studio einfach einen Screenshot von der Preview machen.
 */
@Composable
fun FeatureGraphic() {
    Box(
        modifier = Modifier
            .size(width = 1024.dp, height = 500.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C3E50),
                        Color(0xFF1A1A1B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Dekoratives Element im Hintergrund (Lichteffekt)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x11FFFFFF), Color.Transparent),
                        radius = 800f
                    )
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(40.dp)
        ) {
            // Das App-Logo (dein Flakon)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(48.dp))
                    .background(Color(0x11FFFFFF))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(48.dp))

            Column {
                Text(
                    text = "PerfumeVault",
                    color = Color.White,
                    fontSize = 84.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "YOUR PERSONAL FRAGRANCE COLLECTION",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 6.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1024, heightDp = 500)
@Composable
fun PreviewFeatureGraphic() {
    PerfumeVaultTheme(darkTheme = true) {
        FeatureGraphic()
    }
}
