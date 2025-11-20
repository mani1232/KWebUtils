package cc.worldmandia.kwebconverter.domain.repository

import cc.worldmandia.kwebconverter.domain.model.ProjectFile

interface IFileRepository {
    suspend fun saveFile(file: ProjectFile)
    suspend fun saveDraft(fileId: String, content: String)
    suspend fun getDraft(fileId: String): String?
    suspend fun clearDraft(fileId: String)
}