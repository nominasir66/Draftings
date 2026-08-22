package com.example.data

import com.example.model.DocumentModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepository(private val dao: DocumentDao) {

    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()

    fun searchDocuments(query: String): Flow<List<DocumentEntity>> = dao.searchDocuments(query)

    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>> = dao.getDocumentsByCategory(category)

    suspend fun getDocumentById(id: Long): DocumentModel? {
        val entity = dao.getDocumentById(id) ?: return null
        val doc = DocumentSerializer.fromJson(entity.contentJson)
        return doc.copy(id = entity.id, title = entity.title)
    }

    suspend fun saveDocument(doc: DocumentModel): Long {
        val now = System.currentTimeMillis()
        val contentJson = DocumentSerializer.toJson(doc)
        val snippet = doc.pages.firstOrNull()?.paragraphs?.firstOrNull { it.text.isNotBlank() }?.text ?: ""
        val entity = DocumentEntity(
            id = doc.id,
            title = doc.title.ifBlank { "بلا عنوان مسودہ" },
            category = doc.category.name,
            contentJson = contentJson,
            pageCount = doc.pages.size,
            previewSnippet = snippet.take(120),
            createdAt = if (doc.id == 0L) now else doc.createdAt,
            updatedAt = now
        )
        return dao.insertDocument(entity)
    }

    suspend fun duplicateDocument(id: Long): Long {
        val original = dao.getDocumentById(id) ?: return -1
        val copyTitle = "${original.title} (کاپی)"
        val newEntity = original.copy(
            id = 0,
            title = copyTitle,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return dao.insertDocument(newEntity)
    }

    suspend fun renameDocument(id: Long, newTitle: String) {
        dao.updateTitle(id, newTitle, System.currentTimeMillis())
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    suspend fun deleteDocument(id: Long) {
        dao.deleteDocumentById(id)
    }
}
