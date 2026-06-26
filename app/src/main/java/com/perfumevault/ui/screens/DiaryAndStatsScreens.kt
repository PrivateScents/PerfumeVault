package com.perfumevault.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perfumevault.BuildConfig
import com.perfumevault.data.UsageLog
import com.perfumevault.data.Perfume
import com.perfumevault.ui.components.*
import com.perfumevault.ui.theme.*
import com.perfumevault.viewmodel.PerfumeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// ──────────────────────────────────────────────
// DIARY SCREEN
// ──────────────────────────────────────────────
@Composable
fun DiaryScreen(viewModel: PerfumeViewModel, onPerfumeClick: (String) -> Unit) {
    val logs by viewModel.allLogs.collectAsState()
    val perfumes by viewModel.unfilteredPerfumes.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val adaptive = LocalAdaptiveColors.current
    viewModel.currentLanguage.collectAsState() 

    var editingLog by remember { mutableStateOf<UsageLog?>(null) }
    val perfumeMap = remember(perfumes) { perfumes.associateBy { it.id } }
    val grouped by remember(logs) {
        derivedStateOf {
            logs.groupBy { it.date }.entries.sortedByDescending { it.key }
        }
    }

    // Pre-calculate historical volumes for each log to ensure correct math in UI
    val historicalVolumes = remember(logs, perfumes) {
        val map = mutableMapOf<Int, Double>() // logId -> volume AFTER this log
        val currentVolumes = perfumes.associateBy({ it.id }, { it.remainingMl }).toMutableMap()
        
        // Process logs from NEWEST to OLDEST (as they appear in the list)
        // Since remainingMl is the volume AFTER the newest log.
        logs.sortedByDescending { it.id }.forEach { log ->
            val volAfter = currentVolumes[log.perfumeId] ?: 0.0
            map[log.id] = volAfter
            // To find the volume BEFORE this log, we add what was consumed
            val mlConsumed = log.sprays.toDouble() / 15.0
            currentVolumes[log.perfumeId] = volAfter + mlConsumed
        }
        map
    }

    if (grouped.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📖", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    viewModel.t("Noch keine Einträge", "No entries yet"), 
                    color = adaptive.textSecondary, 
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    viewModel.t("Wähle einen Duft aus und tippe 'Heute getragen'", "Select a fragrance and tap 'Worn Today'"), 
                    color = adaptive.textSecondary.copy(alpha = 0.6f), 
                    fontSize = 12.sp
                )
            }
        }
    } else {
        val bottomPadding = if (BuildConfig.FLAVOR == "public" && !isAdFree) 200.dp else 100.dp
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            grouped.forEach { (date, dayLogs) ->
                item(key = "header_$date") {
                    DateHeader(date, viewModel)
                }
                itemsIndexed(dayLogs, key = { _, it -> it.id }) { _, log ->
                    perfumeMap[log.perfumeId]?.let { perfume ->
                        LogCard(
                            log = log,
                            perfume = perfume,
                            viewModel = viewModel,
                            volAfter = historicalVolumes[log.id] ?: perfume.remainingMl,
                            onDelete = { viewModel.deleteLog(log) },
                            onPerfumeClick = { onPerfumeClick(perfume.id) }
                        ) {
                            editingLog = log
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
        val perfume = perfumeMap[currentEditingLog.perfumeId]
        com.perfumevault.ui.dialogs.EditLogDialog(
            log = currentEditingLog,
            currentPerfumeVolume = perfume?.remainingMl ?: 0.0,
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
    val adaptive = LocalAdaptiveColors.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()
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
        text = label.uppercase(),
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp, start = 4.dp),
        color = adaptive.textPrimary.copy(alpha = 0.4f),
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.sp
    )
}

@Composable
fun LogCard(log: UsageLog, perfume: Perfume, viewModel: PerfumeViewModel, volAfter: Double, onDelete: () -> Unit, onPerfumeClick: () -> Unit, onEditClick: () -> Unit) {
    var showDelete by remember { mutableStateOf(value = false) }
    val adaptive = LocalAdaptiveColors.current

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onEditClick,
        onLongClick = { showDelete = !showDelete }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f).clickable { onPerfumeClick() }) {
                Text(
                    perfume.brand.uppercase(),
                    fontSize = 11.sp,
                    color = adaptive.textSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    perfume.name, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = adaptive.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val percentage = remember(perfume.remainingMl, perfume.bottleSize) {
                    ((perfume.remainingMl / perfume.bottleSize.toDouble()) * 100).toInt().coerceIn(0, 100)
                }
                val progressColor = when {
                    percentage >= 20 -> Color(0xFF4CAF50)
                    percentage >= 10 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }
                
                val mlConsumed = log.sprays.toDouble() / 15.0
                
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(progressColor.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${log.sprays} ${viewModel.t("Sprüher", "Sprays")}",
                            color = progressColor,
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        viewModel.t(
                            "Verbrauch: %.2f ml (%.2f ml → %.2f ml)",
                            "Usage: %.2f ml (%.2f ml → %.2f ml)"
                        ).format(
                            mlConsumed, 
                            volAfter + mlConsumed, 
                            volAfter
                        ),
                        fontSize = 9.sp,
                        color = adaptive.textSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (log.weather.isNotEmpty()) LogChip(log.weather)
            if (log.occasion.isNotEmpty()) LogChip(viewModel.translateOccasion(log.occasion))
        }

        if (log.note.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                "„${log.note}“",
                fontSize = 13.sp,
                color = adaptive.textPrimary.copy(alpha = 0.7f),
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
                    Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// ──────────────────────────────────────────────
// STATS SCREEN
// ──────────────────────────────────────────────
@Composable
fun StatsScreen(viewModel: PerfumeViewModel, onPerfumeClick: (String) -> Unit) {
    val totalCount by viewModel.totalCount.collectAsState()
    val avgRating by viewModel.averageRating.collectAsState()
    val totalValue by viewModel.totalValue.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val mostUsed by viewModel.mostUsed.collectAsState()
    val perfumes by viewModel.unfilteredPerfumes.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val adaptive = LocalAdaptiveColors.current

    val top5 by remember(perfumes) { 
        derivedStateOf {
            perfumes.asSequence().sortedByDescending { it.rating }.take(5).toList() 
        }
    }

    if (totalCount == 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BarChart, 
                    contentDescription = null, 
                    modifier = Modifier.size(64.dp), 
                    tint = adaptive.textSecondary.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    viewModel.t("Noch keine Daten", "No data yet"), 
                    color = adaptive.textPrimary, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    viewModel.t("Füge Düfte hinzu, um Statistiken zu sehen.", "Add fragrances to see statistics."), 
                    color = adaptive.textSecondary.copy(alpha = 0.5f), 
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    val bottomPadding = if (BuildConfig.FLAVOR == "public" && !isAdFree) 200.dp else 120.dp
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomPadding, start = 16.dp, end = 16.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- TITLE ---
        item {
            var isHeaderVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { isHeaderVisible = true }
            
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    viewModel.t("Statistiken", "Statistics"),
                    color = adaptive.textPrimary,
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
                    color = adaptive.textPrimary.copy(alpha = 0.6f),
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
                    value = totalCount.toString(),
                    icon = Icons.Default.Inventory2
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Ø Rating", "Avg Rating"),
                    value = "%.1f".format(avgRating),
                    icon = Icons.Default.Star
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Gesamtwert", "Total Value"),
                    value = "€%.0f".format(totalValue),
                    icon = Icons.Default.Payments
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = viewModel.t("Einträge", "Log Entries"),
                    value = "${allLogs.size}",
                    icon = Icons.Default.History
                )
            }
        }

        // --- MOST WORN HERO ---
        if (mostUsed != null) {
            item {
                SectionHeader(viewModel.t("Meistgetragen", "Most Worn"))
                Spacer(Modifier.height(12.dp))
                PressableGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPerfumeClick(mostUsed!!.id) }
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
                                    .background(adaptive.textPrimary.copy(alpha = 0.04f)),
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
                                    color = adaptive.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    lineHeight = 28.sp
                                )
                                Text(
                                    mostUsed!!.brand.uppercase(),
                                    color = adaptive.textPrimary.copy(alpha = 0.5f),
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
            SectionHeader(viewModel.t("Füllstand Analyse", "Fill Level Analysis"))
            Spacer(Modifier.height(12.dp))
            val perfumesList = perfumes
            val lowStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) < 0.1 } }
            val mediumStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) in 0.1..0.25 } }
            val goodStock = remember(perfumesList) { perfumesList.count { (it.remainingMl / it.bottleSize.toDouble()) > 0.25 } }

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.5f,
                cornerRadius = 28.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FillStatItem(viewModel.t("Voll", "Full"), goodStock, Color(0xFF4CAF50))
                    FillStatItem(viewModel.t("Wenig", "Low"), mediumStock, Color(0xFFFF9800))
                    FillStatItem(viewModel.t("Leer", "Empty"), lowStock, Color(0xFFF44336))
                }
            }
        }

        // --- TOP 5 RANKING ---
        item {
            SectionHeader(viewModel.t("Top 5 Bewertungen", "Top 5 Rated"))
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                top5.forEachIndexed { index, perfume ->
                    PressableGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onPerfumeClick(perfume.id) }
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
                                    color = adaptive.textPrimary.copy(alpha = 0.1f),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            perfume.name, 
                                            color = adaptive.textPrimary, 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 18.sp,
                                            maxLines = 1
                                        )
                                        if (perfume.isSample) {
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                viewModel.t("PROBE", "SAMPLE"),
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .background(BlueSlate, RoundedCornerShape(3.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        if (perfume.isFavorite) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        perfume.brand.uppercase(), 
                                        color = adaptive.textSecondary, 
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
    }
}

@Composable
fun FillStatItem(label: String, count: Int, color: Color) {
    val adaptive = LocalAdaptiveColors.current
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
            color = adaptive.textPrimary, 
            fontWeight = FontWeight.Bold, 
            fontSize = 18.sp
        )
        Text(
            label, 
            color = adaptive.textPrimary.copy(alpha = 0.5f), 
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
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val adaptive = LocalAdaptiveColors.current
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
        alpha = 0.4f,
        cornerRadius = 28.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = adaptive.textPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                displayValue, 
                fontWeight = FontWeight.Black, 
                fontSize = 28.sp, 
                color = adaptive.textPrimary, 
                letterSpacing = (-0.5).sp
            )
            Text(
                label, 
                fontSize = 11.sp, 
                color = adaptive.textSecondary, 
                fontWeight = FontWeight.Bold, 
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun SettingsScreen(viewModel: PerfumeViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val perfumes by viewModel.absoluteAllPerfumes.collectAsState()
    val perfumeSizes by viewModel.allPerfumeSizes.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val adaptive = LocalAdaptiveColors.current
    
    val scope = rememberCoroutineScope()
    
    var showBulkImport by remember { mutableStateOf(false) }
    var deleteConfirmationStep by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item {
            Text(
                viewModel.t("Einstellungen", "Settings"),
                color = adaptive.textPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp
            )
        }

        // --- SECTION: APPEARANCE ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Erscheinungsbild", "Appearance"))
                
                if (com.perfumevault.BuildConfig.FLAVOR == "public" && !isAdFree) {
                    SettingsActionCard(
                        label = viewModel.t("Werbung entfernen", "Remove Ads"),
                        icon = androidx.compose.material.icons.Icons.Default.WorkspacePremium,
                        contentColor = AppleAccentBlue,
                        onClick = {
                            val activity = context as? android.app.Activity
                            activity?.let { viewModel.purchaseRemoveAds(it) }
                        }
                    )
                }

                GlassToggle(
                    label = viewModel.t("Dunkler Modus", "Dark Mode"),
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }
        }

        // --- SECTION: LANGUAGE ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Sprache", "Language"))
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
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
                                    .background(if (selected) adaptive.textPrimary else Color.Transparent)
                                    .clickable { viewModel.setLanguage(code) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label,
                                    color = if (selected) (if (adaptive.isDark) Color.Black else Color.White) else adaptive.textPrimary.copy(alpha = 0.6f),
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
                SectionHeader(viewModel.t("Datenverwaltung", "Data Management"))
                
                // Bulk Import
                SettingsActionCard(
                    label = viewModel.t("Bulk Import (JSON / TXT)", "Bulk Import (JSON / TXT)"),
                    icon = Icons.Default.UploadFile,
                    onClick = { showBulkImport = true }
                )

                // Export
                SettingsActionCard(
                    label = viewModel.t("Sammlung exportieren (JSON)", "Export Collection (JSON)"),
                    icon = Icons.Default.Share,
                    onClick = {
                        scope.launch {
                            val json = org.json.JSONObject().apply {
                                put("perfumes", org.json.JSONArray().apply {
                                    perfumes.forEach { p ->
                                        put(org.json.JSONObject().apply {
                                            put("id", p.id)
                                            put("brand", p.brand)
                                            put("name", p.name)
                                            put("bottleSize", p.bottleSize)
                                            put("remainingMl", p.remainingMl)
                                            put("price", p.price)
                                            put("rating", p.rating)
                                            put("type", p.type)
                                            put("concentration", p.concentration)
                                            put("season", p.season)
                                            put("occasion", p.occasion)
                                            put("notes", p.notes)
                                            put("isFavorite", p.isFavorite)
                                            put("purchaseDate", p.purchaseDate)
                                            put("addedDate", p.addedDate)
                                            put("imageUrl", p.imageUrl)
                                            put("isWishlist", p.isWishlist)
                                            put("isSample", p.isSample)
                                        })
                                    }
                                })
                                put("logs", org.json.JSONArray().apply {
                                    allLogs.forEach { l ->
                                        put(org.json.JSONObject().apply {
                                            put("perfumeId", l.perfumeId)
                                            put("date", l.date)
                                            put("occasion", l.occasion)
                                            put("weather", l.weather)
                                            put("note", l.note)
                                            put("sprays", l.sprays)
                                        })
                                    }
                                })
                                put("sizes", org.json.JSONArray().apply {
                                    perfumeSizes.forEach { s ->
                                        put(org.json.JSONObject().apply {
                                            put("perfumeId", s.perfumeId)
                                            put("ml", s.ml)
                                        })
                                    }
                                })
                            }.toString(2)
                            
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Export PerfumeVault"))
                        }
                    }
                )

                // Delete Collection
                SettingsActionCard(
                    label = viewModel.t("Sammlung unwiderruflich löschen", "Delete Collection permanently"),
                    icon = Icons.Default.DeleteForever,
                    contentColor = Color.Red.copy(alpha = 0.8f),
                    onClick = { deleteConfirmationStep = 1 }
                )
            }
        }

        // --- SECTION: ABOUT ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(viewModel.t("Über PerfumeVault", "About PerfumeVault"))
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "PerfumeVault v6.0",
                            color = adaptive.textPrimary, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            viewModel.t("Deine digitale Vitrine für exklusive Düfte.", "Your digital cabinet for exclusive fragrances."),
                            color = adaptive.textPrimary.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "© 2024 PerfumeVault Team",
                            color = adaptive.textPrimary.copy(alpha = 0.3f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        item { Spacer(Modifier.height(if (BuildConfig.FLAVOR == "public") 180.dp else 100.dp)) }
    }

    if (showBulkImport) {
        com.perfumevault.ui.dialogs.BulkAddDialog(
            viewModel = viewModel,
            onDismiss = { showBulkImport = false }
        )
    }

    // --- DELETE CONFIRMATION DIALOGS ---
    if (deleteConfirmationStep == 1) {
        AlertDialog(
            onDismissRequest = { deleteConfirmationStep = 0 },
            containerColor = adaptive.glassBase,
            shape = RoundedCornerShape(28.dp),
            title = { Text(viewModel.t("Sammlung löschen?", "Delete Collection?"), fontWeight = FontWeight.Bold, color = adaptive.textPrimary) },
            text = { Text(viewModel.t("Bist du sicher? Alle Düfte und Einträge gehen verloren.", "Are you sure? All fragrances and entries will be lost."), color = adaptive.textPrimary.copy(alpha = 0.7f)) },
            confirmButton = {
                TextButton(onClick = { deleteConfirmationStep = 2 }) {
                    Text(viewModel.t("Weiter", "Continue"), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmationStep = 0 }) {
                    Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary)
                }
            }
        )
    }

    if (deleteConfirmationStep == 2) {
        AlertDialog(
            onDismissRequest = { deleteConfirmationStep = 0 },
            containerColor = adaptive.glassBase,
            shape = RoundedCornerShape(28.dp),
            title = { Text(viewModel.t("Letzte Warnung!", "Last Warning!"), fontWeight = FontWeight.Black, color = Color.Red) },
            text = { Text(viewModel.t("Diese Aktion ist UNWIDERRUFLICH. Wirklich alles löschen?", "This action is IRREVERSIBLE. Really delete everything?"), color = adaptive.textPrimary.copy(alpha = 0.9f)) },
            confirmButton = {
                HighVisibilityButton(
                    text = viewModel.t("JETZT LÖSCHEN", "DELETE NOW"),
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    onClick = {
                        viewModel.clearAllData()
                        deleteConfirmationStep = 0
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmationStep = 0 }) {
                    Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingsActionCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentColor: Color? = null,
    onClick: () -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    val finalColor = contentColor ?: adaptive.textPrimary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .background(adaptive.textPrimary.copy(alpha = 0.02f))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                icon, 
                contentDescription = null, 
                tint = finalColor.copy(alpha = 0.6f), 
                modifier = Modifier.size(24.dp)
            )
            Text(
                label,
                modifier = Modifier.weight(1f),
                color = finalColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Icon(
                Icons.Default.ChevronRight, 
                null, 
                tint = finalColor.copy(alpha = 0.2f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    val adaptive = LocalAdaptiveColors.current
    Text(
        text,
        color = adaptive.textSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.3.sp
    )
}
