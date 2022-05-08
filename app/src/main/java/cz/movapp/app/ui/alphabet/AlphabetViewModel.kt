package cz.movapp.app.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.appModule
import cz.movapp.app.data.Language
import cz.movapp.app.data.SharedPrefsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlphabetViewModel(application: Application, mainViewModel: MainViewModel, language: String) :
    AndroidViewModel(application) {

    val alphabetsState = MutableLiveData<AlphabetState>(AlphabetState( isLoaded = false))

    data class AlphabetState(
        val alphabetData: List<AlphabetData> = listOf(),
        val scrollPositions: Map<String, Int> = mapOf(),
        val lang: String = Language.Ukrainian.langCode,
        val isLoaded: Boolean = false
    ) {
    }

    init {

            viewModelScope.launch(Dispatchers.IO) {
                val storedScrollPositions =
                    appModule().stateStore.restoreState(AlphabetStateKeys.SCROLL_POSITIONS)

                val scrollPositions = storedScrollPositions.first()
                val alphabetData = appModule().alphabetDataSource.load(language)

                withContext(Dispatchers.Main){
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
        lang: String = alphabetsState.value!!.lang,
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
        lang: String,
        oldScrollPosition: Int
    ) =
        if (alphaState.isLoaded) {
            alphaState.scrollPositions.toMutableMap() + mapOf(lang to oldScrollPosition)
        } else {
            alphaState.scrollPositions
        }

    private fun appModule() = getApplication<App>().appModule()


    override fun onCleared() {
        appModule().stateStore.saveState(
            AlphabetStateKeys.SCROLL_POSITIONS,
            alphabetsState.value!!.scrollPositions
        )
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application, private val mainViewModel: MainViewModel, private val language: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlphabetViewModel(application, mainViewModel, language) as T
        }
    }

}