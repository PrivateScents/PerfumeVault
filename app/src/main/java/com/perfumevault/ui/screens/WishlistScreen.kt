package com.perfumevault.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.perfumevault.BuildConfig
import com.perfumevault.data.Perfume
import com.perfumevault.ui.components.*
import com.perfumevault.ui.theme.*
import com.perfumevault.viewmodel.PerfumeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WishlistScreen(
    viewModel: PerfumeViewModel,
    onPerfumeClick: (Perfume) -> Unit
) {
    val perfumes by viewModel.wishlistPerfumes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterSamples by viewModel.filterSamples.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val adaptive = LocalAdaptiveColors.current
    val scope = rememberCoroutineScope()

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
                                        viewModel.t("Wünsche durchsuchen...", "Search wishes..."),
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

        // ── Filterleiste (Proben)
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
        }

        Spacer(Modifier.height(8.dp))

        if (perfumes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text(
                        "✦", 
                        fontSize = 48.sp, 
                        color = adaptive.textPrimary.copy(alpha = 0.1f)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        viewModel.t("Keine Wünsche gefunden", "No wishes found"), 
                        color = adaptive.textPrimary, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            val bottomPadding = if (BuildConfig.FLAVOR == "public" && !isAdFree) 200.dp else 120.dp
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = bottomPadding),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items = perfumes, key = { it.id }) { perfume ->
                    WishlistCard(
                        perfume = perfume,
                        viewModel = viewModel,
                        onClick = { onPerfumeClick(perfume) },
                        onBuy = { 
                            scope.launch {
                                viewModel.moveToCollection(perfume)
                            }
                        },
                        onDelete = { viewModel.deletePerfume(perfume) }
                    )
                }
            }
        }
    }
}

@Composable
fun WishlistCard(
    modifier: Modifier = Modifier,
    perfume: Perfume,
    viewModel: PerfumeViewModel,
    onClick: () -> Unit,
    onBuy: () -> Unit,
    onDelete: () -> Unit
) {
    val adaptive = LocalAdaptiveColors.current
    var isMoving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val density = LocalDensity.current
    val moveAmount = remember(density) { with(density) { -400.dp.toPx() } }
    
    val offsetX by animateFloatAsState(
        targetValue = if (isMoving) moveAmount else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "moveLeft"
    )
    
    val opacity by animateFloatAsState(
        targetValue = if (isMoving) 0f else 1f,
        animationSpec = tween(500),
        label = "fade"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer {
                translationX = offsetX
                alpha = opacity
            }
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
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
                            Icons.Default.ShoppingBag, 
                            null, 
                            modifier = Modifier.size(32.dp).align(Alignment.Center), 
                            tint = adaptive.textPrimary.copy(alpha = 0.1f)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            perfume.brand.uppercase(),
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
                        color = adaptive.textPrimary
                    )
                    
                    if (perfume.season.isNotEmpty() && perfume.season != "Alle") {
                        Spacer(Modifier.height(4.dp))
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
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                isMoving = true
                                scope.launch {
                                    delay(600)
                                    onBuy()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = adaptive.textPrimary,
                                contentColor = if (adaptive.isDark) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(viewModel.t("Gerade gekauft", "Just Bought"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline, 
                                null, 
                                tint = Color.Red.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Indicator Dots removed as per user request
    }
}
