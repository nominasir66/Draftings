package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.typography.FontHelper
import com.example.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ترتیبات (Settings)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = nastaleeqFont,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Typography & Editor
                item {
                    Text(
                        text = "خطاطی و صفحہ (TYPOGRAPHY & LAYOUT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Default Font
                            Column {
                                Text(
                                    text = "پہلے سے طے شدہ فونٹ (Default Font):",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont, fontSize = 16.sp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FontHelper.AVAILABLE_FONTS.take(2).forEach { (fontKey, label) ->
                                        FilterChip(
                                            selected = settings.defaultFontFamily == fontKey,
                                            onClick = { viewModel.setFontFamily(fontKey) },
                                            shape = RoundedCornerShape(100),
                                            label = {
                                                Text(
                                                    if (fontKey == FontHelper.FONT_JAMEEL_NASTALEEQ) "جمیل نوری نستعلیق" else "خط نسخ",
                                                    fontFamily = nastaleeqFont,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Default Font Size
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "متن کا سائز (Default Font Size):",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont, fontSize = 16.sp)
                                    )
                                    Text(
                                        text = "${settings.defaultFontSizeSp.toInt()} pt",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Slider(
                                    value = settings.defaultFontSizeSp,
                                    onValueChange = { viewModel.setFontSize(it) },
                                    valueRange = 14f..28f,
                                    steps = 6
                                )
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Default Margins
                            Column {
                                Text(
                                    text = "صفحے کے قانونی حاشیے (Legal Margins):",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont, fontSize = 16.sp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(15f to "15 mm", 20f to "20 mm (معیاری)", 25f to "25 mm (کشادہ)").forEach { (margin, label) ->
                                        FilterChip(
                                            selected = settings.defaultMarginMm == margin,
                                            onClick = { viewModel.setMargin(margin) },
                                            shape = RoundedCornerShape(100),
                                            label = { Text(label, fontFamily = nastaleeqFont, fontSize = 13.sp) }
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Margin Guides
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "حاشیے کی علامتی لکیریں دکھائیں (Margin Guidelines):",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont, fontSize = 16.sp)
                                )
                                Switch(
                                    checked = settings.showMarginGuidesDefault,
                                    onCheckedChange = { viewModel.setMarginGuides(it) }
                                )
                            }
                        }
                    }
                }

                // Section 2: Appearance & Theme
                item {
                    Text(
                        text = "تھیم و وضع قطع (THEME & APPEARANCE)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "ایپ تھیم (App Theme):",
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont, fontSize = 16.sp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = settings.isDarkMode == false,
                                    onClick = { viewModel.setDarkMode(false) },
                                    shape = RoundedCornerShape(100),
                                    label = { Text("روشن (Light)", fontFamily = nastaleeqFont) }
                                )
                                FilterChip(
                                    selected = settings.isDarkMode == true,
                                    onClick = { viewModel.setDarkMode(true) },
                                    shape = RoundedCornerShape(100),
                                    label = { Text("تاریک (Dark)", fontFamily = nastaleeqFont) }
                                )
                                FilterChip(
                                    selected = settings.isDarkMode == null,
                                    onClick = { viewModel.setDarkMode(null) },
                                    shape = RoundedCornerShape(100),
                                    label = { Text("خودکار سسٹم", fontFamily = nastaleeqFont) }
                                )
                            }
                        }
                    }
                }

                // Section 3: About Draftings
                item {
                    Text(
                        text = "ایپ کے بارے میں (ABOUT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("ڈ", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = nastaleeqFont)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Draftings (اردو قانونی ڈرافٹنگز)",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Geometric Balance Edition • v1.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "مستند اردو قانونی و عدالتی دستاویزات کی تیاری کے لیے پیشہ ورانہ موبائل ورڈ پروسیسر۔ حقیقی A4 پی ڈی ایف ایکسپورٹ، مائیکروسافٹ ورڈ (DOCX) فائلز، اور جمیل نوری نستعلیق خطاطی کی مکمل معاونت۔",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = nastaleeqFont,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
