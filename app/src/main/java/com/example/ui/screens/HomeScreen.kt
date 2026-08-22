package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DocumentEntity
import com.example.data.DocumentRepository
import com.example.data.AppDatabase
import com.example.export.DocxExporter
import com.example.export.PdfExporter
import com.example.export.PrintAndShareHelper
import com.example.model.DocumentCategory
import com.example.model.LegalTemplate
import com.example.model.LegalTemplates
import com.example.typography.FontHelper
import com.example.viewmodel.HomeUiState
import com.example.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenDocument: (Long) -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isSearchActive by remember { mutableStateOf(false) }
    var showNewDocDialog by remember { mutableStateOf(false) }
    var renameDocId by remember { mutableStateOf<Long?>(null) }
    var renameTitleText by remember { mutableStateOf("") }

    val totalDocsCount = uiState.documents.size
    val favoriteDocsCount = uiState.documents.count { it.isFavorite }
    val totalPagesCount = uiState.documents.sumOf { it.pageCount }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Drawer / Menu icon button
                    IconButton(
                        onClick = onOpenTemplates,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = "Templates",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pill Search Field
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (uiState.searchQuery.isEmpty()) {
                                    Text(
                                        text = "مسودات میں تلاش کریں…",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = nastaleeqFont,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                                BasicTextField(
                                    value = uiState.searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = nastaleeqFont,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onSearchQueryChanged("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Settings / Profile Avatar Button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "ڈ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = nastaleeqFont
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewDocDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Document",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Geometric Balance Hero Card (Project / Legal Compliance Header)
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shadowElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "اردو قانونی ڈرافٹنگز",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontFamily = nastaleeqFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                lineHeight = 32.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "معیاری عدالتی فارمیٹ و نستعلیق پرنٹنگ",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = nastaleeqFont,
                                                fontSize = 14.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(100),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            text = "فعال A4",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = nastaleeqFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                        )
                                    }
                                }

                                // Geometric Capacity / Progress Indicator
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "قانونی ضوابط اور فارمیٹنگ کی تعمیل",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = nastaleeqFont,
                                                fontSize = 13.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "100%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp),
                                        shape = RoundedCornerShape(100),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(1f)
                                                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(100))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Key Metrics Section (Geometric 2-Card Layout)
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "اہم اعداد و شمار (KEY METRICS)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = onOpenTemplates,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "ٹیمپلیٹس دیکھیں",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = nastaleeqFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Metric Card 1: Total Specs / Drafts
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "کل مسودات",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = nastaleeqFont,
                                                fontSize = 13.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$totalDocsCount",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                // Metric Card 2: Approved / Starred
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "پسندیدہ / تصدیق شدہ",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = nastaleeqFont,
                                                fontSize = 13.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$favoriteDocsCount",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp
                                            ),
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Category Filter Chips Row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DocumentCategory.values().forEach { category ->
                                val isSelected = uiState.selectedCategory == category
                                Surface(
                                    shape = RoundedCornerShape(100),
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier.clickable { viewModel.onCategorySelected(category) }
                                ) {
                                    Text(
                                        text = category.displayNameUrdu,
                                        fontFamily = nastaleeqFont,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Templates Row
                    if (uiState.searchQuery.isBlank() && uiState.selectedCategory == DocumentCategory.ALL) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "فوری قانونی نمونہ جات (QUICK TEMPLATES)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(LegalTemplates.allTemplates.take(4)) { template ->
                                        Surface(
                                            modifier = Modifier
                                                .width(160.dp)
                                                .clickable {
                                                    viewModel.createFromTemplate(template) { newId ->
                                                        onOpenDocument(newId)
                                                    }
                                                },
                                            shape = RoundedCornerShape(16.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.NoteAdd,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = template.titleUrdu,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontFamily = nastaleeqFont,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = template.titleEnglish,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section Title: Recent Documents
                    item {
                        Text(
                            text = "حالیہ مسودات (RECENT DOCUMENTS)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Main Documents Content
                    if (uiState.documents.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (uiState.searchQuery.isNotBlank()) "کوئی مسودہ نہیں ملا" else "کوئی محفوظ شدہ مسودہ موجود نہیں",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = nastaleeqFont,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    Text(
                                        text = "نیا مسودہ شروع کریں یا ٹیمپلیٹس میں سے انتخاب کریں",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = nastaleeqFont,
                                            fontSize = 14.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = {
                                            viewModel.createNewBlankDocument { newId ->
                                                onOpenDocument(newId)
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("خالی صفحہ (Blank)", fontFamily = nastaleeqFont, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        items(uiState.documents, key = { it.id }) { doc ->
                            DocumentCard(
                                document = doc,
                                onClick = { onOpenDocument(doc.id) },
                                onFavoriteToggle = { viewModel.toggleFavorite(doc.id, doc.isFavorite) },
                                onRename = {
                                    renameDocId = doc.id
                                    renameTitleText = doc.title
                                },
                                onDuplicate = { viewModel.duplicateDocument(doc.id) },
                                onDelete = { viewModel.deleteDocument(doc.id) },
                                onExportPdf = {
                                    coroutineScope.launch {
                                        val repo = DocumentRepository(AppDatabase.getInstance(context).documentDao())
                                        val fullDoc = repo.getDocumentById(doc.id)
                                        if (fullDoc != null) {
                                            val pdfFile = PdfExporter.exportToPdf(context, fullDoc)
                                            PrintAndShareHelper.sharePdf(context, pdfFile, fullDoc.title)
                                        }
                                    }
                                },
                                onExportDocx = {
                                    coroutineScope.launch {
                                        val repo = DocumentRepository(AppDatabase.getInstance(context).documentDao())
                                        val fullDoc = repo.getDocumentById(doc.id)
                                        if (fullDoc != null) {
                                            val docxFile = DocxExporter.exportToDocx(context, fullDoc)
                                            PrintAndShareHelper.shareDocx(context, docxFile, fullDoc.title)
                                        }
                                    }
                                },
                                onPrint = {
                                    coroutineScope.launch {
                                        val repo = DocumentRepository(AppDatabase.getInstance(context).documentDao())
                                        val fullDoc = repo.getDocumentById(doc.id)
                                        if (fullDoc != null) {
                                            val pdfFile = PdfExporter.exportToPdf(context, fullDoc)
                                            PrintAndShareHelper.printPdf(context, pdfFile, fullDoc.title)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // New Document Dialog
    if (showNewDocDialog) {
        var docTitle by remember { mutableStateOf("نیا قانونی مسودہ") }
        AlertDialog(
            shape = RoundedCornerShape(24.dp),
            onDismissRequest = { showNewDocDialog = false },
            title = {
                Text(
                    text = "نیا مسودہ بنائیں (New Document)",
                    fontFamily = nastaleeqFont,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = docTitle,
                    onValueChange = { docTitle = it },
                    label = { Text("مسودے کا عنوان", fontFamily = nastaleeqFont) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showNewDocDialog = false
                        viewModel.createNewBlankDocument(docTitle) { newId ->
                            onOpenDocument(newId)
                        }
                    }
                ) {
                    Text("مسودہ کھولیں", fontFamily = nastaleeqFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDocDialog = false }) {
                    Text("منسوخ", fontFamily = nastaleeqFont)
                }
            }
        )
    }

    // Rename Document Dialog
    if (renameDocId != null) {
        AlertDialog(
            shape = RoundedCornerShape(24.dp),
            onDismissRequest = { renameDocId = null },
            title = {
                Text(
                    text = "مسودے کا نام تبدیل کریں",
                    fontFamily = nastaleeqFont,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = renameTitleText,
                    onValueChange = { renameTitleText = it },
                    label = { Text("نیا عنوان", fontFamily = nastaleeqFont) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        val id = renameDocId
                        if (id != null) {
                            viewModel.renameDocument(id, renameTitleText)
                        }
                        renameDocId = null
                    }
                ) {
                    Text("تبدیل کریں", fontFamily = nastaleeqFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { renameDocId = null }) {
                    Text("منسوخ", fontFamily = nastaleeqFont)
                }
            }
        )
    }
}

@Composable
fun DocumentCard(
    document: DocumentEntity,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onExportPdf: () -> Unit,
    onExportDocx: () -> Unit,
    onPrint: () -> Unit
) {
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)
    var showMenu by remember { mutableStateOf(false) }

    val formattedDate = remember(document.updatedAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(document.updatedAt))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = document.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = nastaleeqFont,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$formattedDate • ${document.pageCount} صفحات",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (document.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (document.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                        }

                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("مسودہ کھولیں (Open)", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = { showMenu = false; onClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("پی ڈی ایف ایکسپورٹ و شیئر", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) },
                                onClick = { showMenu = false; onExportPdf() }
                            )
                            DropdownMenuItem(
                                text = { Text("پرنٹ کریں (Print A4)", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Print, contentDescription = null) },
                                onClick = { showMenu = false; onPrint() }
                            )
                            DropdownMenuItem(
                                text = { Text("ورڈ (DOCX) ایکسپورٹ", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                                onClick = { showMenu = false; onExportDocx() }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("نام تبدیل کریں (Rename)", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) },
                                onClick = { showMenu = false; onRename() }
                            )
                            DropdownMenuItem(
                                text = { Text("کاپی بنائیں (Duplicate)", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = { showMenu = false; onDuplicate() }
                            )
                            DropdownMenuItem(
                                text = { Text("حذف کریں (Delete)", fontFamily = nastaleeqFont, color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = { showMenu = false; onDelete() }
                            )
                        }
                    }
                }
            }

            if (document.previewSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = document.previewSnippet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = nastaleeqFont,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
