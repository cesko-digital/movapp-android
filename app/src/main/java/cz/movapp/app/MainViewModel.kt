package cz.movapp.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.movapp.app.data.LanguagePair


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedLanguage = MutableLiveData(LanguagePair.getDefault())

    val selectedLanguage: LiveData<LanguagePair>
        get() = _selectedLanguage

    fun selectLanguage(lang: LanguagePair) {
        _selectedLanguage.value = lang
    }
}