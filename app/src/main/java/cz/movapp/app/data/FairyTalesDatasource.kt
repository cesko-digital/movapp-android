package cz.movapp.app.data

import android.content.Context
import android.graphics.drawable.Drawable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException

data class MetaFairyTaleTitle(
    @SerializedName("cs") val cs: String? = null,
    @SerializedName("uk") val uk: String? = null,
) {
    fun getValue(lang: String): String? = when (lang) {
        "cs" -> cs
        "uk" -> uk
        else -> null
    }
}

data class MetaFairyTale(
    @SerializedName("title") val title: MetaFairyTaleTitle,
    @SerializedName("slug") val slug: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("supportedLanguages") val supportedLanguages: List<String>,
)

data class MetaFairyTales(
    @SerializedName("stories") val stories: List<MetaFairyTale>? = null
)

data class Column(
    @SerializedName("text") val text: String,
    @SerializedName("start") val start: Float,
    @SerializedName("end") val end: Float,
)

data class TimeData(
    @SerializedName("cs") val cs: Column,
    @SerializedName("uk") val uk: Column,
) {
    fun getValue(lang: String): Column? = when (lang) {
        "cs" -> cs
        "uk" -> uk
        else -> null
    }
}

data class FairyTale(
    @SerializedName("timeline") val sections: List<TimeData>? = null
)
class FairyTalesDatasource(private val context: Context) {
    private var cache: List<Pair<MetaFairyTale, FairyTale>>? = null
    private var cacheDrawables: Map<String,Drawable>? = null

    init {
        if (cache == null) {
            val res = mutableListOf<Pair<MetaFairyTale, FairyTale>>()
            val resDrawables = mutableMapOf<String,Drawable>()

            var jsonString: String = ""

            try {
                jsonString = context.assets.open("stories/metadata.json").bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }

            val metaData = Gson().fromJson<MetaFairyTales>(jsonString, MetaFairyTales::class.java)

            for (metaFairyTale in metaData.stories!!) {
                try {
                    jsonString = context.assets.open("stories/${metaFairyTale.slug}/metadata.json")
                        .bufferedReader().use { it.readText() }
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                }

                val fairyTale = Gson().fromJson<FairyTale>(jsonString, FairyTale::class.java)

                res.add(Pair(metaFairyTale, fairyTale))




                try {
                    val imageStream = context!!.assets.open("images/android/${metaFairyTale.slug}/${metaFairyTale.slug}.webp")
                    resDrawables[metaFairyTale.slug] = Drawable.createFromStream(imageStream, null)!!
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                }

            }

            cache = res
            cacheDrawables = resDrawables
        }
    }

    fun loadFairyTales(): List<Pair<MetaFairyTale, FairyTale>> {
        return cache!!
    }

    fun getMetaFairyTale(slug: String): MetaFairyTale? {
        for (i in cache!!) {
            if (i.first.slug == slug) {
                return i.first
            }
        }

        return null
    }

    fun getFairyTale(slug: String): FairyTale? {
        for (i in cache!!) {
            if (i.first.slug == slug) {
                return i.second
            }
        }

        return null
    }

    fun getFairyTaleDrawables(): Map<String,Drawable> {
        return cacheDrawables!!
    }
}