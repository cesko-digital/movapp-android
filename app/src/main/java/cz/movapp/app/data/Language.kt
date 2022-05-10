package cz.movapp.app.data

import androidx.annotation.DrawableRes
import cz.movapp.app.App
import cz.movapp.app.R
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
            return lang ?: UkToCs
        }

        fun nextLanguage(languagePair: LanguagePair): LanguagePair {
            val values = LanguagePair.values()
            val index = values.indexOf(languagePair)
            println(values[(index + 1) % values.size])
            return values[(index + 1) % values.size]
        }
    }
}

enum class Language(
    val langCode: String,
    @DrawableRes val flagResId: Int,
    val stringText: String?
) {

    Ukrainian("uk", R.drawable.ua, App.ctx.resources.getString(R.string.ukrainian)),
    Czech("cs", R.drawable.cz, App.ctx.resources.getString(R.string.czech))
}