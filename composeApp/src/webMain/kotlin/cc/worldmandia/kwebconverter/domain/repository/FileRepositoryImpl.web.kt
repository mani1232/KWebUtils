package cc.worldmandia.kwebconverter.domain.repository

import cc.worldmandia.kwebconverter.domain.model.ProjectFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import web.storage.localStorage

actual class FileRepositoryImpl : IFileRepository {
    actual override suspend fun saveFile(file: ProjectFile) {
        FileKit.download(
            bytes = file.content.encodeToByteArray(),
            fileName = "${file.name}.${file.extension}"
        )
    }

    actual override suspend fun saveDraft(fileId: String, content: String) {
        localStorage.setItem("draft_$fileId", content)
    }

    actual override suspend fun getDraft(fileId: String): String? {
        return localStorage.getItem("draft_$fileId")
    }

    actual override suspend fun clearDraft(fileId: String) {
        localStorage.removeItem("draft_$fileId")
    }
}