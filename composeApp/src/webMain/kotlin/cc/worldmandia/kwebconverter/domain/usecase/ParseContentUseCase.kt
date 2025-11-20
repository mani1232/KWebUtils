package cc.worldmandia.kwebconverter.domain.usecase

import cc.worldmandia.kwebconverter.domain.model.FileFormat
import cc.worldmandia.kwebconverter.presentation.feature.editor.mapper.NodeSerializer
import li.songe.json5.Json5

class ParseContentUseCase {
    operator fun invoke(content: String, format: FileFormat): Result<Any> {
        return runCatching {
            when (format) {
                FileFormat.YAML -> NodeSerializer.yamlConfig.parseToYamlNode(content)
                FileFormat.JSON5 -> Json5.parseToJson5Element(content)
                FileFormat.JSON -> NodeSerializer.jsonConfig.parseToJsonElement(content)
                else -> throw IllegalArgumentException("Unsupported format")
            }
        }
    }
}