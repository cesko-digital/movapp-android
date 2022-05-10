package cz.movapp.app.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.appModule
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.LanguagePair.Companion.getDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlphabetViewModel(application: Application, language: Language) :
    AndroidViewModel(application) {

    val alphabetsState = MutableLiveData<AlphabetState>(AlphabetState( isLoaded = false))

    data class AlphabetState(
        val alphabetData: List<AlphabetData> = listOf(),
        val scrollPositions: Map<String, Int> = mapOf(),
        val lang: Language = getDefault().from,
        val isLoaded: Boolean = false
    ) {
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val storedScrollPositions =
                appModule().stateStore.restoreState(AlphabetStateKeys.SCROLL_POSITIONS)

            val scrollPositions = storedScrollPositions.first()
            val alphabetData = appModule().alphabetDataSource.load(language)

            withContext(Dispatchers.Main) {
                val newValue = alphabetsState.value!!.copy(
                    alphabetData = alphabetData,
                    lang = language,
                    scrollPositions = scrollPositions ?: AlphabetState().scrollPositions,
                    isLoaded = true
                )
                alphabetsState.setValue(newValue)
            }
        }
    }

    fun onLanguageChanged(
        lang: Language = alphabetsState.value!!.lang,
        oldScrollPosition: Int
    ) {
        if (alphabetsState.value!!.isLoaded){
            viewModelScope.launch(Dispatchers.IO) {
                appModule().alphabetDataSource
                    .load(lang)
                    .let {

                        withContext(Dispatchers.Main){
                            val newValue = alphabetsState.value!!.copy(
                                alphabetData = it,
                                scrollPositions = ifIsLoadedUpdateScrollPosition(
                                    alphabetsState.value!!,
                                    lang,
                                    oldScrollPosition
                                ),
                                lang = lang,
                                isLoaded = true,
                            )
                            alphabetsState.setValue(newValue)
                        }
                    }
            }
        }
    }

    private fun ifIsLoadedUpdateScrollPosition(
        alphaState: AlphabetState,
        lang: Language,
        oldScrollPosition: Int
    ) =
        if (alphaState.isLoaded) {
            alphaState.scrollPositions.toMutableMap() + mapOf(lang.langCode to oldScrollPosition)
        } else {
            alphaState.scrollPositions
        }

    private fun appModule() = getApplication<App>().appModule()

    fun storeState() {
        appModule().stateStore.saveState(
            AlphabetStateKeys.SCROLL_POSITIONS,
            alphabetsState.value!!.scrollPositions
        )
    }

    override fun onCleared() {
        storeState()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application, private val language: Language) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlphabetViewModel(application, language) as T
        }
    }

}