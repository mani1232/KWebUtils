package cc.worldmandia.kwebconverter.model

import cc.worldmandia.kwebconverter.ParserType
import cc.worldmandia.kwebconverter.viewmodel.FilesViewModel
import io.github.vinceglb.filekit.*
import kotlinx.serialization.Serializable

@Serializable
data class FileItemModel(
    val fileData: FileDataModel,
    val cachedOriginalContent: String,
    var cachedEditedContent: String? = cachedOriginalContent,
    val parserType: ParserType
) {
    constructor(originalFile: PlatformFile, parserType: ParserType, cachedOriginalContent: String) : this(
        fileData = FileDataModel(originalFile.nameWithoutExtension, originalFile.extension),
        parserType = parserType,
        cachedOriginalContent = cachedOriginalContent
    )

    fun resetCache() {
        cachedEditedContent = cachedOriginalContent
        FilesViewModel.clearDraft(fileData.nameWithExtension)
    }

    suspend fun saveToUser() {
        FileKit.download(
            (cachedEditedContent
                ?: cachedOriginalContent).encodeToByteArray(),
            fileData.nameWithExtension
        )
    }

}

@Serializable
data class FileDataModel(
    var fileName: String,
    val extension: String,
    val nameWithExtension: String = "$fileName.$extension",
)