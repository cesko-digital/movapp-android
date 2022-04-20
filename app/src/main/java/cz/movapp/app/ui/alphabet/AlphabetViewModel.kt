package cz.movapp.app.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.App
import cz.movapp.app.MainViewModel
import cz.movapp.app.appModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlphabetViewModel(application: Application, mainViewModel: MainViewModel) :
    AndroidViewModel(application) {

    val alphabetsState = MutableLiveData<AlphabetState>(AlphabetState( isLoaded = false))

    data class AlphabetState(
        val alphabetData: List<AlphabetData> = listOf(),
        val scrollPositions: Map<Boolean, Int> = mapOf(true to 0, false to 0),
        val fromUa: Boolean = true,
        val isLoaded: Boolean = false
    ) {
    }

    init {
        mainViewModel.fromUa.value?.let { fromUa: Boolean ->

            viewModelScope.launch(Dispatchers.IO) {
                val storedScrollPositions =
                    appModule().stateStore.restoreState(AlphabetStateKeys.SCROLL_POSITIONS)

                val scrollPositions = storedScrollPositions.first()
                val alphabetData = appModule().alphabetDataSource.load(fromUa)

                withContext(Dispatchers.Main){
                    val newValue = alphabetsState.value!!.copy(
                        alphabetData = alphabetData,
                        fromUa = fromUa,
                        scrollPositions = scrollPositions ?: AlphabetState().scrollPositions,
                        isLoaded = true
                    )
                    alphabetsState.setValue(newValue)
                }
            }

        }
    }

    fun onLanguageChanged(
        fromUa: Boolean = alphabetsState.value!!.fromUa,
        oldScrollPosition: Int
    ) {
        if (alphabetsState.value!!.isLoaded){
            viewModelScope.launch(Dispatchers.IO) {
                appModule().alphabetDataSource
                    .load(fromUa)
                    .let {

                        withContext(Dispatchers.Main){
                            val newValue = alphabetsState.value!!.copy(
                                alphabetData = it,
                                scrollPositions = ifIsLoadedUpdateScrollPosition(
                                    alphabetsState.value!!,
                                    fromUa,
                                    oldScrollPosition
                                ),
                                fromUa = fromUa,
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
        fromUa: Boolean,
        oldScrollPosition: Int
    ) =
        if (alphaState.isLoaded) {
            alphaState.scrollPositions.toMutableMap() + mapOf(fromUa to oldScrollPosition)
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
    class Factory(private val application: Application, private val mainViewModel: MainViewModel) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlphabetViewModel(application, mainViewModel) as T
        }
    }

}