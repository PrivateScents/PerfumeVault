package com.perfumevault.ui.components

import android.os.Build
import android.app.Dialog
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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
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
    alpha: Float = 0.35f,
    content: @Composable BoxScope.() -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    
    // In Dark Mode we use a more visible "frosted" color to pop against the dark background
    val baseColor = if (adaptive.isDark) Color(0xFF323235) else Color(0xFFF2F2F7)
    val finalAlpha = if (adaptive.isDark) alpha * 1.4f else alpha
    val borderColor = if (adaptive.isDark) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .graphicsLayer { clip = true; shape = RoundedCornerShape(cornerRadius) }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = finalAlpha),
                        baseColor.copy(alpha = finalAlpha * 0.7f)
                    )
                )
            )
            .border(
                width = 0.6.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.Transparent)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        // Real-time Element Blur (Android 12+) - Performance Optimized
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            40f, 40f, android.graphics.Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
            )
        }
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
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "cardScale"
    )
    val adaptive = LocalAdaptiveColors.current
    val baseColor = if (adaptive.isDark) Color(0xFF2C2C2E) else Color.White
    val borderColor = if (adaptive.isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.05f)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                clip = true
                shape = RoundedCornerShape(28.dp)
            }
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = if (adaptive.isDark) 0.4f else 0.7f),
                        baseColor.copy(alpha = if (adaptive.isDark) 0.25f else 0.4f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.Transparent)
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            50f, 50f, android.graphics.Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
            )
        }

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
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
    val finalContentColor = contentColor ?: adaptive.textPrimary
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "btnScale"
    )

    val baseColor = if (adaptive.isDark) Color(0xFF3A3A3C) else Color(0xFFF2F2F7)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                clip = true
                shape = RoundedCornerShape(28.dp)
            }
            .height(56.dp)
            .widthIn(min = 140.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap = { onClick() }
                )
            }
            .background(baseColor.copy(alpha = 0.6f))
            .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            30f, 30f, android.graphics.Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
            )
        }
        
        Text(
            text.uppercase(), 
            fontSize = 13.sp, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 2.sp,
            color = finalContentColor,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            color = adaptive.textPrimary.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(20.dp),
            placeholder = hint?.let { { Text(it, fontSize = 14.sp, color = adaptive.textSecondary.copy(alpha = 0.5f)) } },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
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
        color = if (selected) AppleAccentBlue else adaptive.textPrimary.copy(alpha = 0.05f),
        contentColor = if (selected) Color.White else adaptive.textPrimary.copy(alpha = 0.7f),
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

@Composable
fun GlassDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SideEffect {
            val window = (view.parent as? android.view.Window) 
                         ?: (view.parent as? Dialog)?.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window?.setBackgroundBlurRadius(60)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp, top = 32.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.82f),
                cornerRadius = 32.dp,
                alpha = 0.85f
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun GlassAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null
) {
    GlassDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title?.let { 
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), contentAlignment = Alignment.Center) { it() } 
            }
            text?.let { 
                Box(modifier = Modifier.weight(1f, fill = false)) { it() }
            }
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                confirmButton()
                dismissButton?.let { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        it()
                    }
                }
            }
        }
    }
}
