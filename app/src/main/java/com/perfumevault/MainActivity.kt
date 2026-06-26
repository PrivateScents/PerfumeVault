package com.perfumevault

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.perfumevault.data.PerfumeDatabase
import com.perfumevault.repository.PerfumeRepository
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.zIndex
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.util.lerp
import com.perfumevault.ui.components.*
import com.perfumevault.ui.dialogs.AddPerfumeDialog
import com.perfumevault.ui.screens.*
import com.perfumevault.ui.theme.*
import com.perfumevault.viewmodel.PerfumeViewModel
import com.perfumevault.viewmodel.PerfumeViewModelFactory
import com.google.android.gms.ads.MobileAds
import androidx.work.*
import com.perfumevault.util.DailyReminderWorker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    private val viewModel: PerfumeViewModel by viewModels {
        val db = PerfumeDatabase.getDatabase(application)
        PerfumeViewModelFactory(application, PerfumeRepository(db.perfumeDao(), db.usageLogDao(), db.catalogDao(), db.perfumeSizeDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (BuildConfig.FLAVOR == "public") {
            MobileAds.initialize(this) {}
        }

        // Benachrichtigungs-Berechtigung anfragen (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    scheduleDailyReminder()
                }
            }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            scheduleDailyReminder()
        }

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            PerfumeVaultTheme(darkTheme = isDarkMode) {
                PerfumeVaultApp(viewModel)
            }
        }
    }

    private fun scheduleDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Berechne Delay für 7:00 Uhr morgens
        val calendar = Calendar.getInstance()
        val nowMillis = calendar.timeInMillis
        
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 7) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        val delay = calendar.timeInMillis - nowMillis

        val periodicRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_perfume_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }
}

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

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun PerfumeVaultApp(viewModel: PerfumeViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAdFree by viewModel.isAdFree.collectAsState()
    val tourStep by viewModel.tourStep.collectAsState()
    val showTour by viewModel.showTour.collectAsState()
    
    val addBtnRect = remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }
    val navRects = remember { mutableStateMapOf<Int, androidx.compose.ui.geometry.Rect>() }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPerfumeId by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = selectedPerfumeId != null) {
        selectedPerfumeId = null
    }

    val pagerState = rememberPagerState(pageCount = { NAV_TABS.size })
    val scope = rememberCoroutineScope()

    // Sync PagerState with ViewModel selectedTab (External changes like Tour)
    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(selectedTab)
        }
    }

    // Sync ViewModel selectedTab with PagerState (User Swipes)
    LaunchedEffect(pagerState.settledPage) {
        if (viewModel.selectedTab.value != pagerState.settledPage) {
            viewModel.setTab(pagerState.settledPage)
        }
    }
    
    // Auto-switch tabs during tour for better context
    LaunchedEffect(tourStep, showTour) {
        if (showTour) {
            when (tourStep) {
                0 -> if (selectedTab != 0) viewModel.setTab(0)
                1 -> if (selectedTab != 0) viewModel.setTab(0)
                2 -> if (selectedTab != 2) viewModel.setTab(2)
                3 -> if (selectedTab != 3) viewModel.setTab(3)
                4 -> if (selectedTab != 4) viewModel.setTab(4)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Static Background Layer
        val backgroundBrush = remember(isDarkMode) {
            if (isDarkMode) DarkBackground else SoftWhiteBackground
        }
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width
            val cy = size.height

            drawRect(backgroundBrush)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isDarkMode) {
                        listOf(Color(0xFF3D4466).copy(alpha = 0.5f), Color.Transparent)
                    } else {
                        listOf(Color(0xFF749BC2).copy(alpha = 0.12f), Color.Transparent)
                    },
                    center = Offset(cx * 0.1f, cy * 0.8f),
                    radius = cx * 1.2f
                ),
                radius = cx * 1.2f,
                center = Offset(cx * 0.1f, cy * 0.8f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isDarkMode) {
                        listOf(Color(0xFF8B93C7).copy(alpha = 0.4f), Color.Transparent)
                    } else {
                        listOf(Color(0xFFF1C27B).copy(alpha = 0.12f), Color.Transparent)
                    },
                    center = Offset(cx * 0.9f, cy * 0.1f),
                    radius = cx * 0.8f
                ),
                radius = cx * 0.8f,
                center = Offset(cx * 0.9f, cy * 0.1f)
            )
        }

        // Animated Screen Transition
        AnimatedContent(
            targetState = selectedPerfumeId,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it / 2 } + fadeOut())
                } else {
                    (slideInHorizontally { -it / 2 } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                }
            },
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
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = {
                                    var isVisible by remember { mutableStateOf(false) }
                                    LaunchedEffect(pagerState.currentPage) {
                                        isVisible = false
                                        kotlinx.coroutines.delay(50.milliseconds)
                                        isVisible = true
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AnimatedVisibility(
                                            visible = isVisible,
                                            enter = fadeIn() + slideInVertically { it / 2 },
                                            exit = fadeOut(),
                                            modifier = Modifier.weight(1f)
                                        ) {
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
                                        }
                                    }
                                },
                                actions = {
                                    AnimatedVisibility(pagerState.currentPage == 0 || pagerState.currentPage == 1) {
                                        IconButton(
                                            onClick = { showAddDialog = true },
                                            modifier = Modifier
                                                .padding(end = 12.dp)
                                                .onGloballyPositioned { coords ->
                                                    addBtnRect.value = coords.boundsInRoot()
                                                }
                                        ) {
                                            Icon(
                                                Icons.Filled.Add,
                                                contentDescription = viewModel.t("Hinzufügen", "Add"),
                                                tint = if (isDarkMode) Color.White else AppleTextBlack,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                            )
                        }
                    ) { paddingValues ->
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = paddingValues.calculateTopPadding()),
                            verticalAlignment = Alignment.Top,
                            beyondViewportPageCount = 1
                        ) { page ->
                            // Animation Effect
                            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            
                            // Add bottom padding to ensure content is not hidden by the glass bar
                            val bottomPadding = if (BuildConfig.FLAVOR == "public" && !isAdFree) 160.dp else 100.dp
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    val scale = 1f - (pageOffset.absoluteValue * 0.15f).coerceIn(0f, 1f)
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = 1f - (pageOffset.absoluteValue * 0.5f).coerceIn(0f, 1f)
                                }
                                .padding(bottom = bottomPadding)
                            ) {
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

                    // Glass Navigation Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .zIndex(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (BuildConfig.FLAVOR == "public" && !isAdFree) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .padding(bottom = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AdBanner()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 4.dp)
                        ) {
                            val config = androidx.compose.ui.platform.LocalConfiguration.current
                            val navBarWidth = config.screenWidthDp.dp - 48.dp
                            val tabWidth = navBarWidth / NAV_TABS.size
                            
                            val scrollPosition by remember { 
                                derivedStateOf { pagerState.currentPage + pagerState.currentPageOffsetFraction } 
                            }

                            val stretch by remember {
                                derivedStateOf {
                                    val offset = pagerState.currentPageOffsetFraction
                                    val absOffset = if (offset < 0) -offset else offset
                                    (absOffset * (1f - absOffset) * 4f).coerceIn(0f, 1f)
                                }
                            }
                            
                            val baseIndicatorWidth = tabWidth * 0.75f
                            val stretchFactor = 1f + (stretch * 0.35f)

                            GlassSurface(
                                modifier = Modifier.fillMaxWidth().height(64.dp),
                                cornerRadius = 32.dp,
                                alpha = if (isDarkMode) 0.5f else 0.4f
                            ) {
                                val indicatorColor = if (isDarkMode) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.05f)
                                
                                // Hintergrund-Indikator (Nicht interaktiv)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .graphicsLayer {
                                            translationX = (scrollPosition * tabWidth.toPx()) + (tabWidth.toPx() - baseIndicatorWidth.toPx()) / 2
                                            scaleX = stretchFactor
                                            clip = true
                                            shape = RoundedCornerShape(28.dp)
                                        }
                                        .width(baseIndicatorWidth)
                                        .fillMaxHeight(0.75f)
                                        .background(indicatorColor)
                                )

                                // Klickbare Icons (Interaktiv)
                                Row(
                                    modifier = Modifier.fillMaxSize().zIndex(2f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    NAV_TABS.forEach { tab ->
                                        val pageOffset = (pagerState.currentPage - tab.index) + pagerState.currentPageOffsetFraction
                                        val animationProgress = (1f - (pageOffset.coerceIn(-1f, 1f).let { if (it < 0) -it else it })).coerceIn(0f, 1f)
                                        
                                        val iconTint = if (isDarkMode) {
                                            lerp(Color.White.copy(alpha = 0.35f), Color.White, animationProgress)
                                        } else {
                                            lerp(AppleTextBlack.copy(alpha = 0.35f), AppleAccentBlue, animationProgress)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .onGloballyPositioned { coords ->
                                                    navRects[tab.index] = coords.boundsInRoot()
                                                }
                                                .clickable(
                                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                                    indication = null
                                                ) { 
                                                    scope.launch {
                                                        pagerState.animateScrollToPage(tab.index)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .graphicsLayer {
                                                        scaleX = 1f + (0.12f * animationProgress)
                                                        scaleY = 1f + (0.12f * animationProgress)
                                                    },
                                                tint = iconTint
                                            )
                                        }
                                    }
                                }
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
                onSave = { name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist, allSizes, isSample ->
                    viewModel.addPerfume(name, brand, rating, type, concentration, season, occasion, bottleSize, remainingMl, price, notes, imageUrl, isWishlist, allSizes, isSample)
                    showAddDialog = false
                }
            )
        }

        if (showTour) {
            FeatureTour(
                viewModel = viewModel,
                step = tourStep,
                addBtnRect = addBtnRect.value,
                navRects = navRects,
                onNext = { viewModel.nextTourStep() },
                onDismiss = { viewModel.dismissTour() }
            )
        }
    }
}
