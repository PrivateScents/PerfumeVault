package com.example.perfumevault.ui.dialogs

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.perfumevault.ui.components.*
import com.example.perfumevault.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createTempPictureUri(context: android.content.Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null && !storageDir.exists()) storageDir.mkdirs()
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        FileProvider.getUriForFile(
            context,
            "com.example.perfumevault.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun saveImageLocally(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "PERFUME_$timeStamp.jpg"
        val file = File(context.filesDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.use { it.copyTo(outputStream) }
        }
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPerfumeDialog(
    viewModel: com.example.perfumevault.viewmodel.PerfumeViewModel,
    onDismiss: () -> Unit,
    onSave: (name: String, brand: String, rating: Double, type: String,
             concentration: String, season: String, occasion: String,
             bottleSize: Int, remainingMl: Double, price: Double, notes: String, imageUrl: String,
             isWishlist: Boolean) -> Unit,
    initialIsWishlist: Boolean = false
) {
    viewModel.currentLanguage.collectAsState() 

    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var ratingStr by remember { mutableStateOf("") }
    var concentration by remember { mutableStateOf("EDP") }
    var season by remember { mutableStateOf("Alle") }
    var occasion by remember { mutableStateOf("Alle") }
    var bottleSizeStr by remember { mutableStateOf("100") }
    var remainingMlStr by remember { mutableStateOf("100") }
    var priceStr by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isWishlist by remember { mutableStateOf(initialIsWishlist) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val adaptive = LocalAdaptiveColors.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val localPath = saveImageLocally(context, it)
                if (localPath != null) {
                    imageUrl = localPath
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let { uri ->
                    val localPath = saveImageLocally(context, uri)
                    if (localPath != null) {
                        imageUrl = localPath
                    }
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempPictureUri(context)
            if (uri != null) {
                tempPhotoUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 24.dp),
        containerColor = adaptive.glassBase,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                if (initialIsWishlist) viewModel.t("Neuer Wunsch", "New Wish") else viewModel.t("Neuer Duft", "New Fragrance"),
                color = adaptive.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = (-0.5).sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMessage != null) {
                    ErrorBanner(errorMessage!!)
                }

                GlassTextField(value = brand, onValueChange = { brand = it }, label = viewModel.t("Marke", "Brand"))
                GlassTextField(value = name, onValueChange = { name = it }, label = viewModel.t("Name", "Name"))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionLabel(viewModel.t("Bild", "Image"))
                    if (imageUrl.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(adaptive.textPrimary.copy(alpha = 0.03f))
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageUrl = "" },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = adaptive.textPrimary)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassSurface(
                            modifier = Modifier.weight(1f),
                            alpha = 0.4f,
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = adaptive.textPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text(viewModel.t("Galerie", "Gallery"), fontSize = 12.sp, color = adaptive.textPrimary)
                            }
                        }
                        GlassSurface(
                            modifier = Modifier.weight(1f),
                            alpha = 0.4f,
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = adaptive.textPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text(viewModel.t("Kamera", "Camera"), fontSize = 12.sp, color = adaptive.textPrimary)
                            }
                        }
                    }
                    GlassTextField(
                        value = if (imageUrl.startsWith("http")) imageUrl else "",
                        onValueChange = { imageUrl = it },
                        label = viewModel.t("Bild URL", "Image URL")
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = ratingStr,
                        onValueChange = { if (it.length <= 4) ratingStr = it },
                        label = viewModel.t("Rating", "Rating"),
                        hint = "(1.0–10.0)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    GlassTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = viewModel.t("Preis", "Price"),
                        hint = "(€)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = bottleSizeStr,
                        onValueChange = { bottleSizeStr = it; errorMessage = null },
                        label = viewModel.t("Größe", "Size"),
                        hint = "(ml)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    GlassTextField(
                        value = remainingMlStr,
                        onValueChange = { remainingMlStr = it; errorMessage = null },
                        label = viewModel.t("Füllstand", "Remaining"),
                        hint = "(ml)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                SectionLabel(viewModel.t("Konzentration", "Concentration"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val concentrations = listOf("EDT", "EDP", "Parfum", "Extrait")
                    items(concentrations) { c ->
                        SelectableChip(c, selected = concentration == c) { concentration = c }
                    }
                }

                SectionLabel(viewModel.t("Jahreszeit", "Season"))
                val selectedSeasons = season.split(" / ").filter { it.isNotBlank() }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val seasons = listOf("Frühling", "Sommer", "Herbst", "Winter")
                    items(seasons) { s ->
                        val isSelected = selectedSeasons.contains(s)
                        SelectableChip(
                            label = viewModel.translateSeason(s), 
                            selected = isSelected
                        ) {
                            val newList = if (isSelected) {
                                selectedSeasons.filter { it != s }
                            } else {
                                selectedSeasons + s
                            }
                            season = if (newList.isEmpty()) "Alle" else newList.joinToString(" / ")
                        }
                    }
                }

                SectionLabel(viewModel.t("Anlass", "Occasion"))
                val selectedOccasions = occasion.split(" / ").filter { it.isNotBlank() }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val occasions = listOf("Alltag", "Büro", "Business", "Date", "Abend", "Ausgehen", "Sport", "Reise", "Besonderer Anlass")
                    items(occasions) { o ->
                        val isSelected = selectedOccasions.contains(o)
                        SelectableChip(
                            label = viewModel.translateOccasion(o), 
                            selected = isSelected
                        ) {
                            val newList = if (isSelected) {
                                selectedOccasions.filter { it != o }
                            } else {
                                selectedOccasions + o
                            }
                            occasion = if (newList.isEmpty()) "Alle" else newList.joinToString(" / ")
                        }
                    }
                }

                if (!initialIsWishlist) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(adaptive.textPrimary.copy(alpha = 0.05f))
                            .clickable { isWishlist = !isWishlist }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            viewModel.t("Auf Merkliste setzen", "Add to Wishlist"),
                            color = adaptive.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = isWishlist,
                            onCheckedChange = { isWishlist = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AppleAccentBlue,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = adaptive.textPrimary.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            HighVisibilityButton(
                text = viewModel.t("Speichern", "Save"),
                onClick = {
                    val bSize = bottleSizeStr.toIntOrNull() ?: 100
                    val rMl = remainingMlStr.replace(",", ".").toDoubleOrNull() ?: bSize.toDouble()
                    val rPrice = priceStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val rRating = ratingStr.replace(",", ".").toDoubleOrNull()?.coerceIn(1.0, 10.0) ?: 7.0

                    if (name.isBlank() || brand.isBlank()) {
                        errorMessage = viewModel.t("Name und Marke benötigt", "Name and Brand required")
                    } else if (rMl > bSize) {
                        errorMessage = viewModel.t(
                            "Füllstand ($rMl ml) kann nicht größer als Flasche ($bSize ml) sein",
                            "Fill level ($rMl ml) cannot exceed bottle size ($bSize ml)"
                        )
                    } else {
                        onSave(
                            name, brand, rRating, "", concentration, season, occasion,
                            bSize, rMl, rPrice, "", imageUrl, isWishlist
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 12.dp),
                containerColor = adaptive.textPrimary,
                contentColor = if (adaptive.isDark) Color.Black else Color.White
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPerfumeDialog(
    perfume: com.example.perfumevault.data.Perfume,
    viewModel: com.example.perfumevault.viewmodel.PerfumeViewModel,
    onDismiss: () -> Unit,
    onSave: (com.example.perfumevault.data.Perfume) -> Unit
) {
    viewModel.currentLanguage.collectAsState() 

    var name by remember { mutableStateOf(perfume.name) }
    var brand by remember { mutableStateOf(perfume.brand) }
    var ratingStr by remember { mutableStateOf(perfume.rating.toString()) }
    var concentration by remember { mutableStateOf(perfume.concentration) }
    var season by remember { mutableStateOf(perfume.season) }
    var occasion by remember { mutableStateOf(perfume.occasion) }
    var bottleSizeStr by remember { mutableStateOf(perfume.bottleSize.toString()) }
    var remainingMlStr by remember { mutableStateOf("%.2f".format(perfume.remainingMl)) }
    var priceStr by remember { mutableStateOf("%.2f".format(perfume.price)) }
    var imageUrl by remember { mutableStateOf(perfume.imageUrl) }
    var isWishlist by remember { mutableStateOf(perfume.isWishlist) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val adaptive = LocalAdaptiveColors.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val localPath = saveImageLocally(context, it)
                if (localPath != null) {
                    imageUrl = localPath
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let { uri ->
                    val localPath = saveImageLocally(context, uri)
                    if (localPath != null) {
                        imageUrl = localPath
                    }
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempPictureUri(context)
            if (uri != null) {
                tempPhotoUri = uri
                cameraLauncher.launch(uri)
            }
        }
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 24.dp),
        containerColor = adaptive.glassBase,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                viewModel.t("Bearbeiten", "Edit"),
                color = adaptive.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = (-0.5).sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (errorMessage != null) {
                    ErrorBanner(errorMessage!!)
                }

                GlassTextField(value = brand, onValueChange = { brand = it }, label = viewModel.t("Marke", "Brand"))
                GlassTextField(value = name, onValueChange = { name = it }, label = viewModel.t("Name", "Name"))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionLabel(viewModel.t("Bild", "Image"))
                    if (imageUrl.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(adaptive.textPrimary.copy(alpha = 0.03f))
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageUrl = "" },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = adaptive.textPrimary)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassSurface(
                            modifier = Modifier.weight(1f),
                            alpha = 0.4f,
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = adaptive.textPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text(viewModel.t("Galerie", "Gallery"), fontSize = 12.sp, color = adaptive.textPrimary)
                            }
                        }
                        GlassSurface(
                            modifier = Modifier.weight(1f),
                            alpha = 0.4f,
                            cornerRadius = 12.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = adaptive.textPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text(viewModel.t("Kamera", "Camera"), fontSize = 12.sp, color = adaptive.textPrimary)
                            }
                        }
                    }
                    GlassTextField(
                        value = if (imageUrl.startsWith("http")) imageUrl else "",
                        onValueChange = { imageUrl = it },
                        label = viewModel.t("Bild URL", "Image URL")
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = ratingStr,
                        onValueChange = { if (it.length <= 4) ratingStr = it },
                        label = viewModel.t("Rating", "Rating"),
                        hint = "(1.0–10.0)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    GlassTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = viewModel.t("Preis", "Price"),
                        hint = "(€)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassTextField(
                        value = bottleSizeStr,
                        onValueChange = { bottleSizeStr = it; errorMessage = null },
                        label = viewModel.t("Größe", "Size"),
                        hint = "(ml)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    GlassTextField(
                        value = remainingMlStr,
                        onValueChange = { remainingMlStr = it; errorMessage = null },
                        label = viewModel.t("Füllstand", "Remaining"),
                        hint = "(ml)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                SectionLabel(viewModel.t("Konzentration", "Concentration"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val concentrations = listOf("EDT", "EDP", "Parfum", "Extrait")
                    items(concentrations) { c ->
                        SelectableChip(c, selected = concentration == c) { concentration = c }
                    }
                }

                SectionLabel(viewModel.t("Jahreszeit", "Season"))
                val selectedSeasons = season.split(" / ").filter { it.isNotBlank() }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val seasons = listOf("Frühling", "Sommer", "Herbst", "Winter")
                    items(seasons) { s ->
                        val isSelected = selectedSeasons.contains(s)
                        SelectableChip(
                            label = viewModel.translateSeason(s), 
                            selected = isSelected
                        ) {
                            val newList = if (isSelected) {
                                selectedSeasons.filter { it != s }
                            } else {
                                selectedSeasons + s
                            }
                            season = if (newList.isEmpty()) "Alle" else newList.joinToString(" / ")
                        }
                    }
                }

                SectionLabel(viewModel.t("Anlass", "Occasion"))
                val selectedOccasions = occasion.split(" / ").filter { it.isNotBlank() }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val occasions = listOf("Alltag", "Büro", "Business", "Date", "Abend", "Ausgehen", "Sport", "Reise", "Besonderer Anlass")
                    items(occasions) { o ->
                        val isSelected = selectedOccasions.contains(o)
                        SelectableChip(
                            label = viewModel.translateOccasion(o), 
                            selected = isSelected
                        ) {
                            val newList = if (isSelected) {
                                selectedOccasions.filter { it != o }
                            } else {
                                selectedOccasions + o
                            }
                            occasion = if (newList.isEmpty()) "Alle" else newList.joinToString(" / ")
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(adaptive.textPrimary.copy(alpha = 0.05f))
                        .clickable { isWishlist = !isWishlist }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        viewModel.t("Auf Merkliste setzen", "Add to Wishlist"),
                        color = adaptive.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = isWishlist,
                        onCheckedChange = { isWishlist = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppleAccentBlue,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = adaptive.textPrimary.copy(alpha = 0.05f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            HighVisibilityButton(
                text = viewModel.t("Update", "Update"),
                onClick = {
                    val bSize = bottleSizeStr.toIntOrNull() ?: perfume.bottleSize
                    val rMl = remainingMlStr.replace(",", ".").toDoubleOrNull()
                    
                    if (name.isBlank() || brand.isBlank()) {
                        errorMessage = viewModel.t("Name und Marke benötigt", "Name and Brand required")
                    } else if (rMl != null && rMl > bSize) {
                        errorMessage = viewModel.t(
                            "Füllstand ($rMl ml) kann nicht größer als Flasche ($bSize ml) sein",
                            "Fill level ($rMl ml) cannot exceed bottle size ($bSize ml)"
                        )
                    } else {
                        onSave(
                            perfume.copy(
                                name = name,
                                brand = brand,
                                rating = ratingStr.replace(",", ".").toDoubleOrNull()?.coerceIn(1.0, 10.0) ?: perfume.rating,
                                type = "",
                                concentration = concentration,
                                season = season,
                                occasion = occasion,
                                bottleSize = bSize,
                                remainingMl = rMl ?: perfume.remainingMl,
                                price = priceStr.replace(",", ".").toDoubleOrNull() ?: perfume.price,
                                notes = "",
                                imageUrl = imageUrl,
                                isWishlist = isWishlist
                            )
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 12.dp),
                containerColor = adaptive.textPrimary,
                contentColor = if (adaptive.isDark) Color.Black else Color.White
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun AddLogDialog(
    perfumeName: String,
    currentRemainingMl: Double,
    viewModel: com.example.perfumevault.viewmodel.PerfumeViewModel,
    onDismiss: () -> Unit,
    onSave: (occasion: String, weather: String, note: String, sprays: Int) -> Unit
) {
    viewModel.currentLanguage.collectAsState() 
    
    // 15 Sprüher = 1ml
    val maxSpraysFromVolume = (currentRemainingMl * 15.0).toInt().coerceIn(1, 50)
    val adaptive = LocalAdaptiveColors.current
    
    val weathers = listOf("☀️ Sonnig", "🌤 Bewölkt", "🌧 Regen", "❄️ Kalt", "🌡 Heiß")
    val occasions = listOf("Alltag", "Business", "Abend", "Date", "Sport", "Reise")

    var weather by remember { mutableStateOf(weathers.first()) }
    var occasion by remember { mutableStateOf(occasions.first()) }
    var note by remember { mutableStateOf("") }
    var sprays by remember { mutableIntStateOf(3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = adaptive.glassBase,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                "${viewModel.t("Duft des Tages", "SotD")}: $perfumeName", 
                color = adaptive.textPrimary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    SectionLabel(viewModel.t("Sprühstöße", "Sprays"))
                    // Showing decimal sprays would be odd, but formatting price/stats below
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (sprays > 1) sprays-- },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(adaptive.textPrimary.copy(alpha = 0.05f))
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = adaptive.textPrimary)
                        }

                        Text(
                            "$sprays",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = adaptive.textPrimary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = { if (sprays < maxSpraysFromVolume) sprays++ },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(adaptive.textPrimary.copy(alpha = 0.05f))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = adaptive.textPrimary)
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Slider(
                        value = sprays.toFloat(),
                        onValueChange = { sprays = it.toInt() },
                        valueRange = 1f..maxSpraysFromVolume.toFloat(),
                        steps = if (maxSpraysFromVolume > 1) maxSpraysFromVolume - 1 else 0,
                        colors = SliderDefaults.colors(
                            thumbColor = adaptive.textPrimary,
                            activeTrackColor = adaptive.textPrimary,
                            inactiveTrackColor = adaptive.textPrimary.copy(alpha = 0.05f)
                        )
                    )
                }

                SectionLabel(viewModel.t("Wetter", "Weather"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(weathers) { w ->
                        SelectableChip(
                            label = when(w) {
                                "☀️ Sonnig" -> viewModel.t("☀️ Sonnig", "☀️ Sunny")
                                "🌤 Bewölkt" -> viewModel.t("🌤 Bewölkt", "🌤 Cloudy")
                                "🌧 Regen" -> viewModel.t("🌧 Regen", "🌧 Rain")
                                "❄️ Kalt" -> viewModel.t("❄️ Kalt", "❄️ Cold")
                                "🌡 Heiß" -> viewModel.t("🌡 Heiß", "🌡 Hot")
                                else -> w
                            }, 
                            selected = weather == w
                        ) { weather = w }
                    }
                }

                SectionLabel(viewModel.t("Anlass", "Occasion"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(occasions) { o ->
                        SelectableChip(
                            label = viewModel.translateOccasion(o), 
                            selected = occasion == o
                        ) { occasion = o }
                    }
                }

                GlassTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = viewModel.t("Notiz zum heutigen Tag", "Note for the day"),
                    singleLine = false,
                    modifier = Modifier.height(80.dp)
                )
            }
        },
        confirmButton = {
            HighVisibilityButton(
                text = viewModel.t("Eintragen", "Log"),
                onClick = { onSave(occasion, weather, note, sprays) },
                modifier = Modifier.padding(bottom = 12.dp),
                containerColor = adaptive.textPrimary,
                contentColor = if (adaptive.isDark) Color.Black else Color.White
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkAddDialog(
    viewModel: com.example.perfumevault.viewmodel.PerfumeViewModel,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val adaptive = LocalAdaptiveColors.current

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
      }
    ]
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = adaptive.glassBase,
        shape = RoundedCornerShape(32.dp),
        title = { 
            Text(
                viewModel.t("Bulk Import", "Bulk Import"), 
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = adaptive.textPrimary
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    viewModel.t(
                        "Füge JSON oder Text (Marke; Name; Größe; Füllstand; Preis) ein.",
                        "Paste JSON or text (Brand; Name; Size; Fill; Price)."
                    ),
                    fontSize = 12.sp,
                    color = adaptive.textPrimary.copy(alpha = 0.5f),
                    lineHeight = 16.sp
                )
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    placeholder = { Text(viewModel.t("Hier einfügen...", "Paste here..."), color = adaptive.textPrimary.copy(alpha = 0.3f)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = adaptive.textPrimary,
                        unfocusedTextColor = adaptive.textPrimary,
                        focusedContainerColor = adaptive.textPrimary.copy(alpha = 0.03f),
                        unfocusedContainerColor = adaptive.textPrimary.copy(alpha = 0.03f),
                        focusedBorderColor = AppleAccentBlue,
                        unfocusedBorderColor = adaptive.textPrimary.copy(alpha = 0.1f)
                    )
                )

                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("PerfumeVault Example", exampleJson)
                        clipboard.setPrimaryClip(clip)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp), tint = AppleAccentBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(viewModel.t("Beispiel JSON kopieren", "Copy Example JSON"), color = AppleAccentBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            HighVisibilityButton(
                text = viewModel.t("Importieren", "Import"),
                onClick = {
                    viewModel.addPerfumesFromText(text)
                    onDismiss()
                },
                containerColor = adaptive.textPrimary,
                contentColor = if (adaptive.isDark) Color.Black else Color.White
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary)
            }
        }
    )
}

@Composable
fun EditLogDialog(
    log: com.example.perfumevault.data.UsageLog,
    currentPerfumeVolume: Double,
    viewModel: com.example.perfumevault.viewmodel.PerfumeViewModel,
    onDismiss: () -> Unit,
    onSave: (com.example.perfumevault.data.UsageLog) -> Unit
) {
    // Calculate how many sprays are available total (current + what was already used in this log)
    val totalAvailableVolume = currentPerfumeVolume + (log.sprays.toDouble() / 15.0)
    val maxAllowedSprays = (totalAvailableVolume * 15.0).toInt().coerceIn(1, 200)
    val adaptive = LocalAdaptiveColors.current

    val weathers = listOf("☀️ Sonnig", "🌤 Bewölkt", "🌧 Regen", "❄️ Kalt", "🌡 Heiß")
    val occasions = listOf("Alltag", "Business", "Abend", "Date", "Sport", "Reise")

    var weather by remember { mutableStateOf(log.weather) }
    var occasion by remember { mutableStateOf(log.occasion) }
    var note by remember { mutableStateOf(log.note) }
    var sprays by remember { mutableIntStateOf(log.sprays) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = adaptive.glassBase,
        shape = RoundedCornerShape(32.dp),
        title = { 
            Text(
                viewModel.t("Eintrag bearbeiten", "Edit Entry"), 
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = adaptive.textPrimary
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    SectionLabel(viewModel.t("Sprühstöße", "Sprays"))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (sprays > 1) sprays-- },
                            modifier = Modifier.background(adaptive.textPrimary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        ) { Icon(Icons.Default.Remove, null, tint = adaptive.textPrimary) }
                        
                        Text(
                            "$sprays", 
                            fontSize = 28.sp, 
                            fontWeight = FontWeight.ExtraBold,
                            color = adaptive.textPrimary
                        )
                        
                        IconButton(
                            onClick = { if (sprays < maxAllowedSprays) sprays++ },
                            modifier = Modifier.background(adaptive.textPrimary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        ) { Icon(Icons.Default.Add, null, tint = adaptive.textPrimary) }
                    }
                    
                    if (sprays >= maxAllowedSprays) {
                        Text(
                            viewModel.t("Max. Kapazität erreicht", "Max volume reached"),
                            fontSize = 10.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                SectionLabel(viewModel.t("Wetter", "Weather"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(weathers) { w ->
                        SelectableChip(
                            label = when(w) {
                                "☀️ Sonnig" -> viewModel.t("☀️ Sonnig", "☀️ Sunny")
                                "🌤 Bewölkt" -> viewModel.t("🌤 Bewölkt", "🌤 Cloudy")
                                "🌧 Regen" -> viewModel.t("🌧 Regen", "🌧 Rain")
                                "❄️ Kalt" -> viewModel.t("❄️ Kalt", "❄️ Cold")
                                "🌡 Heiß" -> viewModel.t("🌡 Heiß", "🌡 Hot")
                                else -> w
                            }, 
                            selected = weather == w
                        ) { weather = w }
                    }
                }

                SectionLabel(viewModel.t("Anlass", "Occasion"))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(occasions) { o ->
                        SelectableChip(viewModel.translateOccasion(o), selected = occasion == o) { occasion = o }
                    }
                }

                GlassTextField(value = note, onValueChange = { note = it }, label = viewModel.t("Notiz", "Note"))
            }
        },
        confirmButton = {
            HighVisibilityButton(
                text = viewModel.t("Speichern", "Save"),
                onClick = {
                    onSave(log.copy(weather = weather, occasion = occasion, note = note, sprays = sprays))
                },
                containerColor = adaptive.textPrimary,
                contentColor = if (adaptive.isDark) Color.Black else Color.White
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(viewModel.t("Abbrechen", "Cancel"), color = adaptive.textSecondary) }
        }
    )
}

@Composable
private fun SectionLabel(text: String) {
    val adaptive = LocalAdaptiveColors.current
    Text(
        text.uppercase(),
        color = adaptive.textPrimary.copy(alpha = 0.9f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Red.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.Error, 
            contentDescription = null, 
            tint = Color.Red, 
            modifier = Modifier.size(20.dp)
        )
        Text(
            message, 
            color = Color.Red,
            fontSize = 13.sp, 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.weight(1f)
        )
    }
}
