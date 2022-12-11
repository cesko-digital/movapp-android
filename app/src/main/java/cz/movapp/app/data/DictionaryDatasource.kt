package cz.movapp.app.data

import android.content.Context
import cz.movapp.android.createLangAssetsString
import cz.movapp.android.stripDiacritics
import cz.movapp.app.ui.dictionary.DictionarySectionsData
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import org.json.JSONObject
import java.io.IOException
import java.util.*

class DictionaryDatasource {

    private val sectionsCache = mutableMapOf<String,List<DictionarySectionsData>>()
    private val translationsCache = mutableMapOf<String,List<DictionaryTranslationsData>>()

    private fun loadSectionsFromAssets(context: Context, langStorageString: String): List<DictionarySectionsData> {
        var jsonString: String = ""
        var dict = mutableListOf<DictionarySectionsData>()

        try {
            jsonString = context.assets.open("${langStorageString}-dictionary.json").bufferedReader().use { it.readText() }
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

    private fun loadTranslationsFromAssets(context: Context, langStorageString: String): List<DictionaryTranslationsData> {
        var jsonString: String = ""
        var translations = mutableListOf<DictionaryTranslationsData>()

        try {
            jsonString = context.assets.open("${langStorageString}-dictionary.json").bufferedReader().use { it.readText() }
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
                    stripDiacritics(jsonObjMainItem.getString("translation").lowercase(Locale.getDefault())),
                    jsonObjMainItem.getString("sound_url"),
                    jsonObjMainItem.getString("sound_url").replace("https://data.movapp.eu/", ""),

                    jsonObjSourceItem.getString("translation"),
                    jsonObjSourceItem.getString("transcription"),
                    stripDiacritics(jsonObjSourceItem.getString("translation").lowercase(Locale.getDefault())),
                    jsonObjSourceItem.getString("sound_url"),
                    jsonObjSourceItem.getString("sound_url").replace("https://data.movapp.eu/", "")
                )
            )
        }

        return translations
    }

    fun loadSections(context: Context, langPair: LanguagePair): List<DictionarySectionsData> {
        var langStorageString = createLangAssetsString(langPair)
        return lazySectionsCacheLoad(context, langStorageString)
    }

    fun loadTranslations(context: Context, langPair: LanguagePair): List<DictionaryTranslationsData> {
        var langStorageString = createLangAssetsString(langPair)
        return lazyTranslationsCacheLoad(context, langStorageString)
    }

    private fun lazySectionsCacheLoad(context: Context, langStorageString: String): List<DictionarySectionsData> {
        var selected = sectionsCache[langStorageString]
        return if (selected == null) {
            selected = loadSectionsFromAssets(context, langStorageString)
            sectionsCache[langStorageString] = selected
            selected
        } else {
            selected
        }
    }

    private fun lazyTranslationsCacheLoad(context: Context, langStorageString: String): List<DictionaryTranslationsData> {
        var selected = translationsCache[langStorageString]
        return if (selected == null) {
            selected = loadTranslationsFromAssets(context, langStorageString)
            translationsCache[langStorageString] = selected
            selected
        } else {
            selected
        }
    }
}