package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.LegalTemplates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DocumentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "draftings_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with starter templates
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val dao = getInstance(context).documentDao()
                                LegalTemplates.allTemplates.take(4).forEach { template ->
                                    val doc = template.documentModel
                                    val contentJson = DocumentSerializer.toJson(doc)
                                    val snippet = doc.pages.firstOrNull()?.paragraphs?.firstOrNull { it.text.isNotBlank() }?.text ?: ""
                                    val entity = DocumentEntity(
                                        title = template.titleUrdu,
                                        category = template.category.name,
                                        contentJson = contentJson,
                                        pageCount = doc.pages.size,
                                        previewSnippet = snippet.take(120),
                                        isFavorite = template.id == "template_affidavit",
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    dao.insertDocument(entity)
                                }
                            } catch (e: Exception) {
                                // Prevent unhandled coroutine exception crash
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
