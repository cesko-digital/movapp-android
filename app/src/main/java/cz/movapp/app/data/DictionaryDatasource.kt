package cz.movapp.app.data

import android.content.Context
import cz.movapp.android.createLangAssetsString
import cz.movapp.android.stripDiacritics
import cz.movapp.app.ui.dictionary.DictionaryMetaCategoryData
import cz.movapp.app.ui.dictionary.DictionarySectionsData
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

object DictionaryDatasource {

    private val sectionsCache = mutableMapOf<String,List<DictionarySectionsData>>()
    private val translationsCache = mutableMapOf<String,List<DictionaryTranslationsData>>()
    private val metaCategoriesCache = mutableMapOf<String,List<DictionaryMetaCategoryData>>()

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

            try {
                if (jsonObj.getBoolean("hidden")) {
                    continue
                }
            } catch (_: Exception) {}

            try {
                if (jsonObj.getBoolean("metaOnly")) {
                    continue
                }
            } catch (_: Exception) {}

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

    private fun loadMetaCategoriesFromAssets(context: Context, langStorageString: String): List<DictionaryMetaCategoryData> {
        var jsonString = ""
        var dict = mutableListOf<DictionaryMetaCategoryData>()

        try {
            jsonString = context.assets.open("${langStorageString}-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONObject(jsonString).getJSONArray("categories")

        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)

            if (!jsonObj.optBoolean("metaOnly", false)) {
                continue
            }

            val id = jsonObj.getString("id")
            val jsonNameObj = jsonObj.getJSONObject("name")

            val metaCategories = mutableListOf<String>()
            val jsonMetaCatsArr = jsonObj.getJSONArray("metacategories")
            for (j in 0 until jsonMetaCatsArr.length()) {
                metaCategories.add(jsonMetaCatsArr.getString(j))
            }

            dict.add(DictionaryMetaCategoryData(
                id,
                jsonNameObj.getString("main"),
                jsonNameObj.getString("source"),
                metaCategories)
            )
        }

        return dict
    }

    fun loadSections(context: Context, langPair: LanguagePair): List<DictionarySectionsData> {
        return lazySectionsCacheLoad(context, createLangAssetsString(langPair))
    }

    fun loadTranslations(context: Context, langPair: LanguagePair): List<DictionaryTranslationsData> {
        return lazyTranslationsCacheLoad(context, createLangAssetsString(langPair))
    }

    fun loadMetaCategories(context: Context, langPair: LanguagePair): List<DictionaryMetaCategoryData> {
        return lazyMetaCategoriesCacheLoad(context, createLangAssetsString(langPair))
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

    private fun lazyMetaCategoriesCacheLoad(context: Context, langStorageString: String): List<DictionaryMetaCategoryData> {
        var selected = metaCategoriesCache[langStorageString]
        return if (selected == null) {
            selected = loadMetaCategoriesFromAssets(context, langStorageString)
            metaCategoriesCache[langStorageString] = selected
            selected
        } else {
            selected
        }
    }
}