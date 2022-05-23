package cz.movapp.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException


data class Member(
    @SerializedName("name") val name: String
)

data class Section(
    @SerializedName("name") val name: String,
    @SerializedName("members") val members: List<Member>
)

data class Team(
    @SerializedName("sections") val sections: List<Section>? = null
)

class AboutTeamDatasource(private val context: Context) {

    fun loadTeam(): Team {
        var jsonString: String = ""

        try {
            jsonString = context.assets.open("team.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        return Gson().fromJson<Team>(jsonString, Team::class.java)
    }
}