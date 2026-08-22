package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DocumentRepository
import com.example.export.DocxExporter
import com.example.export.PdfExporter
import com.example.export.PrintAndShareHelper
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class EditorUiState(
    val document: DocumentModel = DocumentModel(),
    val activePageIndex: Int = 0,
    val activeParagraphIndex: Int = 0,
    val selectedTextBox: TextBoxModel? = null,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val exportedPdfFile: File? = null,
    val showPdfPreview: Boolean = false,
    val statusMessage: String? = null
)

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocumentRepository = DocumentRepository(AppDatabase.getInstance(application).documentDao())

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = ArrayDeque<DocumentModel>()
    private val redoStack = ArrayDeque<DocumentModel>()
    private var autoSaveJob: Job? = null

    fun loadDocument(documentId: Long) {
        viewModelScope.launch {
            if (documentId > 0) {
                val doc = repository.getDocumentById(documentId)
                if (doc != null) {
                    undoStack.clear()
                    redoStack.clear()
                    _uiState.value = _uiState.value.copy(
                        document = doc,
                        activePageIndex = 0,
                        activeParagraphIndex = 0,
                        selectedTextBox = null,
                        hasUnsavedChanges = false,
                        canUndo = false,
                        canRedo = false
                    )
                }
            }
        }
    }

    private fun pushHistory(newDoc: DocumentModel) {
        undoStack.addLast(_uiState.value.document)
        if (undoStack.size > 40) {
            undoStack.removeFirst()
        }
        redoStack.clear()

        _uiState.value = _uiState.value.copy(
            document = newDoc,
            hasUnsavedChanges = true,
            canUndo = undoStack.isNotEmpty(),
            canRedo = false
        )

        scheduleAutoSave()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeLast()
            redoStack.addLast(_uiState.value.document)
            _uiState.value = _uiState.value.copy(
                document = previous,
                hasUnsavedChanges = true,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true
            )
            scheduleAutoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeLast()
            undoStack.addLast(_uiState.value.document)
            _uiState.value = _uiState.value.copy(
                document = next,
                hasUnsavedChanges = true,
                canUndo = true,
                canRedo = redoStack.isNotEmpty()
            )
            scheduleAutoSave()
        }
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(1800)
            saveDocument()
        }
    }

    fun saveDocument(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val currentDoc = _uiState.value.document
            val savedId = repository.saveDocument(currentDoc)
            val updatedDoc = currentDoc.copy(id = savedId, updatedAt = System.currentTimeMillis())
            _uiState.value = _uiState.value.copy(
                document = updatedDoc,
                isSaving = false,
                hasUnsavedChanges = false,
                statusMessage = "محفوظ کر لیا گیا (Saved)"
            )
            onComplete?.invoke()
        }
    }

    fun updateTitle(newTitle: String) {
        val updated = _uiState.value.document.copy(title = newTitle)
        pushHistory(updated)
    }

    fun selectParagraph(pageIndex: Int, paraIndex: Int) {
        _uiState.value = _uiState.value.copy(
            activePageIndex = pageIndex,
            activeParagraphIndex = paraIndex,
            selectedTextBox = null
        )
    }

    fun updateParagraphText(pageIndex: Int, paraIndex: Int, newText: String) {
        val doc = _uiState.value.document
        if (pageIndex !in doc.pages.indices) return
        val page = doc.pages[pageIndex]
        if (paraIndex !in page.paragraphs.indices) return

        val oldPara = page.paragraphs[paraIndex]
        if (oldPara.text == newText) return

        val updatedParagraphs = page.paragraphs.toMutableList().apply {
            this[paraIndex] = oldPara.copy(text = newText)
        }
        val updatedPages = doc.pages.toMutableList().apply {
            this[pageIndex] = page.copy(paragraphs = updatedParagraphs)
        }
        pushHistory(doc.copy(pages = updatedPages))
    }

    fun toggleBold() {
        mutateActiveParagraph { it.copy(isBold = !it.isBold) }
    }

    fun toggleUnderline() {
        mutateActiveParagraph { it.copy(isUnderline = !it.isUnderline) }
    }

    fun toggleItalic() {
        mutateActiveParagraph { it.copy(isItalic = !it.isItalic) }
    }

    fun setFontSize(sizeSp: Float) {
        mutateActiveParagraph { it.copy(fontSizeSp = sizeSp) }
    }

    fun setAlignment(alignment: TextAlignment) {
        mutateActiveParagraph { it.copy(alignment = alignment) }
    }

    fun setDirection(direction: TextDirection) {
        mutateActiveParagraph { it.copy(direction = direction) }
    }

    fun setLineSpacing(spacing: Float) {
        mutateActiveParagraph { it.copy(lineSpacing = spacing) }
    }

    fun setHeading(headingLevel: Int) {
        mutateActiveParagraph {
            it.copy(
                isHeading = headingLevel > 0,
                headingLevel = headingLevel,
                fontSizeSp = when (headingLevel) {
                    1 -> 24f
                    2 -> 20f
                    else -> 18f
                },
                isBold = headingLevel > 0,
                alignment = if (headingLevel == 1) TextAlignment.CENTER else it.alignment
            )
        }
    }

    fun insertParagraphAfterActive() {
        val doc = _uiState.value.document
        val pIdx = _uiState.value.activePageIndex
        val paraIdx = _uiState.value.activeParagraphIndex
        if (pIdx !in doc.pages.indices) return
        val page = doc.pages[pIdx]

        val newPara = ParagraphModel(
            fontSizeSp = doc.defaultFontSizeSp,
            alignment = doc.defaultAlignment,
            direction = doc.defaultDirection,
            lineSpacing = doc.defaultLineSpacing
        )

        val updatedParas = page.paragraphs.toMutableList().apply {
            val insertAt = (paraIdx + 1).coerceAtMost(size)
            add(insertAt, newPara)
        }

        val updatedPages = doc.pages.toMutableList().apply {
            this[pIdx] = page.copy(paragraphs = updatedParas)
        }

        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(activeParagraphIndex = paraIdx + 1)
    }

    fun deleteActiveParagraph() {
        val doc = _uiState.value.document
        val pIdx = _uiState.value.activePageIndex
        val paraIdx = _uiState.value.activeParagraphIndex
        if (pIdx !in doc.pages.indices) return
        val page = doc.pages[pIdx]

        if (page.paragraphs.size <= 1) {
            // Clear text instead of deleting only paragraph
            updateParagraphText(pIdx, 0, "")
            return
        }

        val updatedParas = page.paragraphs.toMutableList().apply {
            removeAt(paraIdx)
        }

        val updatedPages = doc.pages.toMutableList().apply {
            this[pIdx] = page.copy(paragraphs = updatedParas)
        }

        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(
            activeParagraphIndex = (paraIdx - 1).coerceAtLeast(0)
        )
    }

    fun insertLegalClause(clauseText: String) {
        val doc = _uiState.value.document
        val pIdx = _uiState.value.activePageIndex
        val paraIdx = _uiState.value.activeParagraphIndex
        if (pIdx !in doc.pages.indices) return
        val page = doc.pages[pIdx]
        if (paraIdx !in page.paragraphs.indices) return

        val current = page.paragraphs[paraIdx]
        val newText = if (current.text.isBlank()) clauseText else "${current.text} $clauseText"
        updateParagraphText(pIdx, paraIdx, newText)
    }

    fun addNewPage() {
        val doc = _uiState.value.document
        val newPageNumber = doc.pages.size + 1
        val newPage = PageModel(
            pageNumber = newPageNumber,
            paragraphs = listOf(
                ParagraphModel(
                    fontSizeSp = doc.defaultFontSizeSp,
                    alignment = doc.defaultAlignment,
                    direction = doc.defaultDirection
                )
            )
        )
        val updatedPages = doc.pages + newPage
        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(
            activePageIndex = updatedPages.lastIndex,
            activeParagraphIndex = 0
        )
    }

    fun deletePage(pageIndex: Int) {
        val doc = _uiState.value.document
        if (doc.pages.size <= 1) return
        if (pageIndex !in doc.pages.indices) return

        val updatedPages = doc.pages.toMutableList().apply {
            removeAt(pageIndex)
        }.mapIndexed { index, pageModel ->
            pageModel.copy(pageNumber = index + 1)
        }

        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(
            activePageIndex = (pageIndex - 1).coerceAtLeast(0),
            activeParagraphIndex = 0
        )
    }

    // Text Box Management
    fun addTextBox(pageIndex: Int) {
        val doc = _uiState.value.document
        if (pageIndex !in doc.pages.indices) return
        val page = doc.pages[pageIndex]

        val newBox = TextBoxModel(
            pageIndex = pageIndex,
            xPercent = 0.55f,
            yPercent = 0.05f,
            widthPercent = 0.38f,
            heightPercent = 0.10f,
            text = "خانہ نوٹ / مہر و تصدیق",
            fontSizeSp = 14f,
            alignment = TextAlignment.CENTER,
            showBorder = true
        )

        val updatedPages = doc.pages.toMutableList().apply {
            this[pageIndex] = page.copy(textBoxes = page.textBoxes + newBox)
        }

        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(selectedTextBox = newBox)
    }

    fun selectTextBox(box: TextBoxModel?) {
        _uiState.value = _uiState.value.copy(selectedTextBox = box)
    }

    fun updateTextBox(updatedBox: TextBoxModel) {
        val doc = _uiState.value.document
        val pageIdx = updatedBox.pageIndex
        if (pageIdx !in doc.pages.indices) return
        val page = doc.pages[pageIdx]

        val updatedBoxes = page.textBoxes.map {
            if (it.id == updatedBox.id) updatedBox else it
        }

        val updatedPages = doc.pages.toMutableList().apply {
            this[pageIdx] = page.copy(textBoxes = updatedBoxes)
        }

        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(selectedTextBox = updatedBox)
    }

    fun deleteTextBox(boxId: String) {
        val doc = _uiState.value.document
        val updatedPages = doc.pages.map { page ->
            page.copy(textBoxes = page.textBoxes.filterNot { it.id == boxId })
        }
        pushHistory(doc.copy(pages = updatedPages))
        _uiState.value = _uiState.value.copy(selectedTextBox = null)
    }

    fun setDocumentFontFamily(fontName: String) {
        val updated = _uiState.value.document.copy(defaultFontFamily = fontName)
        pushHistory(updated)
    }

    fun setMargins(margins: MarginsModel) {
        val updated = _uiState.value.document.copy(margins = margins)
        pushHistory(updated)
    }

    fun toggleMarginGuidelines() {
        val updated = _uiState.value.document.copy(
            showMarginGuidelines = !_uiState.value.document.showMarginGuidelines
        )
        pushHistory(updated)
    }

    private inline fun mutateActiveParagraph(crossinline transform: (ParagraphModel) -> ParagraphModel) {
        val doc = _uiState.value.document
        val pIdx = _uiState.value.activePageIndex
        val paraIdx = _uiState.value.activeParagraphIndex
        if (pIdx !in doc.pages.indices) return
        val page = doc.pages[pIdx]
        if (paraIdx !in page.paragraphs.indices) return

        val currentPara = page.paragraphs[paraIdx]
        val updatedPara = transform(currentPara)

        val updatedParas = page.paragraphs.toMutableList().apply {
            this[paraIdx] = updatedPara
        }
        val updatedPages = doc.pages.toMutableList().apply {
            this[pIdx] = page.copy(paragraphs = updatedParas)
        }
        pushHistory(doc.copy(pages = updatedPages))
    }

    // Export Actions
    fun generatePdf(context: Context, onReady: (File) -> Unit) {
        viewModelScope.launch {
            val file = PdfExporter.exportToPdf(context, _uiState.value.document)
            _uiState.value = _uiState.value.copy(
                exportedPdfFile = file,
                showPdfPreview = true
            )
            onReady(file)
        }
    }

    fun exportAndSharePdf(context: Context) {
        viewModelScope.launch {
            val file = PdfExporter.exportToPdf(context, _uiState.value.document)
            PrintAndShareHelper.sharePdf(context, file, _uiState.value.document.title)
        }
    }

    fun exportAndShareDocx(context: Context) {
        viewModelScope.launch {
            val file = DocxExporter.exportToDocx(context, _uiState.value.document)
            PrintAndShareHelper.shareDocx(context, file, _uiState.value.document.title)
        }
    }

    fun printDocument(context: Context) {
        viewModelScope.launch {
            val file = PdfExporter.exportToPdf(context, _uiState.value.document)
            PrintAndShareHelper.printPdf(context, file, _uiState.value.document.title)
        }
    }

    fun dismissPdfPreview() {
        _uiState.value = _uiState.value.copy(showPdfPreview = false)
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
