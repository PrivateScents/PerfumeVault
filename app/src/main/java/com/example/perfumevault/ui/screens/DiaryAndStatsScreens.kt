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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.perfumevault.data.UsageLog
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.viewmodel.PerfumeViewModel
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
    val topRated by viewModel.unfilteredPerfumes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val top5 = remember(topRated) { topRated.sortedByDescending { it.rating }.take(5) }

    if (totalCount == 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.BarChart, 
                    contentDescription = null, 
                    modifier = Modifier.size(48.dp), 
                    tint = (if (isDarkMode) Color.White else AppleTextSecondary).copy(alpha = 0.4f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    viewModel.t("Noch keine Daten", "No data yet"), 
                    color = if (isDarkMode) Color.White else AppleTextBlack, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            var isHeaderVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { isHeaderVisible = true }
            
            Text(
                viewModel.t("Statistiken", "Statistics"),
                color = if (isDarkMode) Color.White else AppleTextBlack,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                modifier = Modifier.graphicsLayer {
                    alpha = if (isHeaderVisible) 1f else 0f
                    translationY = if (isHeaderVisible) 0f else -20f
                }
            )
        }

        item {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(), 
                isDarkMode = isDarkMode,
                alpha = if (isDarkMode) 0.3f else 0.4f
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatBox(Modifier.weight(1f), viewModel.t("Düfte", "Fragrances"), "$totalCount", isDarkMode)
                        StatBox(Modifier.weight(1f), viewModel.t("Ø Rating", "Avg Rating"), "%.2f".format(avgRating), isDarkMode)
                    }
                    HorizontalDivider(color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.08f), thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatBox(Modifier.weight(1f), viewModel.t("Wert", "Total Value"), "€%.2f".format(totalValue), isDarkMode)
                        StatBox(Modifier.weight(1f), viewModel.t("Einträge", "Log Entries"), "${allLogs.size}", isDarkMode)
                    }
                }
            }
        }

        item {
            val perfumes = topRated
            val lowStock = remember(perfumes) { perfumes.count { (it.remainingMl / it.bottleSize.toDouble()) < 0.1 } }
            val mediumStock = remember(perfumes) { 
                perfumes.count { 
                    (it.remainingMl / it.bottleSize.toDouble()) in 0.1..0.2
                } 
            }
            val goodStock = remember(perfumes) { perfumes.count { (it.remainingMl / it.bottleSize.toDouble()) >= 0.2 } }

            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                isDarkMode = isDarkMode,
                alpha = 0.5f,
                cornerRadius = 24.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        viewModel.t("Füllstand-Status", "Fill Level Status"), 
                        color = if (isDarkMode) Color.White else AppleTextBlack, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FillStatItem(viewModel.t("Voll", "Full"), goodStock, Color(0xFF4CAF50), isDarkMode)
                        FillStatItem(viewModel.t("Wenig", "Low"), mediumStock, Color(0xFFFF9800), isDarkMode)
                        FillStatItem(viewModel.t("Fast leer", "Near Empty"), lowStock, Color(0xFFF44336), isDarkMode)
                    }
                }
            }
        }

        if (mostUsed != null) {
            item {
                SectionHeader(viewModel.t("🏆 Meistgetragen", "🏆 Most Worn"), isDarkMode)
                Spacer(Modifier.height(12.dp))
                PressableGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onPerfumeClick(mostUsed!!.id) },
                    isDarkMode = isDarkMode
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background((if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = if (isDarkMode) Color.White else AppleTextBlack, modifier = Modifier.size(26.dp))
                        }
                        Column {
                            Text(mostUsed!!.brand.uppercase(), color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(mostUsed!!.name, color = if (isDarkMode) Color.White else AppleTextBlack, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(viewModel.t("★ Top 5 Bewertungen", "★ Top 5 Rated"), isDarkMode)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                top5.forEachIndexed { index, perfume ->
                    var isCardVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { isCardVisible = true }
                    
                    val alpha by animateFloatAsState(
                        targetValue = if (isCardVisible) 1f else 0f,
                        animationSpec = tween(600, delayMillis = index * 50),
                        label = "top5Alpha"
                    )

                    PressableGlassCard(
                        modifier = Modifier.fillMaxWidth().graphicsLayer { this.alpha = alpha },
                        onClick = { onPerfumeClick(perfume.id) },
                        isDarkMode = isDarkMode
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(perfume.brand.uppercase(), color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(perfume.name, color = if (isDarkMode) Color.White else AppleTextBlack, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            }
                            Text(
                                "%.1f/10".format(perfume.rating),
                                color = if (isDarkMode) Color.White else AppleTextBlack,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(viewModel.t("🧬 Duftfamilien", "🧬 Fragrance Families"), isDarkMode)
            Spacer(Modifier.height(16.dp))
            val groupedTypes = remember(topRated) {
                topRated.flatMap { it.type.split(" / ") }
                    .filter { it.isNotBlank() }
                    .groupBy { it.trim() }
                    .mapValues { it.value.size }
                    .entries.sortedByDescending { it.value }
                    .take(6)
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                groupedTypes.forEach { (type, count) ->
                    val fraction = remember(count, topRated.size) { count.toFloat() / (topRated.size.coerceAtLeast(1)) }
                    TypeBar(label = viewModel.translateFamily(type), fraction = fraction, count = count, isDarkMode = isDarkMode)
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
fun StatBox(modifier: Modifier, label: String, value: String, isDarkMode: Boolean) {
    val numericValue = remember(value) { value.replace(",", ".").filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0 }
    val animatedValue by animateFloatAsState(
        targetValue = numericValue.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "statCounter"
    )

    val displayValue = remember(animatedValue, value) {
        if (value.contains("€")) {
            "€%.2f".format(animatedValue)
        } else if (value.contains(".")) {
            "%.2f".format(animatedValue)
        } else {
            "${animatedValue.toInt()}"
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(displayValue, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = if (isDarkMode) Color.White else AppleTextBlack)
        Text(label, fontSize = 11.sp, color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun SettingsScreen(viewModel: PerfumeViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var showBulkImport by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            viewModel.t("Einstellungen", "Settings"),
            color = if (isDarkMode) Color.White else AppleTextBlack,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1).sp
        )

        // Dark Mode Toggle
        GlassToggle(
            label = viewModel.t("Dunkelmodus", "Dark Mode"),
            checked = isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode() },
            isDarkMode = isDarkMode
        )

        // Language Selection
        GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    viewModel.t("Sprache / Language", "Language / Sprache"),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val languages = listOf("de" to "Deutsch", "en" to "English")
                    languages.forEach { (code, label) ->
                        val selected = currentLang == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selected) (if (isDarkMode) Color.White else AppleTextBlack) else (if (isDarkMode) Color.White.copy(alpha = 0.1f) else AppleTextBlack.copy(alpha = 0.05f)))
                                .clickable { viewModel.setLanguage(code) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (selected) (if (isDarkMode) Color.Black else Color.White) else (if (isDarkMode) Color.White else AppleTextBlack),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Datenverwaltung
        GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    viewModel.t("Datenverwaltung", "Data Management"),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = { showBulkImport = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) Color.White else Color.Black,
                        contentColor = if (isDarkMode) Color.Black else Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            viewModel.t("Bulk Import (JSON / TXT)", "Bulk Import (JSON / TXT)"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                // Example JSON Action
                val context = androidx.compose.ui.platform.LocalContext.current
                TextButton(
                    onClick = {
                        val exampleJson = """
                        [
                          {
                            "brand": "Dior",
                            "name": "Sauvage",
                            "bottleSize": 100,
                            "remainingMl": 85.0,
                            "price": 95.0,
                            "rating": 8.5,
                            "type": "Frisch / Würzig",
                            "isWishlist": false
                          },
                          {
                            "brand": "Chanel",
                            "name": "Bleu de Chanel",
                            "bottleSize": 50,
                            "remainingMl": 50.0,
                            "isWishlist": true
                          }
                        ]
                        """.trimIndent()
                        
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, exampleJson)
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "PerfumeVault Example JSON")
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Example JSON"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = AppleAccentBlue)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            viewModel.t("Beispiel-JSON kopieren", "Copy Example JSON"),
                            fontSize = 13.sp,
                            color = AppleAccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        GlassSurface(modifier = Modifier.fillMaxWidth(), isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("PerfumeVault v1.3", color = if (isDarkMode) Color.White else AppleTextBlack, fontWeight = FontWeight.Bold)
                Text(
                    viewModel.t("Deine persönliche Duft-Sammlung", "Your personal fragrance collection"),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }

    if (showBulkImport) {
        com.example.perfumevault.ui.dialogs.BulkAddDialog(
            viewModel = viewModel,
            onDismiss = { showBulkImport = false }
        )
    }
}

@Composable
fun TypeBar(label: String, fraction: Float, count: Int, isDarkMode: Boolean) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "typeBar"
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = if (isDarkMode) Color.White else AppleTextBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("$count", color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background((if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDarkMode) Color.White else AppleTextBlack)
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
