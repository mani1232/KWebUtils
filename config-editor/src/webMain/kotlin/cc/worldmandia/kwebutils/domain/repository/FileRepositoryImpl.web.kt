package cc.worldmandia.kwebutils.domain.repository

import cc.worldmandia.kwebutils.domain.model.ProjectFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download

actual suspend fun IFileRepository.saveAsFile(file: ProjectFile) {
    FileKit.download(
        bytes = file.content.encodeToByteArray(),
        fileName = "${file.name}.${file.extension}"
    )
}