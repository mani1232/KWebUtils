package cc.worldmandia.kwebutils.domain.repository

import cc.worldmandia.kwebutils.domain.model.ProjectFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.write

actual suspend fun IFileRepository.saveAsFile(file: ProjectFile) {
    val newFile = FileKit.openFileSaver(
        suggestedName = file.name,
        extension = file.extension
    )

    newFile?.write(file.content.encodeToByteArray())
}