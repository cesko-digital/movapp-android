package digital.cesko.movapp.ui.alphabet

import android.app.Application
import androidx.lifecycle.*
import digital.cesko.movapp.adapter.AlphabetAdapter
import digital.cesko.movapp.data.AlphabetDatasource

class AlphabetViewModel(application: Application, language: String) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _alphabet = MutableLiveData<AlphabetAdapter>().apply {
        value = AlphabetAdapter(context, AlphabetDatasource().loadLanguage(context, language))
    }

    val alphabet: MutableLiveData<AlphabetAdapter> = _alphabet

}

class AlphabetViewModelFactory(private val application: Application, private val language: String): ViewModelProvider.NewInstanceFactory()  {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return AlphabetViewModel(application, language) as T
    }
}