package cz.movapp.app.data

import android.content.Context
import cz.movapp.app.ui.dictionary.DictionarySectionsData
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import org.json.JSONObject
import java.io.IOException
import java.text.Normalizer
import java.util.*

class DictionaryDatasource {

    fun loadSections(context: Context): List<DictionarySectionsData> {
        var jsonString: String = ""
        var dict = mutableListOf<DictionarySectionsData>()

        try {
            jsonString = context.assets.open("uk-cs-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONObject(jsonString).getJSONArray("categories")

        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)

            var phrases = mutableListOf<String>()

            val jsonPhrasesArr = jsonObj.getJSONArray("phrases")

            for (j in 0 until jsonPhrasesArr.length()) {
                phrases.add(jsonPhrasesArr.getString(j))
            }

            val jsonNameObj = jsonObj.getJSONObject("name")

            dict.add(DictionarySectionsData(
                jsonObj.getString("id"),
                jsonNameObj.getString("main"),
                jsonNameObj.getString("source"),
                phrases)
            )
         }

        return dict
    }

    fun loadTranslations(context: Context): List<DictionaryTranslationsData> {
        var jsonString: String = ""
        var translations = mutableListOf<DictionaryTranslationsData>()

        try {
            jsonString = context.assets.open("uk-cs-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonObj = JSONObject(jsonString).getJSONObject("phrases")

        for (i in jsonObj.keys()) {
            val jsonItemObj = jsonObj.getJSONObject(i)
            val jsonObjMainItem = jsonItemObj.getJSONObject("main")
            val jsonObjSourceItem = jsonItemObj.getJSONObject("source")

            translations.add(
                DictionaryTranslationsData(
                    jsonItemObj.getString("id"),

                    jsonObjMainItem.getString("translation"),
                    jsonObjMainItem.getString("transcription"),
                    stripAccents(jsonObjMainItem.getString("translation").toString().lowercase(Locale.getDefault())),
                    jsonObjMainItem.getString("sound_url"),
                    jsonObjMainItem.getString("sound_url").replace("https://data.movapp.eu/", ""),

                    jsonObjSourceItem.getString("translation"),
                    jsonObjSourceItem.getString("transcription"),
                    jsonObjSourceItem.getString("translation").lowercase(Locale.getDefault()),
                    jsonObjSourceItem.getString("sound_url"),
                    jsonObjSourceItem.getString("sound_url").replace("https://data.movapp.eu/", "")
                )
            )
        }

        return translations
    }

    private fun stripAccents(input: String): String {
        var output = Normalizer.normalize(input, Normalizer.Form.NFD)
        return output.replace(Regex("[^\\p{ASCII}]"), "")
    }
}