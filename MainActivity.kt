package com.example.perfumevault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.perfumevault.ui.components.AppleTextBlack
import com.example.perfumevault.ui.components.AppleTextSecondary
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
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            com.example.perfumevault.ui.theme.PerfumeVaultTheme(darkTheme = isDarkMode) {
                PerfumeVaultApp(viewModel)
            }
        }
    }
}

private data class NavTab(
    val index: Int,
    val label: String
)

private val NAV_TABS = listOf(
    NavTab(0, "Sammlung"),
    NavTab(1, "Tagebuch"),
    NavTab(2, "Statistik"),
    NavTab(3, "Settings")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfumeVaultApp(viewModel: PerfumeViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState() 
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPerfumeId by remember { mutableStateOf<Int?>(null) }

    val bgColor = if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
    val textColor = if (isDarkMode) Color(0xFFF2F2F7) else AppleTextBlack
    val secondaryTextColor = if (isDarkMode) Color(0xFFAEB2B8) else AppleTextSecondary
    val glassColor = if (isDarkMode) Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(bgColor) 
                val cx = size.width
                val cy = size.height

                // Soft pastel gradients for glass depth
                if (!isDarkMode) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFD1D1D6).copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(cx * 0.2f, cy * 0.2f),
                            radius = cx * 1.5f
                        ),
                        radius = cx * 1.5f,
                        center = Offset(cx * 0.2f, cy * 0.2f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE5E5EA).copy(alpha = 0.6f), Color.Transparent),
                            center = Offset(cx * 0.8f, cy * 0.8f),
                            radius = cx * 1.2f
                        ),
                        radius = cx * 1.2f,
                        center = Offset(cx * 0.8f, cy * 0.8f)
                    )
                } else {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF2C2C2E).copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(cx * 0.2f, cy * 0.2f),
                            radius = cx * 1.5f
                        ),
                        radius = cx * 1.5f,
                        center = Offset(cx * 0.2f, cy * 0.2f)
                    )
                }
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
                            Column {
                                Text(
                                    "PerfumeVault",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = textColor,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    when (selectedTab) {
                                        0 -> viewModel.t("Sammlung", "Collection")
                                        1 -> viewModel.t("Tagebuch", "Diary")
                                        2 -> viewModel.t("Statistiken", "Statistics")
                                        else -> viewModel.t("Einstellungen", "Settings")
                                    },
                                    fontSize = 12.sp,
                                    color = secondaryTextColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        actions = {
                            AnimatedVisibility(selectedTab == 0) {
                                IconButton(onClick = { showAddDialog = true }) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(textColor.copy(alpha = 0.05f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Hinzufügen",
                                            tint = textColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
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
                            .padding(horizontal = 32.dp, vertical = 20.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(glassColor) 
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier.height(64.dp)
                        ) {
                            NAV_TABS.forEach { tab ->
                                val label = when(tab.index) {
                                    0 -> viewModel.t("Sammlung", "Collection")
                                    1 -> viewModel.t("Tagebuch", "Diary")
                                    2 -> viewModel.t("Statistik", "Stats")
                                    else -> viewModel.t("Settings", "Settings")
                                }
                                NavigationBarItem(
                                    icon = { /* No icon */ },
                                    label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    selected = selectedTab == tab.index,
                                    onClick = { viewModel.setTab(tab.index) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Transparent,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = Color.Transparent,
                                        unselectedTextColor = secondaryTextColor,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it / 3 } + fadeIn() togetherWith
                                        slideOutHorizontally { -it / 3 } + fadeOut()
                            } else {
                                slideInHorizontally { -it / 3 } + fadeIn() togetherWith
                                        slideOutHorizontally { it / 3 } + fadeOut()
                            }
                        },
                        label = "tabContent"
                    ) { tab ->
                        when (tab) {
                            0 -> ShelfScreen(viewModel) { perfume -> selectedPerfumeId = perfume.id }
                            1 -> DiaryScreen(viewModel) { id -> selectedPerfumeId = id }
                            2 -> StatsScreen(viewModel) { id -> selectedPerfumeId = id }
                            3 -> SettingsScreen(viewModel)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddPerfumeDialog(
                viewModel = viewModel,
                onDismiss = { showAddDialog = false },
                onSave = { name, brand, rating, type, concentration, season, occasion, bottleSize, price, notes, imageUrl ->
                    viewModel.addPerfume(name, brand, rating, type, concentration, season, occasion, bottleSize, price, notes, imageUrl)
                    showAddDialog = false
                }
            )
        }
    }
}
