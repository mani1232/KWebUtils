package cc.worldmandia.kwebutils.core.i18n

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "fr", default = false) // TODO Support com.vanniktech.locale.Language.FRENCH
val FrStrings = Strings(
    simple = "Bonjour Compose !",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) {
            append("Bonjour ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
            append("Compose !")
        }
    },

    parameter = { locale ->
        "Locale actuelle : $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "zéro"        // Fits: "J'ai zéro pommes"
            1, 2 -> "quelques" // Fits: "J'ai quelques pommes"
            in 3..10 -> "un tas de" // Fits: "J'ai un tas de pommes"
            else -> "beaucoup de"   // Fits: "J'ai beaucoup de pommes"
        }
        "J'ai $value pommes"
    },

    list = listOf("Avocat", "Ananas", "Prune")
)