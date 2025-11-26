package cc.worldmandia.kwebutils.presentation.feature.editor.mapper

import cc.worldmandia.kwebutils.domain.model.FileFormat
import cc.worldmandia.kwebutils.presentation.model.*
import com.charleskorn.kaml.*
import kotlinx.serialization.json.*
import li.songe.json5.Json5
import li.songe.json5.Json5EncoderConfig

object NodeSerializer {

    val yamlConfig = Yaml(
        configuration = YamlConfiguration(
            anchorsAndAliases = AnchorsAndAliases.Permitted(),
            singleLineStringStyle = SingleLineStringStyle.Plain,
        )
    )
    val jsonConfig = Json { prettyPrint = true }
    val json5Config = Json5EncoderConfig()

    fun serialize(node: EditableNode, format: FileFormat): String {
        return when (format) {
            FileFormat.YAML -> yamlConfig.encodeToString(YamlNode.serializer(), node.toYamlNode())
            FileFormat.JSON5 -> Json5.encodeToString(node.toJsonElement(), json5Config)
            FileFormat.JSON -> jsonConfig.encodeToString(JsonElement.serializer(), node.toJsonElement())
            else -> ""
        }
    }

    // Для буфера обмена (всегда JSON5/JSON для удобства или зависит от выбора)
    fun serializeForClipboard(node: EditableNode): String {
        return Json5.encodeToString(node.toJsonElement(), json5Config)
    }
}

// Converters back to Library Types
private fun EditableNode.toYamlNode(): YamlNode {
    return when (this) {
        is EditableScalar -> YamlScalar(state.text.toString(), YamlPath.root)
        is EditableList -> YamlList(items.map { it.toYamlNode() }, YamlPath.root)
        is EditableMap -> YamlMap(entries.associate {
            YamlScalar(it.key, YamlPath.root) to it.value.toYamlNode()
        }, YamlPath.root)

        is EditableNull -> YamlNull(YamlPath.root)
    }
}

private fun EditableNode.toJsonElement(): JsonElement {
    return when (this) {
        is EditableScalar -> {
            val text = state.text.toString()
            when (explicitType) {
                ScalarType.Boolean -> JsonPrimitive(text.toBooleanStrictOrNull() ?: false)
                ScalarType.Number -> JsonPrimitive(text.toDoubleOrNull() ?: text.toLongOrNull() ?: 0)
                ScalarType.String -> JsonPrimitive(text)
            }
        }

        is EditableList -> JsonArray(items.map { it.toJsonElement() })
        is EditableMap -> JsonObject(entries.associate { it.key to it.value.toJsonElement() })
        is EditableNull -> JsonNull
    }
}