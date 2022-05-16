package cz.movapp.app.data

import android.content.Context
import cz.movapp.app.ui.children.ChildrenData
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChildrenDatasource {

    fun loadChildren(context: Context): List<ChildrenData>{
        var jsonString: String = ""
        val children = mutableListOf<ChildrenData>()

        try {
            jsonString = context.assets.open("dictionary/uk-cs-dictionary.json").bufferedReader().use { it.readText() }
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
                    val forKidsId = jsonPhrasesArr.getString(j)
                    val jsonItemObj = jsonPhrasesObj.getJSONObject(forKidsId)

                    val jsonObjMainItem = jsonItemObj.getJSONObject("main")
                    val jsonObjSourceItem = jsonItemObj.getJSONObject("source")

                    val imagePath = "dictionary/images/${forKidsId}/${forKidsId}.webp"

                    children.add(ChildrenData(
                        forKidsId,
                        jsonObjMainItem.getString("translation"),
                        jsonObjMainItem.getString("transcription"),
                        jsonObjMainItem.getString("sound_url"),
                        jsonObjSourceItem.getString("translation"),
                        jsonObjSourceItem.getString("transcription"),
                        jsonObjSourceItem.getString("sound_url"),
                        imagePath
                    )
                    )
                }
            }
        }

        return children
    }
}