package com.perfumevault.ui.components

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
import com.perfumevault.ui.theme.*

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    alpha: Float = 0.7f,
    content: @Composable BoxScope.() -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    val baseColor = adaptive.glassBase
    val borderColor = adaptive.glassBorder
    
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
    val adaptive = LocalAdaptiveColors.current

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
            .background(adaptive.glassBase.copy(alpha = if (adaptive.isDark) 0.6f else 0.92f))
            .border(
                width = 0.5.dp,
                color = adaptive.glassBorder,
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
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val adaptive = LocalAdaptiveColors.current
    val finalContainerColor = containerColor ?: adaptive.textPrimary
    val finalContentColor = contentColor ?: (if (adaptive.isDark) Color.Black else Color.White)
    
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
            containerColor = finalContainerColor,
            contentColor = finalContentColor
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default
) {
    val adaptive = LocalAdaptiveColors.current
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label.uppercase(), 
            fontSize = 10.sp, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 1.5.sp,
            color = adaptive.textPrimary.copy(alpha = 0.7f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(16.dp),
            placeholder = hint?.let { { Text(it, fontSize = 14.sp, color = adaptive.textSecondary.copy(alpha = 0.5f)) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = adaptive.textPrimary.copy(alpha = 0.03f),
                unfocusedContainerColor = adaptive.textPrimary.copy(alpha = 0.02f),
                focusedBorderColor = AppleAccentBlue,
                unfocusedBorderColor = adaptive.textPrimary.copy(alpha = 0.15f),
                focusedTextColor = adaptive.textPrimary,
                unfocusedTextColor = adaptive.textPrimary
            )
        )
    }
}

@Composable
fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) adaptive.textPrimary else adaptive.textPrimary.copy(alpha = 0.05f),
        contentColor = if (selected) (if (adaptive.isDark) Color.Black else Color.White) else adaptive.textPrimary.copy(alpha = 0.7f),
        modifier = Modifier.height(38.dp),
        border = if (!selected) BorderStroke(0.5.dp, adaptive.textPrimary.copy(alpha = 0.1f)) else null
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
    val adaptive = LocalAdaptiveColors.current
    Text(
        text = label,
        modifier = Modifier
            .background(adaptive.textPrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        fontSize = 11.sp,
        color = adaptive.textPrimary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun GlassToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
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
                color = adaptive.textPrimary, 
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = if (adaptive.isDark) Color.Black else Color.White,
                    checkedTrackColor = AppleAccentBlue,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = adaptive.textPrimary.copy(alpha = 0.05f),
                    uncheckedBorderColor = adaptive.textPrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
