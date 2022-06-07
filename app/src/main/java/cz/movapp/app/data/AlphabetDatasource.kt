package cz.movapp.app.data

import android.content.Context
import cz.movapp.app.ui.alphabet.AlphabetData
import cz.movapp.app.ui.alphabet.LetterExampleData
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


/**
 * see app/src/main/assets/alphabet/cs-alphabet.json
 */
class AlphabetDatasource(private val context: Context) {

    private val cache = mutableMapOf<String,List<AlphabetData>>()

    fun loadByLanguage(sourceLangCode: String, mainLangCode: String): List<AlphabetData> {
        var jsonString: String = ""
        var alphabet = mutableListOf<AlphabetData>()

        val fileName = "$sourceLangCode-$mainLangCode-alphabet.json"

        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONObject(jsonString).getJSONArray("data")

        for (i in 0 until jsonArr.length()) {
            var soundUrl: String? = null

            val jsonLetterObj = jsonArr.getJSONObject(i)

            var examples = mutableListOf<LetterExampleData>()

            val jsonExamplesArr = jsonLetterObj.getJSONArray("examples")

            for (j in 0 until jsonExamplesArr.length()) {
                val jsonExampleObj = jsonExamplesArr.getJSONObject(j)

                soundUrl = jsonExampleObj.getNullString("sound_url")

                if (soundUrl != null) {
                    soundUrl = soundUrl!!.replace("https://data.movapp.eu/", "")
                    soundUrl = if (context.assets.open(soundUrl).readBytes().isEmpty()) null else soundUrl
                }

                examples.add(LetterExampleData(
                    jsonExampleObj.getString("translation"),
                    jsonExampleObj.getString("transcription"),
                    soundUrl
                ))
            }

            soundUrl = jsonLetterObj.getNullString("sound_url")
            if (soundUrl != null) {
                soundUrl = soundUrl!!.replace("https://data.movapp.eu/", "")
            }

            alphabet.add(
                AlphabetData(
                    id = jsonLetterObj.getString("id"),
                    language = sourceLangCode,
                    letter_capital = jsonLetterObj.getJSONArray("letters").getNullString(0),
                    letter = if (jsonLetterObj.getJSONArray("letters").length() < 2) null else jsonLetterObj.getJSONArray("letters").getNullString(1),
                    file_name = fileName,
                    letterSoundAssetFile = soundUrl,
                    transcription = jsonLetterObj.getString("transcription"),
                    examples = examples
                )
            )
        }

        return alphabet
    }

    fun load(sourceLang: Language, mainLang: Language): List<AlphabetData> {
        return lazyCacheLoad(sourceLang, mainLang)
    }

    private fun lazyCacheLoad(sourceLang: Language, mainLang: Language): List<AlphabetData> {
        var selected = cache[sourceLang.langCode]
        return if (selected == null) {
            selected = loadByLanguage(sourceLang.langCode, mainLang.langCode)
            cache[sourceLang.langCode] = selected
            selected
        } else {
            selected
        }
    }

}

fun JSONArray.getNullString(index: Int): String? {
    val string = this.getString(index)
    return if (string == "null") null else string
}

fun JSONObject.getNullString(name: String): String? {
    val string = this.getString(name)
    return if (string == "null") null else string
}
