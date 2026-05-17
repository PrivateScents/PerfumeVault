package com.example.perfumevault.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.ui.dialogs.AddLogDialog
import com.example.perfumevault.viewmodel.PerfumeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    perfumeId: Int,
    viewModel: PerfumeViewModel,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    val perfume by viewModel.getPerfumeById(perfumeId).collectAsState(initial = null)
    val logs by viewModel.getLogsForPerfume(perfumeId).collectAsState(initial = emptyList())
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    if (perfume == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = if (isDarkMode) Color.White else AppleTextBlack)
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
                title = { 
                    Text(
                        currentPerfume.brand.uppercase(), 
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        letterSpacing = 1.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = null, 
                            tint = if (isDarkMode) Color.White else AppleTextBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            Icons.Default.Edit, 
                            contentDescription = null, 
                            tint = if (isDarkMode) Color.White else AppleTextBlack
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(currentPerfume) }) {
                        Icon(
                            imageVector = if (currentPerfume.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (currentPerfume.isFavorite) Color(0xFFFFCC00) else (if (isDarkMode) Color.White.copy(alpha = 0.3f) else AppleTextSecondary.copy(alpha = 0.3f))
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = null, 
                            tint = if (isDarkMode) Color.White.copy(alpha = 0.4f) else AppleTextSecondary.copy(alpha = 0.5f)
                        )
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
                    modifier = Modifier.padding(bottom = 32.dp),
                    containerColor = if (isDarkMode) Color.White else AppleTextBlack,
                    contentColor = if (isDarkMode) Color.Black else Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bild
            if (currentPerfume.imageUrl.isNotEmpty()) {
                item {
                    GlassSurface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        alpha = if (isDarkMode) 0.3f else 0.4f,
                        isDarkMode = isDarkMode
                    ) {
                        AsyncImage(
                            model = currentPerfume.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }
            }

            // Name + Rating
            item {
                GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            currentPerfume.name, 
                            fontSize = 30.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = if (isDarkMode) Color.White else AppleTextBlack, 
                            lineHeight = 36.sp,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(12.dp))
                        AnimatedRatingBar(rating = currentPerfume.rating, isDarkMode = isDarkMode)
                        Spacer(Modifier.height(24.dp))
                        
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            currentPerfume.type.split(" / ").filter { it.isNotBlank() }.forEach { 
                                MiniChip(viewModel.translateFamily(it), isDarkMode) 
                            }
                        }
                    }
                }
            }

            // Füllstand
            item {
                GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        val percentage = (remainingMl / currentPerfume.bottleSize.toDouble() * 100).toInt().coerceIn(0, 100)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    viewModel.t("Füllstand", "Fill Level"), 
                                    color = if (isDarkMode) Color.White else AppleTextBlack, 
                                    fontSize = 16.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    viewModel.t("$percentage% verbleibend", "$percentage% remaining"), 
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                "${"%.2f".format(remainingMl)} / ${currentPerfume.bottleSize} ml", 
                                color = if (isDarkMode) Color.White else AppleTextBlack, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 18.sp
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        val progressColor = when {
                            percentage >= 20 -> Color(0xFF4CAF50) // Green
                            percentage >= 10 -> Color(0xFFFF9800) // Orange
                            else -> Color(0xFFF44336) // Red
                        }
                        val animatedProgress by animateFloatAsState(
                            targetValue = (remainingMl / currentPerfume.bottleSize.toDouble()).toFloat(),
                            animationSpec = tween(1000, easing = FastOutSlowInEasing),
                            label = "progress"
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = progressColor,
                            trackColor = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f),
                        )
                    }
                }
            }

            // Details
            item {
                GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            viewModel.t("DETAILS", "DETAILS"), 
                            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            letterSpacing = 1.sp
                        )
                        DetailRow(viewModel.t("Saison", "Season"), viewModel.translateSeason(currentPerfume.season), isDarkMode)
                        DetailRow(viewModel.t("Anlass", "Occasion"), viewModel.translateOccasion(currentPerfume.occasion), isDarkMode)
                        if (currentPerfume.price > 0) DetailRow(viewModel.t("Preis", "Price"), "€${"%.2f".format(currentPerfume.price)}", isDarkMode)
                        if (currentPerfume.purchaseDate.isNotEmpty()) DetailRow(viewModel.t("Gekauft", "Purchased"), currentPerfume.purchaseDate, isDarkMode)
                        if (currentPerfume.notes.isNotEmpty()) DetailRow(viewModel.t("Notizen", "Notes"), currentPerfume.notes, isDarkMode)
                    }
                }
            }

            // Tragehäufigkeit
            if (!currentPerfume.isWishlist) {
                item {
                    GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                viewModel.t("Tragehäufigkeit", "Usage Frequency"), 
                                color = if (isDarkMode) Color.White else AppleTextBlack, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                "${logs.size}×", 
                                color = if (isDarkMode) Color.White else AppleTextBlack, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }

            // Verlauf
            if (logs.isNotEmpty()) {
                item {
                    Text(
                        viewModel.t("VERLAUF", "HISTORY"),
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                }
                items(logs) { log ->
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        alpha = 0.3f,
                        isDarkMode = isDarkMode
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    log.date, 
                                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${log.sprays} ${viewModel.t("Sprüher", "Sprays")}", 
                                    color = if (isDarkMode) Color.White else AppleTextBlack, 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (log.weather.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    LogChip(log.weather, isDarkMode)
                                    if (log.occasion.isNotEmpty()) LogChip(viewModel.translateOccasion(log.occasion), isDarkMode)
                                }
                            }
                            if (log.note.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    log.note, 
                                    color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.8f), 
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }

    // Log-Dialog
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

    // Edit-Dialog
    if (showEditDialog) {
        com.example.perfumevault.ui.dialogs.EditPerfumeDialog(
            perfume = currentPerfume,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                val wasWishlist = currentPerfume.isWishlist
                val isWishlist = updated.isWishlist
                
                viewModel.updatePerfume(updated)
                showEditDialog = false
                
                // Wenn von Sammlung -> Merkliste: Navigiere
                if (!wasWishlist && isWishlist) {
                    onBack() // Detail schließen
                    viewModel.setTab(1) // Zur Merkliste wechseln
                }
            }
        )
    }

    // Löschen
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = if (isDarkMode) Color(0xFF1C1C1E) else Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { 
                Text(
                    viewModel.t("Duft löschen?", "Delete fragrance?"), 
                    color = if (isDarkMode) Color.White else AppleTextBlack, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(
                    viewModel.t("${currentPerfume.brand} – ${currentPerfume.name} wird dauerhaft entfernt.", "${currentPerfume.brand} – ${currentPerfume.name} will be permanently removed."), 
                    color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else AppleTextSecondary
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePerfume(currentPerfume)
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) { Text(viewModel.t("Löschen", "Delete"), color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(
                        viewModel.t("Abbrechen", "Cancel"), 
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary
                    )
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, isDarkMode: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontSize = 13.sp)
        Text(value, color = if (isDarkMode) Color.White else AppleTextBlack, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
