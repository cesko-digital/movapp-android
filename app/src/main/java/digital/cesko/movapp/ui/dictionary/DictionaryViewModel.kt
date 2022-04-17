package digital.cesko.movapp.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import digital.cesko.movapp.FavoritesViewModel
import digital.cesko.movapp.adapter.DictionaryAdapter
import digital.cesko.movapp.adapter.DictionaryContentAdapter
import digital.cesko.movapp.data.DictionaryDatasource

class DictionaryViewModel(app: Application, favoritesViewModel: FavoritesViewModel) : AndroidViewModel(app) {

    private val _currentSectionTitle = MutableLiveData<String>()

    val currentSectionTitle: LiveData<String>
        get() = _currentSectionTitle

    private val _sections = MutableLiveData<DictionaryAdapter>().apply {
        value = DictionaryAdapter(app.applicationContext, DictionaryDatasource().loadSections(app.applicationContext))
    }

    val sections: LiveData<DictionaryAdapter> = _sections

    val translations = DictionaryContentAdapter(
        app.applicationContext,
        DictionaryDatasource().loadTranslations(app.applicationContext),
        favoritesViewModel
    )

    fun setCustomTitle(title: String) {
        _currentSectionTitle.value = title
    }

    fun selectedTranslations(sectionId: String, translationIds: List<String>):List<DictionaryTranslationsData>? {
        _currentSectionTitle.value = _sections.value?.getSectionTitle(sectionId)

        return translations.getSelectedTranslations(translationIds)
    }

    fun search(constraint: String) {
        translations.search(constraint)
    }
}

class DictionaryViewModelFactory(private val application: Application, private val favoritesViewModel: FavoritesViewModel): ViewModelProvider.NewInstanceFactory()  {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return DictionaryViewModel(application, favoritesViewModel) as T
    }
}