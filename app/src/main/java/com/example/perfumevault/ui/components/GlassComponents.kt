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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Apple Modern Palette
val AppleTextBlack = Color(0xFF1D1D1F)
val AppleTextSecondary = Color(0xFF86868B)
val AppleAccentBlue = Color(0xFF007AFF)
val AppleBackgroundLight = Color(0xFFF5F5F7)
val AppleBackgroundDark = Color(0xFF000000)

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    alpha: Float = 0.4f,
    isDarkMode: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val baseColor = if (isDarkMode) Color(0xFF1C1C1E) else Color.White
    val borderColor = if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = alpha),
                        baseColor.copy(alpha = alpha * 0.7f)
                    )
                )
            )
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(cornerRadius),
            color = Color.Transparent,
            border = BorderStroke(
                0.5.dp, 
                borderColor
            )
        ) {}
        content()
    }
}

@Composable
fun PressableGlassCard(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
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
        alpha = if (isDarkMode) 0.3f else 0.4f,
        isDarkMode = isDarkMode
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
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "btnScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(56.dp)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false }, onTap = { onClick() })
            },
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp)
    }
}

@Composable
fun AnimatedRatingBar(
    rating: Double, // Change to Double
    modifier: Modifier = Modifier,
    maxRating: Int = 10,
    isDarkMode: Boolean = false
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    .background((if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = animatedAlpha))
                    .align(Alignment.CenterVertically)
            )
        }
        
        Spacer(Modifier.width(4.dp))
        Text(
            "%.1f".format(rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else AppleTextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String? = null, // Add optional hint
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isDarkMode: Boolean = false,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(label, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                if (hint != null) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        hint,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.4f) else AppleTextSecondary.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = (if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.8f),
            unfocusedContainerColor = (if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.5f),
            focusedBorderColor = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.3f),
            unfocusedBorderColor = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.1f),
            focusedTextColor = if (isDarkMode) Color.White else AppleTextBlack,
            unfocusedTextColor = if (isDarkMode) Color.White else AppleTextBlack,
            focusedLabelColor = if (isDarkMode) Color.White else AppleTextBlack,
            unfocusedLabelColor = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary
        )
    )
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
    isDarkMode: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "chipScale"
    )
    
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) (if (isDarkMode) Color.White else AppleTextBlack) else (if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.6f)),
        contentColor = if (selected) (if (isDarkMode) Color.Black else Color.White) else (if (isDarkMode) Color.White else AppleTextBlack),
        modifier = Modifier.scale(scale),
        border = if (!selected) BorderStroke(1.dp, (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.1f)) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogChip(label: String, isDarkMode: Boolean = false) {
    Text(
        text = label,
        modifier = Modifier
            .background(
                if (isDarkMode) Color.White.copy(alpha = 0.1f) else AppleTextBlack.copy(alpha = 0.05f), 
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        fontSize = 11.sp,
        color = if (isDarkMode) Color.White else AppleTextBlack,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun GlassToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkMode: Boolean = false
) {
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        alpha = if (isDarkMode) 0.2f else 0.4f,
        isDarkMode = isDarkMode
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
                color = if (isDarkMode) Color.White else AppleTextBlack, 
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppleAccentBlue,
                    uncheckedThumbColor = if (isDarkMode) Color.White.copy(alpha = 0.4f) else Color.Gray,
                    uncheckedTrackColor = Color.Transparent
                )
            )
        }
    }
}
