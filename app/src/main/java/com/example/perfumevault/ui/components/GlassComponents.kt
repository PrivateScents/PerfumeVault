package com.example.perfumevault.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Luxury Modern Palette
val AppleTextBlack = Color(0xFF1D1D1F)
val AppleTextSecondary = Color(0xFF86868B)
val AppleAccentBlue = Color(0xFF007AFF)
val GoldAccent = Color(0xFFD4AF37)

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    alpha: Float = 0.7f,
    content: @Composable BoxScope.() -> Unit
) {
    val baseColor = Color.White
    val borderColor = Color.Black.copy(alpha = 0.08f)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(baseColor.copy(alpha = alpha))
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(cornerRadius),
            color = Color.Transparent,
            border = BorderStroke(0.5.dp, borderColor)
        ) {}
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "cardScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(28.dp))
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() },
                            onLongPress = { onLongClick?.invoke() }
                        )
                    }
                } else Modifier
            )
            .background(Color.White.copy(alpha = 0.92f))
            .border(
                width = 0.5.dp,
                color = Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun PressableGlassCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "scale"
    )

    GlassSurface(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        cornerRadius = 28.dp,
        alpha = 0.4f
    ) {
        content()
    }
}

@Composable
fun HighVisibilityButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = AppleTextBlack,
    contentColor: Color = Color.White
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "btnScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(54.dp)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false }, onTap = { onClick() })
            },
        shape = RoundedCornerShape(27.dp), // Pill shape
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) {
        Text(
            text.uppercase(), 
            fontSize = 13.sp, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun AnimatedRatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    maxRating: Int = 10
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(maxRating) { index ->
            val fillAmount = (rating - index).coerceIn(0.0, 1.0)
            val animatedAlpha by animateFloatAsState(
                targetValue = if (fillAmount > 0) 1f else 0.15f,
                animationSpec = tween(300, delayMillis = index * 20),
                label = "alpha"
            )
            
            Box(
                modifier = Modifier
                    .size(if (fillAmount >= 1.0) 7.dp else if (fillAmount > 0) 6.dp else 5.dp)
                    .clip(CircleShape)
                    .background(AppleTextBlack.copy(alpha = animatedAlpha))
            )
        }
        
        Spacer(Modifier.width(6.dp))
        Text(
            "%.1f".format(rating),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = AppleAccentBlue
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label.uppercase(), 
            fontSize = 10.sp, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 1.5.sp,
            color = AppleTextBlack.copy(alpha = 0.7f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(16.dp),
            placeholder = hint?.let { { Text(it, fontSize = 14.sp, color = Color.Gray.copy(alpha = 0.5f)) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Black.copy(alpha = 0.03f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.02f),
                focusedBorderColor = AppleAccentBlue,
                unfocusedBorderColor = Color.Black.copy(alpha = 0.15f),
                focusedTextColor = AppleTextBlack,
                unfocusedTextColor = AppleTextBlack
            )
        )
    }
}

@Composable
fun StatChip(label: String, value: String) {
    GlassSurface(
        modifier = Modifier,
        cornerRadius = 20.dp,
        alpha = 0.6f
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = AppleTextBlack)
            Text(label, fontSize = 11.sp, color = AppleTextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) AppleTextBlack else Color.Black.copy(alpha = 0.05f),
        contentColor = if (selected) Color.White else AppleTextBlack.copy(alpha = 0.7f),
        modifier = Modifier.height(38.dp),
        border = if (!selected) BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.1f)) else null
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun LogChip(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        fontSize = 11.sp,
        color = AppleTextBlack,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun GlassToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        alpha = 0.4f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label, 
                color = AppleTextBlack, 
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppleAccentBlue,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Black.copy(alpha = 0.05f),
                    uncheckedBorderColor = Color.Black.copy(alpha = 0.1f)
                )
            )
        }
    }
}
