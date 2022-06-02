package cz.movapp.app.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.android.DataStoreKey
import cz.movapp.app.App
import cz.movapp.app.appModule
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.LanguagePair.Companion.getDefault
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlphabetViewModel(application: Application, langPair: LanguagePair, direction: AlphabetDirection) :
    AndroidViewModel(application) {

    val alphabetsState = MutableLiveData<AlphabetState>(AlphabetState( isLoaded = false))

    data class AlphabetState(
        val alphabetData: List<AlphabetData> = listOf(),
        val scrollPositions: Map<String, Int> = mapOf(),
        val sourceLang: Language = getDefault().from,
        val mainLang: Language = getDefault().to,
        val isLoaded: Boolean = false
    ) {
    }

    private lateinit var stateKey: DataStoreKey<Map<String, Int>>
    private lateinit var sourceLang: Language
    private lateinit var mainLang: Language

    init {
        viewModelScope.launch(Dispatchers.IO) {
             if (direction == AlphabetDirection.FROM) {
                 stateKey = AlphabetStateKeys.SCROLL_POSITIONS_FROM
                 sourceLang = langPair.from
                 mainLang = langPair.to
             } else {
                 stateKey = AlphabetStateKeys.SCROLL_POSITIONS_TO
                 sourceLang = langPair.to
                 mainLang = langPair.from
             }

            val storedScrollPositions =
                appModule().dataStore.restoreState(stateKey)

            val scrollPositions = storedScrollPositions.first()
            val alphabetData = appModule().alphabetDataSource.load(sourceLang, mainLang)

            withContext(Dispatchers.Main) {
                val newValue = alphabetsState.value!!.copy(
                    alphabetData = alphabetData,
                    sourceLang = sourceLang,
                    mainLang = mainLang,
                    scrollPositions = scrollPositions ?: AlphabetState().scrollPositions,
                    isLoaded = true
                )
                alphabetsState.setValue(newValue)
            }
        }
    }

    fun onLanguageChanged(
        sourceLang: Language = alphabetsState.value!!.sourceLang,
        mainLang: Language = alphabetsState.value!!.mainLang,
        oldScrollPosition: Int
    ) {
        if (alphabetsState.value!!.isLoaded){
            viewModelScope.launch(Dispatchers.IO) {
                appModule().alphabetDataSource
                    .load(sourceLang, mainLang)
                    .let {

                        withContext(Dispatchers.Main){
                            val newValue = alphabetsState.value!!.copy(
                                alphabetData = it,
                                scrollPositions = ifIsLoadedUpdateScrollPosition(
                                    alphabetsState.value!!,
                                    sourceLang,
                                    oldScrollPosition
                                ),
                                sourceLang = sourceLang,
                                mainLang = mainLang,
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

    override fun onCleared() {
        appModule().dataStore.saveState(
            stateKey,
            alphabetsState.value!!.scrollPositions
        )
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application, private val langPair: LanguagePair, private val direction: AlphabetDirection) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlphabetViewModel(application, langPair, direction) as T
        }
    }

}