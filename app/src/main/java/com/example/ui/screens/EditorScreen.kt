package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
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
import com.example.model.TextBoxModel
import com.example.typography.FontHelper
import com.example.ui.components.A4PageView
import com.example.ui.components.FormattingToolbar
import com.example.ui.components.LegalClauseSheet
import com.example.ui.components.TextBoxEditDialog
import com.example.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    documentId: Long,
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    val listState = rememberLazyListState()

    var showExportMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showLegalClausesSheet by remember { mutableStateOf(false) }
    var editingTextBox by remember { mutableStateOf<TextBoxModel?>(null) }
    var renameText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    BackHandler {
        viewModel.saveDocument {
            onNavigateBack()
        }
    }

    val activeParagraph = remember(uiState.document, uiState.activePageIndex, uiState.activeParagraphIndex) {
        val page = uiState.document.pages.getOrNull(uiState.activePageIndex)
        page?.paragraphs?.getOrNull(uiState.activeParagraphIndex)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable {
                            renameText = uiState.document.title
                            showRenameDialog = true
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.document.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = nastaleeqFont,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Title",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Save Status Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(10.dp),
                                    strokeWidth = 1.5.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "محفوظ ہو رہا ہے…",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = nastaleeqFont, fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (uiState.hasUnsavedChanges) {
                                Text(
                                    text = "غیر محفوظ تبدیلیاں (Unsaved)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = nastaleeqFont, fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "محفوظ شدہ (Saved)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = nastaleeqFont, fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveDocument {
                            onNavigateBack()
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Manual Save button
                    IconButton(onClick = { viewModel.saveDocument() }) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                    }

                    // Export Menu
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Export Menu")
                        }

                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("پی ڈی ایف تیار و پرنٹ کریں", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.generatePdf(context) { }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("پی ڈی ایف شیئر کریں", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.exportAndSharePdf(context)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("پرنٹ کریں (Print A4)", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Print, contentDescription = null) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.printDocument(context)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("ورڈ (DOCX) ایکسپورٹ", fontFamily = nastaleeqFont) },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.exportAndShareDocx(context)
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (uiState.document.showMarginGuidelines) "حاشیے کی لکیریں چھپائیں" else "حاشیے کی لکیریں دکھائیں",
                                        fontFamily = nastaleeqFont
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.BorderColor, contentDescription = null) },
                                onClick = {
                                    showExportMenu = false
                                    viewModel.toggleMarginGuidelines()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            FormattingToolbar(
                activeParagraph = activeParagraph,
                currentFontFamily = uiState.document.defaultFontFamily,
                canUndo = uiState.canUndo,
                canRedo = uiState.canRedo,
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onToggleBold = { viewModel.toggleBold() },
                onToggleUnderline = { viewModel.toggleUnderline() },
                onToggleItalic = { viewModel.toggleItalic() },
                onFontSizeChanged = { viewModel.setFontSize(it) },
                onAlignmentChanged = { viewModel.setAlignment(it) },
                onDirectionChanged = { viewModel.setDirection(it) },
                onLineSpacingChanged = { viewModel.setLineSpacing(it) },
                onFontFamilyChanged = { viewModel.setDocumentFontFamily(it) },
                onHeadingChanged = { viewModel.setHeading(it) },
                onInsertParagraph = { viewModel.insertParagraphAfterActive() },
                onDeleteParagraph = { viewModel.deleteActiveParagraph() },
                onAddTextBox = { viewModel.addTextBox(uiState.activePageIndex) },
                onOpenLegalClauses = { showLegalClausesSheet = true },
                onAddNewPage = { viewModel.addNewPage() },
                modifier = Modifier.imePadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
            ) {
                itemsIndexed(uiState.document.pages) { pageIndex, page ->
                    A4PageView(
                        document = uiState.document,
                        pageIndex = pageIndex,
                        activeParagraphIndex = if (pageIndex == uiState.activePageIndex) uiState.activeParagraphIndex else -1,
                        selectedTextBox = uiState.selectedTextBox,
                        onParagraphFocused = { paraIdx ->
                            viewModel.selectParagraph(pageIndex, paraIdx)
                        },
                        onParagraphTextChanged = { paraIdx, newText ->
                            viewModel.updateParagraphText(pageIndex, paraIdx, newText)
                        },
                        onTextBoxSelected = { box ->
                            viewModel.selectTextBox(box)
                        },
                        onTextBoxUpdated = { box ->
                            viewModel.updateTextBox(box)
                        },
                        onTextBoxEditRequest = { box ->
                            editingTextBox = box
                        },
                        onTextBoxDeleted = { boxId ->
                            viewModel.deleteTextBox(boxId)
                        },
                        onDeletePage = {
                            viewModel.deletePage(pageIndex)
                        }
                    )
                }

                // Add Page Button at end of document
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.addNewPage() },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نیا صفحہ شامل کریں (+ Add Page)", fontFamily = nastaleeqFont, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = {
                Text(
                    text = "دستاویز کا عنوان تبدیل کریں",
                    fontFamily = nastaleeqFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("عنوان", fontFamily = nastaleeqFont) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            viewModel.updateTitle(renameText)
                        }
                        showRenameDialog = false
                    }
                ) {
                    Text("محفوظ کریں", fontFamily = nastaleeqFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("منسوخ", fontFamily = nastaleeqFont)
                }
            }
        )
    }

    // Legal Clauses Bottom Sheet
    if (showLegalClausesSheet) {
        LegalClauseSheet(
            onDismiss = { showLegalClausesSheet = false },
            onClauseSelected = { clause ->
                viewModel.insertLegalClause(clause)
            }
        )
    }

    // Text Box Edit Dialog
    editingTextBox?.let { box ->
        TextBoxEditDialog(
            textBox = box,
            onDismiss = { editingTextBox = null },
            onSave = { updated ->
                viewModel.updateTextBox(updated)
                editingTextBox = null
            },
            onDelete = { boxId ->
                viewModel.deleteTextBox(boxId)
                editingTextBox = null
            }
        )
    }

    // PDF Preview & Share Dialog
    if (uiState.showPdfPreview) {
        PdfPreviewDialog(
            document = uiState.document,
            pdfFile = uiState.exportedPdfFile,
            onDismiss = { viewModel.dismissPdfPreview() },
            onExportDocx = {
                viewModel.dismissPdfPreview()
                viewModel.exportAndShareDocx(context)
            }
        )
    }
}
