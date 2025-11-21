package cc.worldmandia.kwebconverter.domain.repository

import cc.worldmandia.kwebconverter.domain.model.ProjectFile

expect class FileRepositoryImpl : IFileRepository {
    constructor()
    override suspend fun saveFile(file: ProjectFile)
    override suspend fun saveDraft(fileId: String, content: String)
    override suspend fun getDraft(fileId: String): String?
    override suspend fun clearDraft(fileId: String)

}