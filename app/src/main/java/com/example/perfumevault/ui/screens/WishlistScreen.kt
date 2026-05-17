package com.example.perfumevault.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.viewmodel.PerfumeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun WishlistScreen(
    viewModel: PerfumeViewModel,
    onPerfumeClick: (Perfume) -> Unit
) {
    val perfumes by viewModel.wishlistPerfumes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { 
                Text(
                    viewModel.t("Wunschliste durchsuchen…", "Search wishlist..."), 
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

        if (perfumes.isEmpty()) {
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
                        viewModel.t("Keine Wünsche gefunden", "No wishes found"), 
                        color = if (isDarkMode) Color.White else AppleTextBlack, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(perfumes, key = { _, p -> p.id }) { index, perfume ->
                    var isEntryVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { isEntryVisible = true }

                    val enterScale by animateFloatAsState(
                        targetValue = if (isEntryVisible) 1f else 0.9f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
                        label = "entryScale"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (isEntryVisible) 1f else 0f,
                        animationSpec = tween(500, delayMillis = (index * 40).coerceAtMost(400)),
                        label = "entryAlpha"
                    )

                    WishlistCard(
                        modifier = Modifier.graphicsLayer {
                            scaleX = enterScale
                            scaleY = enterScale
                            this.alpha = alpha
                        },
                        perfume = perfume,
                        isDarkMode = isDarkMode,
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
    isDarkMode: Boolean,
    viewModel: PerfumeViewModel,
    onClick: () -> Unit,
    onBuy: () -> Unit,
    onDelete: () -> Unit
) {
    var isMoving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val density = LocalDensity.current
    val moveAmount = with(density) { -400.dp.toPx() } 
    
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

    PressableGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer {
                translationX = offsetX
                alpha = opacity
            },
        onClick = onClick,
        isDarkMode = isDarkMode
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                        Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = (if (isDarkMode) Color.White else AppleTextSecondary).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    perfume.brand.uppercase(),
                    fontSize = 10.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else AppleTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    perfume.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else AppleTextBlack,
                    lineHeight = 26.sp,
                    letterSpacing = (-0.5).sp
                )
                
                Spacer(Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            containerColor = if (isDarkMode) Color.White else Color.Black,
                            contentColor = if (isDarkMode) Color.Black else Color.White
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
                            contentDescription = null, 
                            tint = Color.Red.copy(alpha = 0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
