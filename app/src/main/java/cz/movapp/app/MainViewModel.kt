package cz.movapp.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.movapp.app.data.LanguagePair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val _selectedLanguage = MutableLiveData(LanguagePair.getDefault())

    private fun appModule() = getApplication<App>().appModule()

    val selectedLanguage: LiveData<LanguagePair>
        get() = _selectedLanguage

    fun selectLanguage(lang: LanguagePair) {
        _selectedLanguage.value = lang
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedLanguage = appModule().dataStore.restoreState(LanguageStateKeys.SELECTED_PAIR)
            val lang = selectedLanguage.first()
            if (lang != null)
                _selectedLanguage.postValue(lang)
        }
    }

    fun storeLanguage() {
        appModule().dataStore.saveState(
            LanguageStateKeys.SELECTED_PAIR,
            selectedLanguage.value!!
        )
    }

    override fun onCleared() {
        storeLanguage()
    }
}