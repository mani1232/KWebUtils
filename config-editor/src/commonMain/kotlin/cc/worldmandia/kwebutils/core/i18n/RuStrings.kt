package cc.worldmandia.kwebutils.core.i18n

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "ru", default = false) // TODO Support com.vanniktech.locale.Language.RUSSIAN
val RuStrings = Strings(
    simple = "Привет, Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) {
            append("Привет, ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
            append("Compose!")
        }
    },

    parameter = { locale ->
        "Текущая локаль: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "нет"
            1, 2 -> "несколько"
            in 3..10 -> "куча"
            else -> "много"
        }
        "У меня $value яблок"
    },

    list = listOf("Авокадо", "Ананас", "Слива")
)