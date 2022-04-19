package cz.movapp.app.data

import android.content.Context
import cz.movapp.app.ui.alphabet.AlphabetData
import cz.movapp.app.ui.alphabet.LetterExampleData
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


fun toLanguageCode(fromUa: Boolean?) = if (fromUa == true) "uk" else "cs"

/**
 * see app/src/main/assets/alphabet/cs-alphabet.json
 */
class AlphabetDatasource(private val context: Context) {

    private val cache = mutableMapOf(
        true to listOf<AlphabetData>(),
        false to listOf<AlphabetData>()
    )

    fun loadByLanguage(langCode: String): List<AlphabetData> {
        var jsonString: String = ""
        var alphabet = mutableListOf<AlphabetData>()

        val fileName = "alphabet/%s-alphabet.json".format(langCode)

        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONObject(jsonString).getJSONArray("data")

        for (i in 0 until jsonArr.length()) {
            val jsonLetterObj = jsonArr.getJSONObject(i)

            var examples = mutableListOf<LetterExampleData>()

            val jsonExamplesArr = jsonLetterObj.getJSONArray("examples")

            for (j in 0 until jsonExamplesArr.length()) {
                val jsonExampleObj = jsonExamplesArr.getJSONObject(j)

                examples.add(LetterExampleData(
                    jsonExampleObj.getString("example"),
                    jsonExampleObj.getString("example_transcription")
                ))
            }

            val letterLowerCase = jsonLetterObj.getJSONArray("letter").getNullString(1)
            alphabet.add(AlphabetData(
                    jsonLetterObj.getString("id"),
                    langCode,
                    jsonLetterObj.getJSONArray("letter").getNullString(0),
                    letterLowerCase,
                    letterSoundAssetFile = (if(letterLowerCase == null) null else "alphabet/$langCode-alphabet/$letterLowerCase.mp3"),
                    jsonLetterObj.getString("transcription"),
                    examples
            )
            )
        }

        return alphabet
    }

    fun load(fromUa: Boolean): List<AlphabetData> {
        return lazyCacheLoad(fromUa)
    }

    private fun lazyCacheLoad(fromUa: Boolean): List<AlphabetData> {
        var selected = cache[fromUa]!!
        return if (selected.isEmpty()) {
            selected = loadByLanguage(toLanguageCode(fromUa))
            cache[fromUa] = selected
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
