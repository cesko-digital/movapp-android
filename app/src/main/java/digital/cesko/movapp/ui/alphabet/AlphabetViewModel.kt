package digital.cesko.movapp.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import digital.cesko.movapp.App
import digital.cesko.movapp.MainViewModel
import digital.cesko.movapp.adapter.AlphabetAdapter
import digital.cesko.movapp.appModule
import digital.cesko.movapp.data.AlphabetDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlphabetViewModel(application: Application, mainViewModel: MainViewModel) : AndroidViewModel(application) {

    val currentAlphabet = MutableLiveData(AlphabetAdapter(listOf()))

    init {
        loadAlphabet(getApplication<App>().appModule().alphabetDataSource, mainViewModel.fromUa.value)
    }

    private fun loadAlphabet(alphabetDatasource: AlphabetDatasource, fromUa: Boolean?) {
        viewModelScope.launch(Dispatchers.IO) {
            fromUa?.let { alphabetDatasource.load(it) }
                ?.let { currentAlphabet.postValue(AlphabetAdapter(it))  }
        }
    }

    fun setCurrentAlphabet(fromUa: Boolean) {
        loadAlphabet(getApplication<App>().appModule().alphabetDataSource, fromUa)
    }

    class Factory(private val application: Application, private val mainViewModel: MainViewModel) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlphabetViewModel(application, mainViewModel) as T
        }
    }

}