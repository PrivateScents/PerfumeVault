package com.example.perfumevault.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.perfumevault.data.UsageLog
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.viewmodel.PerfumeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// ──────────────────────────────────────────────
// DIARY SCREEN
// ──────────────────────────────────────────────
@Composable
fun DiaryScreen(viewModel: PerfumeViewModel, onPerfumeClick: (Int) -> Unit) {
    val logs by viewModel.allLogs.collectAsState()
    val perfumes by viewModel.unfilteredPerfumes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    viewModel.currentLanguage.collectAsState() 

    var editingLog by remember { mutableStateOf<UsageLog?>(null) }
    val perfumeMap = remember(perfumes) { perfumes.associateBy { it.id } }
    val grouped = remember(logs) {
        logs.groupBy { it.date }.entries.sortedByDescending { it.key }
    }

    if (grouped.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📖", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    viewModel.t("Noch keine Einträge", "No entries yet"), 
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    viewModel.t("Wähle einen Duft aus und tippe 'Heute getragen'", "Select a fragrance and tap 'Worn Today'"), 
                    color = if (isDarkMode) Color.White.copy(alpha = 0.3f) else AppleTextSecondary.copy(alpha = 0.6f), 
                    fontSize = 12.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            grouped.forEach { (date, dayLogs) ->
                item(key = "header_$date") {
                    DateHeader(date, viewModel)
                }
                itemsIndexed(dayLogs, key = { _, it -> it.id }) { index, log ->
                    val perfume = perfumeMap[log.perfumeId]
                    if (perfume != null) {
                        var isEntryVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { isEntryVisible = true }
                        
                        val alpha by animateFloatAsState(
                            targetValue = if (isEntryVisible) 1f else 0f,
                            animationSpec = tween(600, delayMillis = (index * 20).coerceAtMost(200)),
                            label = "diaryAlpha"
                        )
                        
                        Box(Modifier.graphicsLayer { this.alpha = alpha }) {
                            LogCard(
                                log = log,
                                perfume = perfume,
                                viewModel = viewModel,
                                isDarkMode = isDarkMode,
                                onDelete = { viewModel.deleteLog(log) },
                                onPerfumeClick = { onPerfumeClick(perfume.id) },
                                onEditClick = { editingLog = log }
                            )
                        }
                    }
                }
                item(key = "spacer_$date") {
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    if (editingLog != null) {
        val currentEditingLog = editingLog!!
        com.example.perfumevault.ui.dialogs.EditLogDialog(
            log = currentEditingLog,
            viewModel = viewModel,
            onDismiss = { 
                editingLog = null 
            },
            onSave = { updated ->
                viewModel.updateLog(updated, currentEditingLog.sprays)
                editingLog = null
            }
        )
    }
}

@Composable
fun DateHeader(dateStr: String, viewModel: PerfumeViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val locale = remember(currentLanguage) { if (currentLanguage == "de") Locale.GERMAN else Locale.ENGLISH }

    val date = remember(dateStr) { runCatching { LocalDate.parse(dateStr) }.getOrNull() }
    val label = when (date) {
        LocalDate.now() -> viewModel.t("Heute", "Today")
        LocalDate.now().minusDays(1) -> viewModel.t("Gestern", "Yesterday")
        else -> date?.let {
            "${it.dayOfWeek.getDisplayName(TextStyle.FULL, locale)}, ${it.dayOfMonth}. ${
                it.month.getDisplayName(TextStyle.FULL, locale)
            } ${it.year}"
        } ?: dateStr
    }

    Text(
        text = label,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp, start = 4.dp),
        color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else AppleTextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun LogCard(log: UsageLog, perfume: Perfume, viewModel: PerfumeViewModel, isDarkMode: Boolean, onPerfumeClick: () -> Unit, onEditClick: () -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }

    PressableGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onEditClick,
        onLongClick = { showDelete = !showDelete },
        isDarkMode = isDarkMode
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).clickable { onPerfumeClick() }) {
                    Text(
                        perfume.brand.uppercase(),
                        fontSize = 11.sp,
                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        perfume.name, 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (isDarkMode) Color.White else AppleTextBlack
                    )
                }

                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val percentage = remember(perfume.remainingMl, perfume.bottleSize) {
                        (perfume.remainingMl / perfume.bottleSize.toDouble() * 100).toInt().coerceIn(0, 100)
                    }
                    val progressColor = when {
                        percentage >= 20 -> Color(0xFF4CAF50)
                        percentage >= 10 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(progressColor.copy(alpha = if (isDarkMode) 0.2f else 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${log.sprays} ${viewModel.t("Sprüher", "Sprays")}",
                            color = progressColor,
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (log.weather.isNotEmpty()) LogChip(log.weather, isDarkMode)
                if (log.occasion.isNotEmpty()) LogChip(viewModel.translateOccasion(log.occasion), isDarkMode)
            }

            if (log.note.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "„${log.note}“",
                    fontSize = 13.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else AppleTextBlack.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(showDelete) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp), 
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDelete) {
                        Text(viewModel.t("Löschen", "Delete"), color = Color(0xFFFF4B4B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { showDelete = false }) {
                        Text(viewModel.t("Abbrechen", "Cancel"), color = AppleTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ──────────────────────────────────────────────
// STATS SCREEN
// ──────────────────────────────────────────────
@Composable
fun StatsScreen(viewModel: PerfumeViewModel, onPerfumeClick: (Int) -> Unit) {
    val totalCount by viewModel.totalCount.collectAsState()
    val avgRating by viewModel.averageRating.collectAsState()
    val totalValue by viewModel.totalValue.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val mostUsed by viewModel.mostUsed.collectAsState()
    val perfumes by viewModel.unfilteredPerfumes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val top5 = remember(perfumes) { perfumes.sortedByDescending { it.rating }.take(5) }

    if (totalCount == 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BarChart, 
                    contentDescription = null, 
                    modifier = Modifier.size(64.dp), 
                    tint = (if (isDarkMode) Color.White else AppleTextSecondary).copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    viewModel.t("Noch keine Daten", "No data yet"), 
                    color = if (isDarkMode) Color.White else AppleTextBlack, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    viewModel.t("Füge Düfte hinzu, um Statistiken zu sehen.", "Add fragrances to see statistics."), 
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- TITLE ---
        item {
            var isHeaderVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { isHeaderVisible = true }
            
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    viewModel.t("Statistiken", "Statistics"),
                    color = if (isDarkMode) Color.White else AppleTextBlack,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1.5).sp,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isHeaderVisible) 1f else 0f
                        translationY = if (isHeaderVisible) 0f else -20f
                    }
                )
                Text(
                    viewModel.t("Ein tieferer Einblick in deine Sammlung", "A deep dive into your collection"),
                    color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.4f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // --- KEY METRICS GRID ---
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Düfte", "Fragrances"),
                    value = "$totalCount",
                    icon = Icons.Default.Inventory2,
                    isDarkMode = isDarkMode
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Ø Rating", "Avg Rating"),
                    value = "%.1f".format(avgRating),
                    icon = Icons.Default.Star,
                    isDarkMode = isDarkMode
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Gesamtwert", "Total Value"),
                    value = "€%.0f".format(totalValue),
                    icon = Icons.Default.Payments,
                    isDarkMode = isDarkMode
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Einträge", "Log Entries"),
                    value = "${allLogs.size}",
                    icon = Icons.Default.History,
                    isDarkMode = isDarkMode
                )
            }
        }

        // --- MOST WORN HERO ---
        if (mostUsed != null) {
            item {
                SectionHeader(viewModel.t("🏆 Meistgetragen", "🏆 Most Worn"), isDarkMode)
                Spacer(Modifier.height(12.dp))
                PressableGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPerfumeClick(mostUsed!!.id) },
                    isDarkMode = isDarkMode
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Background Decoration
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = AppleAccentBlue.copy(alpha = 0.05f),
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.CenterEnd)
                                .offset(x = 20.dp, y = 20.dp)
                        )
                        
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AppleAccentBlue, modifier = Modifier.size(36.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    viewModel.t("Top Favorit", "Top Favorite"),
                                    color = AppleAccentBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    mostUsed!!.name,
                                    color = if (isDarkMode) Color.White else AppleTextBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    lineHeight = 28.sp
                                )
                                Text(
                                    mostUsed!!.brand.uppercase(),
                                    color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- FILL LEVEL STATUS ---
        item {
            SectionHeader(viewModel.t("📊 Füllstand Analyse", "📊 Fill Level Analysis"), isDarkMode)
            Spacer(Modifier.height(12.dp))
            val perfumesList = perfumes
            val lowStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) < 0.1 } }
            val mediumStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) in 0.1..0.25 } }
            val goodStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) > 0.25 } }

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isDarkMode = isDarkMode,
                alpha = 0.5f,
                cornerRadius = 28.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FillStatItem(viewModel.t("Voll", "Full"), goodStock, Color(0xFF4CAF50), isDarkMode)
                    FillStatItem(viewModel.t("Wenig", "Low"), mediumStock, Color(0xFFFF9800), isDarkMode)
                    FillStatItem(viewModel.t("Leer", "Empty"), lowStock, Color(0xFFF44336), isDarkMode)
                }
            }
        }

        // --- TOP 5 RANKING ---
        item {
            SectionHeader(viewModel.t("⭐ Top 5 Bewertungen", "⭐ Top 5 Rated"), isDarkMode)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top5.forEachIndexed { index, perfume ->
                    var isCardVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { isCardVisible = true }
                    
                    val alpha by animateFloatAsState(
                        targetValue = if (isCardVisible) 1f else 0f,
                        animationSpec = tween(600, delayMillis = index * 80),
                        label = "top5Alpha"
                    )

                    PressableGlassCard(
                        modifier = Modifier.fillMaxWidth().graphicsLayer { this.alpha = alpha },
                        onClick = { onPerfumeClick(perfume.id) },
                        isDarkMode = isDarkMode
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "${index + 1}",
                                    color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.1f),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Column {
                                    Text(
                                        perfume.name, 
                                        color = if (isDarkMode) Color.White else AppleTextBlack, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 18.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        perfume.brand.uppercase(), 
                                        color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, 
                                        fontSize = 10.sp, 
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AppleAccentBlue.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "%.1f".format(perfume.rating),
                                    color = AppleAccentBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- FRAGRANCE FAMILIES ---
        item {
            SectionHeader(viewModel.t("🧬 Duft-DNA", "🧬 Fragrance DNA"), isDarkMode)
            Spacer(Modifier.height(12.dp))
            val groupedTypes = remember(perfumes) {
                perfumes.flatMap { it.type.split(" / ") }
                    .filter { it.isNotBlank() }
                    .groupBy { it.trim() }
                    .mapValues { it.value.size }
                    .entries.sortedByDescending { it.value }
                    .take(6)
            }
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isDarkMode = isDarkMode,
                alpha = 0.4f,
                cornerRadius = 28.dp
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    groupedTypes.forEach { (type, count) ->
                        val fraction = remember(count, perfumes.size) { count.toFloat() / (perfumes.size.coerceAtLeast(1)) }
                        TypeBar(label = viewModel.translateFamily(type), fraction = fraction, count = count, isDarkMode = isDarkMode)
                    }
                }
            }
        }
    }
}

@Composable
fun FillStatItem(label: String, count: Int, color: Color, isDarkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "$count", 
            color = if (isDarkMode) Color.White else AppleTextBlack, 
            fontWeight = FontWeight.Bold, 
            fontSize = 18.sp
        )
        Text(
            label, 
            color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.5f), 
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean
) {
    val numericValue = remember(value) { value.replace(",", ".").filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0 }
    val animatedValue by animateFloatAsState(
        targetValue = numericValue.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "metricCounter"
    )

    val displayValue = remember(animatedValue, value) {
        if (value.contains("€")) "€%.0f".format(animatedValue)
        else if (value.contains(".")) "%.1f".format(animatedValue)
        else "${animatedValue.toInt()}"
    }

    GlassSurface(
        modifier = modifier,
        isDarkMode = isDarkMode,
        alpha = 0.4f,
        cornerRadius = 28.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (isDarkMode) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(displayValue, fontWeight = FontWeight.Black, fontSize = 28.sp, color = if (isDarkMode) Color.White else AppleTextBlack, letterSpacing = (-0.5).sp)
            Text(label, fontSize = 11.sp, color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun SettingsScreen(viewModel: PerfumeViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val perfumes by viewModel.unfilteredPerfumes.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val scope = rememberCoroutineScope()
    
    var showBulkImport by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            Text(
                viewModel.t("Einstellungen", "Settings"),
                color = if (isDarkMode) Color.White else AppleTextBlack,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp
            )
        }

        // --- SECTION: APPEARANCE ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Erscheinungsbild", "Appearance"), isDarkMode)
                GlassToggle(
                    label = viewModel.t("Dunkelmodus", "Dark Mode"),
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    isDarkMode = isDarkMode
                )
            }
        }

        // --- SECTION: LANGUAGE ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Sprache", "Language"), isDarkMode)
                GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("de" to "Deutsch", "en" to "English").forEach { (code, label) ->
                            val selected = currentLang == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (selected) (if (isDarkMode) Color.White else AppleTextBlack) else Color.Transparent)
                                    .clickable { viewModel.setLanguage(code) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    color = if (selected) (if (isDarkMode) Color.Black else Color.White) else (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION: DATA MANAGEMENT ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Datenverwaltung", "Data Management"), isDarkMode)
                
                // Bulk Import
                SettingsActionCard(
                    label = viewModel.t("Bulk Import (JSON / TXT)", "Bulk Import (JSON / TXT)"),
                    icon = Icons.Default.UploadFile,
                    isDarkMode = isDarkMode,
                    onClick = { showBulkImport = true }
                )

                // Export
                SettingsActionCard(
                    label = viewModel.t("Sammlung exportieren (JSON)", "Export Collection (JSON)"),
                    icon = Icons.Default.Share,
                    isDarkMode = isDarkMode,
                    onClick = {
                        scope.launch {
                            val allPerfumes = perfumes
                            val json = org.json.JSONArray().apply {
                                allPerfumes.forEach { p ->
                                    put(org.json.JSONObject().apply {
                                        put("brand", p.brand)
                                        put("name", p.name)
                                        put("bottleSize", p.bottleSize)
                                        put("remainingMl", p.remainingMl)
                                        put("price", p.price)
                                        put("rating", p.rating)
                                        put("type", p.type)
                                        put("isWishlist", p.isWishlist)
                                    })
                                }
                            }.toString(2)
                            
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Export PerfumeVault"))
                        }
                    }
                )
            }
        }

        // --- SECTION: ABOUT ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Über PerfumeVault", "About PerfumeVault"), isDarkMode)
                GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "PerfumeVault v1.4", 
                            color = if (isDarkMode) Color.White else AppleTextBlack, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            viewModel.t("Deine digitale Vitrine für exklusive Düfte.", "Your digital cabinet for exclusive fragrances."),
                            color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "© 2024 PerfumeVault Team",
                            color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.3f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        item { Spacer(Modifier.height(100.dp)) }
    }

    if (showBulkImport) {
        com.example.perfumevault.ui.dialogs.BulkAddDialog(
            viewModel = viewModel,
            onDismiss = { showBulkImport = false }
        )
    }
}

@Composable
fun SettingsActionCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDarkMode: Boolean,
    contentColor: Color? = null,
    onClick: () -> Unit
) {
    val finalColor = contentColor ?: (if (isDarkMode) Color.White else AppleTextBlack)
    
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 20.dp,
        isDarkMode = isDarkMode,
        alpha = if (isDarkMode) 0.2f else 0.3f
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(finalColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = finalColor, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                modifier = Modifier.weight(1f),
                color = finalColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Icon(
                Icons.Default.ChevronRight, 
                contentDescription = null, 
                tint = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.2f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TypeBar(label: String, fraction: Float, count: Int, isDarkMode: Boolean) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "typeBar"
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label, 
                color = if (isDarkMode) Color.White else AppleTextBlack, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "$count", 
                color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.4f), 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background((if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AppleAccentBlue.copy(alpha = 0.8f), AppleAccentBlue)
                        )
                    )
            )
        }
    }
}

@Composable
fun SectionHeader(text: String, isDarkMode: Boolean) {
    Text(
        text,
        color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else AppleTextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.3.sp
    )
}
