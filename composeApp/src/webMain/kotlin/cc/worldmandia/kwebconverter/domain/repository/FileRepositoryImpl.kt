package cc.worldmandia.kwebconverter.domain.repository

import cc.worldmandia.kwebconverter.domain.model.ProjectFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import web.storage.localStorage

class FileRepositoryImpl : IFileRepository {

    override suspend fun saveFile(file: ProjectFile) {
        FileKit.download(
            bytes = file.content.encodeToByteArray(),
            fileName = "${file.name}.${file.extension}"
        )
    }

    override suspend fun saveDraft(fileId: String, content: String) {
        localStorage.setItem("draft_$fileId", content)
    }

    override suspend fun getDraft(fileId: String): String? {
        return localStorage.getItem("draft_$fileId")
    }

    override suspend fun clearDraft(fileId: String) {
        localStorage.removeItem("draft_$fileId")
    }
}