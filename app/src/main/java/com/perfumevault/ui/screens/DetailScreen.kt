package com.perfumevault.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.perfumevault.BuildConfig
import com.perfumevault.data.Perfume
import com.perfumevault.ui.components.*
import com.perfumevault.ui.dialogs.AddLogDialog
import com.perfumevault.ui.theme.LocalAdaptiveColors
import com.perfumevault.ui.theme.GoldAccent
import com.perfumevault.ui.theme.BlueSlate
import com.perfumevault.viewmodel.PerfumeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    perfumeId: String,
    viewModel: PerfumeViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val perfume by viewModel.getPerfumeById(perfumeId).collectAsState(initial = null)
    val logs by viewModel.getLogsForPerfume(perfumeId).collectAsState(initial = emptyList())
    val sizes by viewModel.getSizesForPerfume(perfumeId).collectAsState(initial = emptyList())
    val adaptive = LocalAdaptiveColors.current

    if (perfume == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = adaptive.textPrimary)
        }
        return
    }

    val currentPerfume = perfume!!
    var showLogDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val remainingMl = currentPerfume.remainingMl.coerceIn(0.0, currentPerfume.bottleSize.toDouble())

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            null, 
                            tint = adaptive.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, null, tint = adaptive.textPrimary)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, null, tint = adaptive.textSecondary.copy(alpha = 0.5f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            if (!currentPerfume.isWishlist) {
                HighVisibilityButton(
                    text = viewModel.t("Heute getragen", "Worn Today"),
                    onClick = { showLogDialog = true },
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        val bottomPadding = if (BuildConfig.FLAVOR == "public") 200.dp else 120.dp
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Image Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(adaptive.textPrimary.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentPerfume.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = currentPerfume.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                alignment = Alignment.Center
                            )
                        } else {
                            Icon(
                                Icons.Default.Image, 
                                null, 
                                modifier = Modifier.size(80.dp), 
                                tint = adaptive.textPrimary.copy(alpha = 0.1f)
                            )
                        }
                        
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentPerfume.isSample) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.Black.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Science, null, tint = BlueSlate, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (currentPerfume.isFavorite) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color.Black.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Star, null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    
                    Text(
                        currentPerfume.brand.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = adaptive.textPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        currentPerfume.name,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = adaptive.textPrimary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 48.sp,
                        letterSpacing = (-1.5).sp
                    )
                    Spacer(Modifier.height(12.dp))
                    // Clean dark rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = adaptive.textPrimary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "%.1f".format(currentPerfume.rating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = adaptive.textPrimary
                        )
                    }
                }
            }

            // Stats Row & Progress Bar
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    val percent = remember(remainingMl, currentPerfume.bottleSize) {
                        percentage(remainingMl, currentPerfume.bottleSize)
                    }
                    val progressColor = remember(percent) {
                        when {
                            percent >= 20 -> Color(0xFF4CAF50) // Green
                            percent >= 10 -> Color(0xFFFF9800) // Orange
                            else -> Color(0xFFF44336) // Red
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoTile(
                            modifier = Modifier.weight(1f),
                            label = viewModel.t("Füllstand", "Volume"),
                            value = "$percent%"
                        )
                        InfoTile(
                            modifier = Modifier.weight(1f),
                            label = viewModel.t("Wert", "Value"),
                            value = "€${"%.0f".format(currentPerfume.price)}"
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Label for Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            viewModel.t("DETAILS ZUM FÜLLSTAND", "VOLUME DETAILS"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = adaptive.textPrimary.copy(alpha = 0.4f)
                        )
                        Text(
                            "${"%.1f".format(remainingMl)} / ${currentPerfume.bottleSize} ml",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = adaptive.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(adaptive.textPrimary.copy(alpha = 0.05f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percent / 100f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(progressColor)
                        )
                    }
                }
            }

            // Details Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionLabel(viewModel.t("CHARAKTERISTIK", "CHARACTERISTICS"))
                    GlassCard {
                        DetailRow(viewModel.t("Saison", "Season"), viewModel.translateSeason(currentPerfume.season))
                        Spacer(Modifier.height(16.dp))
                        DetailRow(viewModel.t("Anlass", "Occasion"), viewModel.translateOccasion(currentPerfume.occasion))
                    }
                    
                    if (sizes.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        SectionLabel(viewModel.t("VERFÜGBARE GRÖSSEN", "AVAILABLE SIZES"))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(sizes) { size ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(adaptive.textPrimary.copy(alpha = 0.05f))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        "${size.ml} ml",
                                        color = adaptive.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notes Section
            if (currentPerfume.notes.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionLabel(viewModel.t("NOTIZEN", "NOTES"))
                        Text(
                            currentPerfume.notes,
                            fontSize = 16.sp,
                            color = adaptive.textPrimary.copy(alpha = 0.7f),
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Timeline
            if (logs.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionLabel(viewModel.t("TIMELINE", "TIMELINE"))
                        logs.take(10).forEach { log ->
                            TimelineItem(log)
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        GlassAlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(viewModel.t("Löschen?", "Delete?"), fontWeight = FontWeight.Bold, color = adaptive.textPrimary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deletePerfume(currentPerfume); onDelete() }) {
                    Text(viewModel.t("Löschen", "Delete"), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary)
                }
            }
        )
    }
    
    if (showLogDialog) {
        AddLogDialog(
            perfumeName = currentPerfume.name,
            currentRemainingMl = remainingMl,
            viewModel = viewModel,
            onDismiss = { showLogDialog = false },
            onSave = { occasion, weather, note, sprays ->
                viewModel.addLog(currentPerfume.id, occasion, weather, note, sprays)
                showLogDialog = false
            }
        )
    }

    if (showEditDialog) {
        com.perfumevault.ui.dialogs.EditPerfumeDialog(
            perfume = currentPerfume,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                val wasWishlist = currentPerfume.isWishlist
                val isWishlist = updated.isWishlist
                viewModel.updatePerfume(updated)
                showEditDialog = false
                if (!wasWishlist && isWishlist) { onBack(); viewModel.setTab(1) }
            }
        )
    }
}

private fun percentage(current: Double, total: Int): Int = (current / total.toDouble() * 100).toInt().coerceIn(0, 100)

@Composable
fun InfoTile(modifier: Modifier, label: String, value: String) {
    val adaptive = LocalAdaptiveColors.current
    GlassSurface(
        modifier = modifier.height(80.dp),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = adaptive.textPrimary.copy(alpha = 0.4f), letterSpacing = 1.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = adaptive.textPrimary)
        }
    }
}

@Composable
fun TimelineItem(log: com.perfumevault.data.UsageLog) {
    val adaptive = LocalAdaptiveColors.current
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(log.date, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = adaptive.textPrimary)
            Text("${log.sprays} sprays", fontSize = 14.sp, color = adaptive.textPrimary.copy(alpha = 0.5f))
        }
        if (log.note.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(log.note, fontSize = 13.sp, color = adaptive.textPrimary.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    val adaptive = LocalAdaptiveColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = adaptive.textSecondary, fontSize = 13.sp)
        Text(value, color = adaptive.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionLabel(text: String) {
    val adaptive = LocalAdaptiveColors.current
    Text(
        text,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp,
        color = adaptive.textPrimary.copy(alpha = 0.9f),
        modifier = Modifier.padding(start = 4.dp)
    )
}
