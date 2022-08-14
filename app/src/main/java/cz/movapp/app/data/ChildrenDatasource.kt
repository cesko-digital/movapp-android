package cz.movapp.app.data

import android.content.Context
import cz.movapp.android.createLangAssetsString
import cz.movapp.app.ui.children.ChildrenDictionaryData
import org.json.JSONObject
import java.io.IOException

class ChildrenDatasource {

    private val cache = mutableMapOf<String,List<ChildrenDictionaryData>>()

    private fun loadChildrenFromAssets(context: Context, langStorageString: String): List<ChildrenDictionaryData> {
        var jsonString: String = ""
        val children = mutableListOf<ChildrenDictionaryData>()

        try {
            jsonString = context.assets.open("${langStorageString}-dictionary.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonCategoriesArr = JSONObject(jsonString).getJSONArray("categories")
        val jsonPhrasesObj = JSONObject(jsonString).getJSONObject("phrases")

        for (i in 0 until jsonCategoriesArr.length()) {
            val jsonObj = jsonCategoriesArr.getJSONObject(i)

            // we seek "For kids" id
            if (jsonObj.getString("id") == "recSHyEn6N0hAqUBp") {
                val jsonPhrasesArr = jsonObj.getJSONArray("phrases")

                for (j in 0 until jsonPhrasesArr.length()) {
                    try {
                        val forKidsId = jsonPhrasesArr.getString(j)
                        val jsonItemObj = jsonPhrasesObj.getJSONObject(forKidsId)

                        val jsonObjMainItem = jsonItemObj.getJSONObject("main")
                        val jsonObjSourceItem = jsonItemObj.getJSONObject("source")

                        val imagePath = "images/android/${forKidsId}/${forKidsId}.webp"

                        children.add(
                            ChildrenDictionaryData(
                                forKidsId,

                                jsonObjMainItem.getString("translation"),
                                jsonObjMainItem.getString("transcription"),
                                jsonObjMainItem.getString("sound_url"),
                                jsonObjMainItem.getString("sound_url")
                                    .replace("https://data.movapp.eu/", ""),

                                jsonObjSourceItem.getString("translation"),
                                jsonObjSourceItem.getString("transcription"),
                                jsonObjSourceItem.getString("sound_url"),
                                jsonObjSourceItem.getString("sound_url")
                                    .replace("https://data.movapp.eu/", ""),

                                imagePath
                            )
                        )
                    } catch (e: Exception) {
                        continue
                    }
                }
            }
        }

        return children
    }

    fun loadChildren(context: Context, langPair: LanguagePair): List<ChildrenDictionaryData> {
        var langStorageString = createLangAssetsString(langPair)
        return lazyChildrenCacheLoad(context, langStorageString)
    }

    private fun lazyChildrenCacheLoad(context: Context, langStorageString: String): List<ChildrenDictionaryData> {
        var selected = cache[langStorageString]
        return if (selected == null) {
            selected = loadChildrenFromAssets(context, langStorageString)
            cache[langStorageString] = selected
            selected
        } else {
            selected
        }
    }
}