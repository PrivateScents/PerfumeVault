package com.perfumevault.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perfumevault.ui.theme.AppleAccentBlue
import com.perfumevault.viewmodel.PerfumeViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FeatureTour(
    viewModel: PerfumeViewModel,
    step: Int,
    addBtnRect: Rect,
    navRects: Map<Int, Rect>,
    onNext: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    if (step == -1) {
        WelcomeScreen(viewModel, onNext)
        return
    }

    if (step > 4) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    val targetRect = when(step) {
        0 -> addBtnRect
        1 -> navRects[0] ?: Rect.Zero
        2 -> navRects[2] ?: Rect.Zero
        3 -> navRects[3] ?: Rect.Zero
        4 -> navRects[4] ?: Rect.Zero
        else -> Rect.Zero
    }

    // Smoothly animate the spotlight
    val left by animateFloatAsState(targetValue = targetRect.left, animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow), label = "left")
    val top by animateFloatAsState(targetValue = targetRect.top, animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow), label = "top")
    val right by animateFloatAsState(targetValue = targetRect.right, animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow), label = "right")
    val bottom by animateFloatAsState(targetValue = targetRect.bottom, animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow), label = "bottom")
    val animatedRect = Rect(left, top, right, bottom)

    Box(modifier = Modifier.fillMaxSize()) {
        // Spotlight Background (Draws overlay but doesn't consume clicks outside info card)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spotlightPath = Path().apply {
                if (animatedRect != Rect.Zero) {
                    addRoundRect(
                        RoundRect(
                            rect = animatedRect.inflate(8.dp.toPx()),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    )
                }
            }
            
            clipPath(spotlightPath, clipOp = ClipOp.Difference) {
                drawRect(Color.Black.copy(alpha = 0.7f))
            }
        }

        // Info Card
        val config = LocalConfiguration.current
        val density = LocalDensity.current
        val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }
        val isBottom = targetRect.top < screenHeightPx / 2
        
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (fadeIn(tween(600, 200)) + slideInVertically { it / 4 }).togetherWith(
                        fadeOut(tween(400)) + slideOutVertically { -it / 4 }
                    )
                },
                modifier = Modifier
                    .align(if (isBottom) Alignment.BottomCenter else Alignment.TopCenter)
                    .padding(horizontal = 32.dp, vertical = 130.dp),
                label = "InfoCardTransition"
            ) { currentStep ->
                Surface(
                    color = if (isDarkMode) Color(0xFF2C2C2E) else Color.White,
                    shape = RoundedCornerShape(28.dp),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val title = when(currentStep) {
                            0 -> viewModel.t("Düfte hinzufügen", "Add Fragrances")
                            1 -> viewModel.t("Deine Sammlung", "Your Collection")
                            2 -> viewModel.t("Nutzungstagebuch", "Usage Diary")
                            3 -> viewModel.t("Statistiken", "Statistics")
                            4 -> viewModel.t("Einstellungen", "Settings")
                            else -> ""
                        }
                        val description = when(currentStep) {
                            0 -> viewModel.t("Hier kannst du neue Parfüms manuell oder über den Katalog hinzufügen.", "Here you can add new perfumes manually or via the catalog.")
                            1 -> viewModel.t("Hier findest du all deine Schätze. Tippe auf einen Flakon für Details.", "Find all your treasures here. Tap a bottle for details.")
                            2 -> viewModel.t("Dokumentiere täglich deine getragenen Düfte und sammle Erinnerungen.", "Document your daily scents and collect memories.")
                            3 -> viewModel.t("Sieh dir an, welche Marken du am meisten nutzt und wie viel deine Sammlung wert ist.", "See which brands you use most and how much your collection is worth.")
                            4 -> viewModel.t("Passe das Design an, ändere die Sprache oder exportiere deine Daten.", "Customize the design, change the language, or export your data.")
                            else -> ""
                        }

                        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = description, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp, color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = onNext,
                            colors = ButtonDefaults.buttonColors(containerColor = AppleAccentBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text(if (currentStep == 4) viewModel.t("Verstanden", "Got it") else viewModel.t("Weiter", "Next"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(if (isBottom) Alignment.BottomCenter else Alignment.TopCenter)
                    .padding(bottom = if (isBottom) 70.dp else 0.dp, top = if (isBottom) 0.dp else 70.dp)
            ) {
                Text(viewModel.t("Tour beenden", "End Tour"), color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

fun Rect.inflate(delta: Float): Rect {
    return Rect(left - delta, top - delta, right + delta, bottom + delta)
}

@Composable
private fun WelcomeScreen(viewModel: PerfumeViewModel, onStart: () -> Unit) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color.Black.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.96f)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onStart() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(64.dp), tint = AppleAccentBlue)
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = viewModel.t("Willkommen bei\nPerfumeVault", "Welcome to\nPerfumeVault"), fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, lineHeight = 42.sp, color = if (isDarkMode) Color.White else Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = viewModel.t("Dein persönliches Archiv für exklusive Düfte.", "Your personal archive for exclusive fragrances."), fontSize = 17.sp, textAlign = TextAlign.Center, color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(56.dp))
            Button(onClick = onStart, colors = ButtonDefaults.buttonColors(containerColor = AppleAccentBlue), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().height(60.dp)) {
                Text(viewModel.t("Tour starten", "Start Tour"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }
}
