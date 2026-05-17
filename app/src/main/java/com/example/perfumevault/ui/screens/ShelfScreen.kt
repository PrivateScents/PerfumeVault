package com.example.perfumevault.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import coil.compose.AsyncImage
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.viewmodel.PerfumeViewModel
import com.example.perfumevault.viewmodel.SortMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfScreen(
    viewModel: PerfumeViewModel,
    onPerfumeClick: (Perfume) -> Unit
) {
    val perfumes by viewModel.allPerfumes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortMode by viewModel.sortMode.collectAsState()
    val filterFavorites by viewModel.filterFavorites.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    viewModel.currentLanguage.collectAsState() // Observe for recomposition

    var showSortMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Suchleiste (Glass Light)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { 
                Text(
                    viewModel.t("Suche nach Marke, Name…", "Search brand, name..."), 
                    fontSize = 14.sp, 
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary
                ) 
            },
            leadingIcon = { 
                Icon(
                    Icons.Default.Search, 
                    contentDescription = null, 
                    tint = if (isDarkMode) Color.White else AppleTextBlack
                ) 
            },
            trailingIcon = {
                AnimatedVisibility(searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            Icons.Default.Clear, 
                            contentDescription = viewModel.t("Löschen", "Clear"),
                            tint = if (isDarkMode) Color.White else AppleTextBlack
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = (if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.8f),
                unfocusedContainerColor = (if (isDarkMode) Color.Black else Color.White).copy(alpha = 0.5f),
                focusedBorderColor = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.1f),
                unfocusedBorderColor = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f),
                focusedTextColor = if (isDarkMode) Color.White else AppleTextBlack,
                unfocusedTextColor = if (isDarkMode) Color.White else AppleTextBlack
            ),
            singleLine = true
        )

        // ── Filter- & Sortierleiste
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectableChip(
                label = viewModel.t("Favoriten", "Favorites"),
                selected = filterFavorites,
                isDarkMode = isDarkMode,
                onClick = viewModel::toggleFavoriteFilter
            )

            Spacer(Modifier.weight(1f))

            Box {
                val sortLabel = when(sortMode) {
                    SortMode.BRAND -> viewModel.t("Marke", "Brand")
                    SortMode.RATING -> viewModel.t("Bewertung", "Rating")
                    SortMode.NAME -> viewModel.t("Name", "Name")
                    SortMode.RECENT -> viewModel.t("Neueste", "Recent")
                }
                SelectableChip(
                    label = "↕ $sortLabel",
                    selected = false,
                    isDarkMode = isDarkMode,
                    onClick = { showSortMenu = true }
                )
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(if (isDarkMode) Color(0xFF1C1C1E) else Color.White)
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
                                    color = if (isDarkMode) Color.White else AppleTextBlack, 
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

        Spacer(Modifier.height(12.dp))

        if (perfumes.isEmpty()) {
            EmptyState(viewModel, isDarkMode)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = perfumes, 
                    key = { _, p -> p.id },
                    contentType = { _, _ -> "perfume_card" }
                ) { index, perfume ->
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { isVisible = true }

                    val scale by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0.85f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
                        label = "scale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = tween(400, delayMillis = (index * 30).coerceAtMost(300)),
                        label = "alpha"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                    ) {
                        PerfumeCard(
                            perfume = perfume,
                            isDarkMode = isDarkMode,
                            onClick = { onPerfumeClick(perfume) },
                            onFavoriteToggle = { viewModel.toggleFavorite(perfume) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PerfumeCard(
    perfume: Perfume,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    PressableGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        onClick = onClick,
        onLongClick = onFavoriteToggle,
        isDarkMode = isDarkMode
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Container with subtle shadow and border
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
                    .border(
                        0.5.dp, 
                        (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.08f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (perfume.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = perfume.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = (if (isDarkMode) Color.White else AppleTextSecondary).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = perfume.brand.uppercase(),
                    fontSize = 10.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = perfume.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else AppleTextBlack,
                    lineHeight = 26.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(10.dp))
                AnimatedRatingBar(rating = perfume.rating, isDarkMode = isDarkMode)
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (perfume.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFCC00),
                        modifier = Modifier.size(20.dp)
                    )
                }
                val percentage = (perfume.remainingMl / perfume.bottleSize.toDouble() * 100).toInt().coerceIn(0, 100)
                FillIndicator(percent = percentage, isDarkMode = isDarkMode)
            }
        }
    }
}

@Composable
fun FillIndicator(percent: Int, isDarkMode: Boolean) {
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
                .background((if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f), RoundedCornerShape(6.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .graphicsLayer {
                        // Using scaleY and translationY to animate fill without layout re-calc
                        scaleY = animatedFill
                        translationY = size.height * (1f - animatedFill) / 2f
                    }
                    .background(
                        progressColor.copy(alpha = if (isDarkMode) 0.6f else 0.8f),
                        RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                    )
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            "$percent%", 
            fontSize = 9.sp, 
            color = progressColor, 
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun MiniChip(label: String, isDarkMode: Boolean = false) {
    Text(
        text = label,
        modifier = Modifier
            .background((if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        fontSize = 11.sp,
        color = if (isDarkMode) Color.White else AppleTextBlack,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun EmptyState(viewModel: PerfumeViewModel, isDarkMode: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.AutoAwesome, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp),
                tint = (if (isDarkMode) Color.White else AppleTextSecondary).copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                viewModel.t("Deine Sammlung ist leer", "Your collection is empty"), 
                color = if (isDarkMode) Color.White else AppleTextBlack, 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp
            )
            Text(
                viewModel.t("Tippe oben auf das Plus-Icon", "Tap the plus icon above"), 
                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else AppleTextSecondary, 
                fontSize = 14.sp
            )
        }
    }
}
