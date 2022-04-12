package digital.cesko.movapp.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import digital.cesko.movapp.adapter.DictionaryAdapter
import digital.cesko.movapp.adapter.DictionaryContentAdapter
import digital.cesko.movapp.data.DictionaryDatasource

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _currentSectionId = MutableLiveData<String>()

    val currentSectionId: LiveData<String>
        get() = _currentSectionId

    private val _currentSectionTitle = MutableLiveData<String>()

    val currentSectionTitle: LiveData<String>
        get() = _currentSectionTitle

    private val _sections = MutableLiveData<DictionaryAdapter>().apply {
        value = DictionaryAdapter(context, DictionaryDatasource().loadSections(context))
    }

    val sections: LiveData<DictionaryAdapter> = _sections

    private val _translations = MutableLiveData<DictionaryContentAdapter>().apply {
        value = DictionaryContentAdapter(context, DictionaryDatasource().loadTranslations(context))
    }

    val translations: LiveData<DictionaryContentAdapter> = _translations

    fun setSelectedTranslationIds(translationIds: List<String>) {
        _translations.value?.setSelectedTranslationIds(translationIds)
    }

    fun search(constraint: String) {
        _translations.value?.filter?.filter(constraint)
        _currentSectionTitle.value = when (val title = _sections.value?.getSectionTitle(constraint)) {
            "" -> constraint
            else -> title
        }
    }
}

class DictionaryViewModelFactory(private val application: Application): ViewModelProvider.NewInstanceFactory()  {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return DictionaryViewModel(application) as T
    }
}