package com.perfumevault

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.perfumevault.ui.components.*
import com.perfumevault.ui.dialogs.AddPerfumeDialog
import com.perfumevault.ui.screens.*
import com.perfumevault.ui.theme.*
import com.perfumevault.viewmodel.PerfumeViewModel
import kotlinx.coroutines.launch

private data class AppTab(
    val index: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val APP_TABS = listOf(
    AppTab(0, Icons.AutoMirrored.Filled.List),
    AppTab(1, Icons.Default.Favorite),
    AppTab(2, Icons.Default.DateRange),
    AppTab(3, Icons.Default.Info),
    AppTab(4, Icons.Default.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: PerfumeViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPerfumeId by remember { mutableStateOf<String?>(null) }

    val pagerState = rememberPagerState(pageCount = { APP_TABS.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(selectedTab)
        }
    }

    LaunchedEffect(pagerState.settledPage) {
        if (viewModel.selectedTab.value != pagerState.settledPage) {
            viewModel.setTab(pagerState.settledPage)
        }
    }
    
    PerfumeVaultTheme(darkTheme = isDarkMode) {
        Box(modifier = Modifier.fillMaxSize()) {
            val backgroundBrush = if (isDarkMode) DarkBackground else SoftWhiteBackground
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(backgroundBrush)
            }

            AnimatedContent(
                targetState = selectedPerfumeId,
                label = "ScreenTransition"
            ) { perfumeId ->
                if (perfumeId != null) {
                    DetailScreen(
                        perfumeId = perfumeId,
                        viewModel = viewModel,
                        onBack = { selectedPerfumeId = null },
                        onDelete = { selectedPerfumeId = null }
                    )
                } else {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = when (pagerState.currentPage) {
                                            0 -> viewModel.t("Sammlung", "Collection")
                                            1 -> viewModel.t("Merkliste", "Wishlist")
                                            2 -> viewModel.t("Tagebuch", "Diary")
                                            3 -> viewModel.t("Statistiken", "Statistics")
                                            else -> viewModel.t("Einstellungen", "Settings")
                                        }.uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = (if (isDarkMode) Color.White else AppleTextBlack).copy(alpha = 0.6f),
                                        letterSpacing = 3.sp
                                    )
                                },
                                actions = {
                                    if (pagerState.currentPage == 0 || pagerState.currentPage == 1) {
                                        IconButton(onClick = { showAddDialog = true }) {
                                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(28.dp))
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                            )
                        },
                        bottomBar = {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                                GlassSurface(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                                    Row(modifier = Modifier.fillMaxSize().zIndex(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                                        APP_TABS.forEach { tab ->
                                            IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(tab.index) } }) {
                                                Icon(tab.icon, null, tint = if (selectedTab == tab.index) AppleAccentBlue else Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { paddingValues ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize().padding(paddingValues)
                        ) { page ->
                            Box(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
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
                }
            }

            if (showAddDialog) {
                AddPerfumeDialog(
                    viewModel = viewModel,
                    onDismiss = { showAddDialog = false },
                    initialIsWishlist = selectedTab == 1,
                    onSave = { name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist, allSizes ->
                        viewModel.addPerfume(name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist, allSizes)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}
