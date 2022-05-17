package cz.movapp.app.ui.about

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cz.movapp.app.data.AboutTeamDatasource

class AboutTeamViewModel(application: Application) : AndroidViewModel(application) {

    val team = AboutTeamDatasource(application.applicationContext).loadTeam()
}