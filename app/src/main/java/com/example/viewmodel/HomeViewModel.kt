package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DocumentEntity
import com.example.data.DocumentRepository
import com.example.model.DocumentCategory
import com.example.model.DocumentModel
import com.example.model.LegalTemplate
import com.example.model.LegalTemplates
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    val searchQuery: String
    val selectedCategory: DocumentCategory
    val documents: List<DocumentEntity>

    data class Success(
        override val searchQuery: String = "",
        override val selectedCategory: DocumentCategory = DocumentCategory.ALL,
        override val documents: List<DocumentEntity> = emptyList()
    ) : HomeUiState
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DocumentRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(DocumentCategory.ALL)
    val selectedCategory: StateFlow<DocumentCategory> = _selectedCategory.asStateFlow()

    val uiState: StateFlow<HomeUiState>

    init {
        val db = AppDatabase.getInstance(application)
        repository = DocumentRepository(db.documentDao())

        val allDocsFlow = repository.allDocuments

        uiState = combine(
            allDocsFlow,
            _searchQuery,
            _selectedCategory
        ) { docs, query, category ->
            val filtered = docs.filter { doc ->
                val matchesQuery = if (query.isBlank()) true else {
                    doc.title.contains(query, ignoreCase = true) || doc.previewSnippet.contains(query, ignoreCase = true)
                }
                val matchesCategory = if (category == DocumentCategory.ALL) true else {
                    doc.category == category.name
                }
                matchesQuery && matchesCategory
            }
            HomeUiState.Success(
                searchQuery = query,
                selectedCategory = category,
                documents = filtered
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Success()
        )
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: DocumentCategory) {
        _selectedCategory.value = category
    }

    fun createNewBlankDocument(title: String = "نیا قانونی مسودہ", onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val blankDoc = DocumentModel(
                title = title.ifBlank { "نیا قانونی مسودہ" }
            )
            val newId = repository.saveDocument(blankDoc)
            onCreated(newId)
        }
    }

    fun createFromTemplate(template: LegalTemplate, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val doc = template.documentModel.copy(
                id = 0,
                title = template.titleUrdu,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.saveDocument(doc)
            onCreated(newId)
        }
    }

    fun duplicateDocument(id: Long) {
        viewModelScope.launch {
            repository.duplicateDocument(id)
        }
    }

    fun renameDocument(id: Long, newTitle: String) {
        viewModelScope.launch {
            repository.renameDocument(id, newTitle)
        }
    }

    fun toggleFavorite(id: Long, isFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isFav)
        }
    }

    fun deleteDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }
}
