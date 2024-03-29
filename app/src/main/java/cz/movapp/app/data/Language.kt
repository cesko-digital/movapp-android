package cz.movapp.app.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cz.movapp.app.R
import java.util.Locale

/**
 * @param isReversed whether translation is reverse to data of json see cs-uk-dictionary.json
 */
enum class LanguagePair(
    val from: Language,
    val to: Language,
    val isReversed: Boolean
) {


    CsToUk(Language.Czech, Language.Ukrainian, false),
    UkToCs(Language.Ukrainian, Language.Czech, true),

    SkToUk(Language.Slovak, Language.Ukrainian, false),
    UkToSk(Language.Ukrainian, Language.Slovak, true),

    PlToUk(Language.Polish, Language.Ukrainian, false),
    UkToPl(Language.Ukrainian, Language.Polish, true);

    companion object {
        fun getDefault(): LanguagePair {
            val lang = LanguagePair.values()
                .firstOrNull { it.from.langCode == Locale.getDefault().language }
            return lang ?: UkToCs
        }

        fun nextLanguage(languagePair: LanguagePair): LanguagePair {
            val values = LanguagePair.values()
            val index = values.indexOf(languagePair)
            return values[(index + 1) % values.size]
        }
    }
}

enum class Language(
    val langCode: String,
    @DrawableRes val flagResId: Int,
    @StringRes val stringId: Int,
    @StringRes val accusativeStringId: Int
) {
    Ukrainian("uk", R.drawable.ua, R.string.ukrainian, R.string.ukrainian_accusative),
    Czech("cs", R.drawable.cz, R.string.czech, R.string.czech_accusative),
    Slovak("sk", R.drawable.sk, R.string.slovak, R.string.slovak_accusative),
    Polish("pl", R.drawable.pl, R.string.polish, R.string.polish_accusative)
}