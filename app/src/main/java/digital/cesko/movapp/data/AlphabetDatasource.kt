package digital.cesko.movapp.data

import android.content.Context
import digital.cesko.movapp.ui.alphabet.AlphabetData
import digital.cesko.movapp.ui.alphabet.LetterExampleData
import org.json.JSONObject
import java.io.IOException

class AlphabetDatasource {
    fun loadLanguage(context: Context, language: String): List<AlphabetData> {
        var jsonString: String = ""
        var alphabet = mutableListOf<AlphabetData>()

        val fileName = "alphabet/%s-alphabet.json".format(language)

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

            alphabet.add(AlphabetData(
                jsonLetterObj.getString("id"),
                language,
                jsonLetterObj.getJSONArray("letter").get(0).toString(),
                jsonLetterObj.getJSONArray("letter").get(1).toString(),
                jsonLetterObj.getString("transcription"),
                examples)
            )
        }

        return alphabet
    }
}