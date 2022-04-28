package cz.movapp.app.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import cz.movapp.app.ui.children.ChildrenData
import org.json.JSONArray
import java.io.IOException
import kotlin.math.roundToInt

class ChildrenDatasource {

    fun loadChildren(context: Context): List<ChildrenData>{
        var jsonString: String = ""
        val children = mutableListOf<ChildrenData>()

        try {
            jsonString = context.assets.open("children/cs-uk-children.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val jsonArr = JSONArray(jsonString)

        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)

            val imageName = jsonObj.getString("image")
            var image: Drawable? = null

            try {
                val imageStream = context.assets.open("children/images/%s.webp".format(imageName))
                image = Drawable.createFromStream(imageStream, imageName)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

            if (image == null)
                continue

            children.add(ChildrenData(
                "null",
                jsonObj.getString("cz_translation"),
                jsonObj.getString("cz_transcription"),
                jsonObj.getString("ua_translation"),
                jsonObj.getString("ua_transcription"),
                image
                )
            )
        }

        return children
    }
}