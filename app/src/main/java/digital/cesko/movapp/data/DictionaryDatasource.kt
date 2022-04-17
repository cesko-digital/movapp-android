package digital.cesko.movapp.data

import android.content.Context
import digital.cesko.movapp.ui.dictionary.DictionarySectionsData
import digital.cesko.movapp.ui.dictionary.DictionaryTranslationsData
import org.json.JSONObject
import java.io.IOException

class DictionaryDatasource {

    fun loadSections(context: Context): List<DictionarySectionsData> {
        var jsonString: String = ""
        var dict = mutableListOf<DictionarySectionsData>()

        try {
            jsonString = context.assets.open("dictionary/cs-uk-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONObject(jsonString).getJSONArray("sections")

        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)

            var translations = mutableListOf<String>()

            val jsonTranslatesArr = jsonObj.getJSONArray("translations")

            for (j in 0 until jsonTranslatesArr.length()) {
                translations.add(jsonTranslatesArr.getString(j))
            }

            dict.add(DictionarySectionsData(
                jsonObj.getString("id"),
                jsonObj.getString("name_from"),
                jsonObj.getString("name_to"),
                translations)
            )
         }

        return dict
    }

    fun loadTranslations(context: Context): List<DictionaryTranslationsData> {
        var jsonString: String = ""
        var translations = mutableListOf<DictionaryTranslationsData>()

        try {
            jsonString = context.assets.open("dictionary/cs-uk-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonObj = JSONObject(jsonString).getJSONObject("translations")

        for (i in jsonObj.keys()) {
            val jsonObjItem = jsonObj.getJSONObject(i)

            translations.add(
                DictionaryTranslationsData(
                    jsonObjItem.getString("id"),
                    jsonObjItem.getString("translation_from"),
                    jsonObjItem.getString("transcription_from"),
                    jsonObjItem.getString("translation_to"),
                    jsonObjItem.getString("transcription_to")
                )
            )
        }

        return translations
    }
}