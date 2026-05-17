package com.example.perfumevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.perfumevault.data.PerfumeDatabase
import com.example.perfumevault.repository.PerfumeRepository
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.ui.dialogs.AddPerfumeDialog
import com.example.perfumevault.ui.screens.*
import com.example.perfumevault.viewmodel.PerfumeViewModel
import com.example.perfumevault.viewmodel.PerfumeViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: PerfumeViewModel by viewModels {
        val db = PerfumeDatabase.getDatabase(application)
        PerfumeViewModelFactory(PerfumeRepository(db.perfumeDao(), db.usageLogDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerfumeVaultApp(viewModel)
        }
    }
}

// ──────────────────────────────────────────────────────────────
// NAV TABS
// ──────────────────────────────────────────────────────────────
private data class NavTab(
    val index: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val NAV_TABS = listOf(
    NavTab(0, Icons.Default.ViewCarousel),      // Sammlung
    NavTab(1, Icons.Default.AutoAwesome),       // Merkliste
    NavTab(2, Icons.AutoMirrored.Filled.MenuBook), // Tagebuch
    NavTab(3, Icons.Default.BarChart),          // Statistik
    NavTab(4, Icons.Default.Settings)           // Settings
)

// ──────────────────────────────────────────────────────────────
// ROOT APP
// ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PerfumeVaultApp(viewModel: PerfumeViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    viewModel.currentLanguage.collectAsState() // Observe for recomposition
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPerfumeId by remember { mutableStateOf<Int?>(null) }

    // System Back Logic
    BackHandler(enabled = selectedPerfumeId != null) {
        selectedPerfumeId = null
    }

    val pagerState = rememberPagerState(pageCount = { NAV_TABS.size })

    // Sync PagerState with ViewModel selectedTab
    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab) {
            pagerState.scrollToPage(selectedTab)
        }
    }

    // Sync ViewModel selectedTab with PagerState
    LaunchedEffect(pagerState.currentPage) {
        if (selectedTab != pagerState.currentPage) {
            viewModel.setTab(pagerState.currentPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val cx = size.width
                val cy = size.height
                
                // Pure Light Background
                drawRect(Color(0xFFF9F9FB))
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE8E8ED).copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(cx * 0.8f, cy * 0.2f),
                        radius = cx
                    ),
                    radius = cx,
                    center = Offset(cx * 0.8f, cy * 0.2f)
                )
            }
    ) {
        if (selectedPerfumeId != null) {
            DetailScreen(
                perfumeId = selectedPerfumeId!!,
                viewModel = viewModel,
                onBack = { selectedPerfumeId = null },
                onDelete = {
                    selectedPerfumeId = null
                }
            )
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            var isVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(selectedTab) {
                                isVisible = false
                                kotlinx.coroutines.delay(50)
                                isVisible = true
                            }
                            
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn() + slideInVertically { it / 2 },
                                exit = fadeOut()
                            ) {
                                Text(
                                    when (selectedTab) {
                                        0 -> viewModel.t("Sammlung", "Collection")
                                        1 -> viewModel.t("Merkliste", "Wishlist")
                                        2 -> viewModel.t("Tagebuch", "Diary")
                                        3 -> viewModel.t("Statistiken", "Statistics")
                                        else -> viewModel.t("Einstellungen", "Settings")
                                    }.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = AppleTextBlack.copy(alpha = 0.6f),
                                    letterSpacing = 3.sp
                                )
                            }
                        },
                        actions = {
                            AnimatedVisibility(selectedTab == 0 || selectedTab == 1) {
                                IconButton(
                                    onClick = { showAddDialog = true },
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = viewModel.t("Hinzufügen", "Add"),
                                        tint = AppleTextBlack,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        GlassSurface(
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                            cornerRadius = 36.dp,
                            alpha = 0.6f
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                NAV_TABS.forEach { tab ->
                                    val selected = selectedTab == tab.index
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clickable(
                                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                                indication = null
                                            ) { viewModel.setTab(tab.index) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(26.dp),
                                                tint = if (selected) AppleTextBlack else AppleTextBlack.copy(alpha = 0.3f)
                                            )
                                            if (selected) {
                                                Spacer(Modifier.height(4.dp))
                                                Box(
                                                    Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(AppleTextBlack)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalAlignment = Alignment.Top,
                    beyondViewportPageCount = 1,
                    flingBehavior = androidx.compose.foundation.pager.PagerDefaults.flingBehavior(
                        state = pagerState,
                        pagerSnapDistance = androidx.compose.foundation.pager.PagerSnapDistance.atMost(1)
                    )
                ) { page ->
                    when (page) {
                        0 -> ShelfScreen(viewModel) { perfume -> selectedPerfumeId = perfume.id }
                        1 -> WishlistScreen(viewModel) { perfume -> selectedPerfumeId = perfume.id }
                        2 -> DiaryScreen(viewModel) { id -> selectedPerfumeId = id }
                        3 -> StatsScreen(viewModel) { id -> selectedPerfumeId = id }
                        4 -> SettingsScreen(viewModel)
                    }
                }
            }
        }

        if (showAddDialog) {
            AddPerfumeDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                initialIsWishlist = selectedTab == 1,
                onSave = { name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist ->
                    viewModel.addPerfume(name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist)
                    showAddDialog = false
                }
            )
        }
    }
}
