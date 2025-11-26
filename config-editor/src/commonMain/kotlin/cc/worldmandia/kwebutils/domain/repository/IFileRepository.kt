package cc.worldmandia.kwebutils.domain.repository

interface IFileRepository {
    suspend fun saveDraft(fileId: String, content: String)
    suspend fun getDraft(fileId: String): String?
    suspend fun clearDraft(fileId: String)
}