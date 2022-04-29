package cz.movapp.app

import androidx.annotation.DrawableRes
import java.util.*

/**
 * @param isReversed whether translation is reverse to data of json see cs-uk-dictionary.json
 */
enum class LanguagePair(
    val from: Language,
    val to: Language,
    val isReversed: Boolean
    ) {


    CsToUk(Language.Czech, Language.Ukrainian, false),
    UkToCs(Language.Ukrainian, Language.Czech, true);

    companion object {
        fun getDefault(): LanguagePair {
            val lang = LanguagePair.values()
                .firstOrNull { it.from.langCode == Locale.getDefault().language }
            return lang ?: LanguagePair.UkToCs
        }

        fun nextLanguage(languagePair: LanguagePair): LanguagePair {
            val values = LanguagePair.values()
            val index = values.indexOf(languagePair)
            return values[(index +1) % values.size]
        }
    }
}

enum class Language(
    val langCode: String,
    @DrawableRes val flagResId: Int,
) {
    Ukrainian("uk", R.drawable.ua),
    Czech("cs", R.drawable.cz)
}