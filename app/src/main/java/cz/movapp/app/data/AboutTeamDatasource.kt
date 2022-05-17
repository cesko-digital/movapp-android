package cz.movapp.app.data

import android.content.Context
import com.google.gson.Gson
import java.io.IOException


data class Member(
    val name: String
) {}

data class Section(
    val name: String,
    val members: List<Member>
) {}

data class Team(
    val sections: List<Section>? = null
) {}

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