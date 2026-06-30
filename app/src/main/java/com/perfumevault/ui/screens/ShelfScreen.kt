package com.perfumevault.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.perfumevault.BuildConfig
import com.perfumevault.data.Perfume
import com.perfumevault.ui.components.*
import com.perfumevault.ui.theme.*
import com.perfumevault.viewmodel.PerfumeViewModel
import com.perfumevault.viewmodel.SortMode

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ShelfScreen(
    viewModel: PerfumeViewModel,
    onPerfumeClick: (Perfume) -> Unit,
) {
    val perfumes by viewModel.allPerfumes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortMode by viewModel.sortMode.collectAsState()
    val filterSamples by viewModel.filterSamples.collectAsState()
    val filterSeason by viewModel.filterSeason.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val adaptive = LocalAdaptiveColors.current
    viewModel.currentLanguage.collectAsState() 

    var showSortMenu by remember { mutableStateOf(value = false) }
    var showSeasonMenu by remember { mutableStateOf(value = false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Suchleiste
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                cornerRadius = 28.dp,
                alpha = 0.4f
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search, 
                        null, 
                        tint = adaptive.textPrimary.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = adaptive.textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(AppleAccentBlue),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        viewModel.t("Suchen...", "Search..."),
                                        color = adaptive.textPrimary.copy(alpha = 0.2f),
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp), tint = adaptive.textPrimary.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }

        // ── Filter- & Sortierleiste
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectableChip(
                label = viewModel.t("Proben", "Samples"),
                selected = filterSamples,
                onClick = viewModel::toggleSampleFilter
            )

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Saison Dropdown
                Box {
                    SelectableChip(
                        label = viewModel.translateSeason(filterSeason),
                        selected = filterSeason != "Alle"
                    ) {
                        showSeasonMenu = true
                    }
                    DropdownMenu(
                        expanded = showSeasonMenu,
                        onDismissRequest = { showSeasonMenu = false },
                        modifier = Modifier.background(adaptive.glassBase)
                    ) {
                        val seasons = listOf("Alle", "Frühling", "Sommer", "Herbst", "Winter")
                        seasons.forEach { s ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        viewModel.translateSeason(s), 
                                        color = adaptive.textPrimary, 
                                        fontWeight = FontWeight.SemiBold
                                    ) 
                                },
                                onClick = { viewModel.setFilterSeason(s); showSeasonMenu = false },
                                leadingIcon = {
                                    if (filterSeason == s) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AppleAccentBlue)
                                    }
                                }
                            )
                        }
                    }
                }

                // Sort Mode Dropdown
                Box {
                    val sortLabel = when(sortMode) {
                        SortMode.BRAND -> viewModel.t("Marke", "Brand")
                        SortMode.RATING -> viewModel.t("Bewertung", "Rating")
                        SortMode.NAME -> viewModel.t("Name", "Name")
                        SortMode.RECENT -> viewModel.t("Neueste", "Recent")
                    }
                    SelectableChip(
                        label = sortLabel,
                        selected = false,
                        onClick = { showSortMenu = true }
                    )
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(adaptive.glassBase)
                    ) {
                        SortMode.entries.forEach { mode ->
                            val itemLabel = when(mode) {
                                SortMode.BRAND -> viewModel.t("Marke", "Brand")
                                SortMode.RATING -> viewModel.t("Bewertung", "Rating")
                                SortMode.NAME -> viewModel.t("Name", "Name")
                                SortMode.RECENT -> viewModel.t("Neueste", "Recent")
                            }
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        itemLabel, 
                                        color = adaptive.textPrimary, 
                                        fontWeight = FontWeight.SemiBold
                                    ) 
                                },
                                onClick = { viewModel.setSortMode(mode); showSortMenu = false },
                                leadingIcon = {
                                    if (sortMode == mode) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AppleAccentBlue)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (perfumes.isEmpty()) {
            EmptyState(viewModel)
        } else {
            val bottomPadding = if (BuildConfig.FLAVOR == "public" && !isAdFree) 200.dp else 120.dp
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = bottomPadding),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = perfumes,
                    key = { it.id }
                ) { perfume ->
                    PerfumeCard(
                        perfume = perfume,
                        viewModel = viewModel,
                        onClick = { onPerfumeClick(perfume) },
                        onFavoriteToggle = { viewModel.toggleFavorite(perfume) }
                    )
                }
            }
        }
    }
}

@Composable
fun PerfumeCard(
    perfume: Perfume,
    viewModel: PerfumeViewModel,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    
    val percentage = remember(perfume.remainingMl, perfume.bottleSize) {
        (perfume.remainingMl / perfume.bottleSize.toDouble() * 100).toInt().coerceIn(0, 100)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            onLongClick = onFavoriteToggle
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Frame
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(adaptive.textPrimary.copy(alpha = 0.02f))
                ) {
                    if (perfume.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = perfume.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Image, 
                            null, 
                            modifier = Modifier.size(32.dp).align(Alignment.Center), 
                            tint = adaptive.textPrimary.copy(alpha = 0.1f)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            com.perfumevault.util.BrandHelper.shortenBrand(perfume.brand).uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = adaptive.textPrimary.copy(alpha = 0.4f)
                        )
                        if (perfume.isSample) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                viewModel.t("PROBE", "SAMPLE"),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier
                                    .background(BlueSlate, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        if (perfume.isFavorite) {
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Text(
                        perfume.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = adaptive.textPrimary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star, 
                            null, 
                            modifier = Modifier.size(14.dp), 
                            tint = adaptive.textPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "%.1f".format(perfume.rating),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = adaptive.textPrimary
                        )
                        
                        if (perfume.season.isNotEmpty() && (perfume.season != "Alle")) {
                            Spacer(Modifier.width(12.dp))
                            Text(
                                viewModel.translateSeason(perfume.season),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = adaptive.textPrimary.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .background(adaptive.textPrimary.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (perfume.notes.isNotEmpty()) {
                        Text(
                            text = perfume.notes,
                            fontSize = 10.sp,
                            color = adaptive.textPrimary.copy(alpha = 0.3f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                FillIndicator(percent = percentage)
            }
        }
        
        // Indicator Dots removed as per user request
    }
}

@Composable
fun FillIndicator(percent: Int) {
    val adaptive = LocalAdaptiveColors.current
    val progressColor = when {
        percent >= 20 -> Color(0xFF4CAF50) // Green
        percent >= 10 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
    
    val animatedFill by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "fill"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(adaptive.textPrimary.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(animatedFill)
                    .background(progressColor.copy(alpha = 0.8f))
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "$percent%", 
            fontSize = 9.sp, 
            color = progressColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun EmptyState(viewModel: PerfumeViewModel) {
    val adaptive = LocalAdaptiveColors.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text(
                "✧", 
                fontSize = 48.sp, 
                color = adaptive.textPrimary.copy(alpha = 0.1f)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                viewModel.t("Deine Sammlung ist leer", "Your collection is empty"), 
                color = adaptive.textPrimary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                viewModel.t("Beginne damit, deine exklusiven Düfte hinzuzufügen.", "Start by adding your exclusive fragrances."), 
                color = adaptive.textPrimary.copy(alpha = 0.4f),
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
