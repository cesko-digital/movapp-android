package cz.movapp.app.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.adapter.DictionaryAdapter
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.data.DictionaryDatasource

class DictionaryViewModel(app: Application, favoritesViewModel: FavoritesViewModel) : AndroidViewModel(app) {

    private val _sections = MutableLiveData<DictionaryAdapter>().apply {
        value = DictionaryAdapter(DictionaryDatasource().loadSections(app.applicationContext))
    }

    private val _searches = MutableLiveData<DictionaryTranslationsAdapter>().apply {
        value = DictionaryTranslationsAdapter(app, DictionaryDatasource().loadTranslations(app.applicationContext), favoritesViewModel)
    }

    val sections: LiveData<DictionaryAdapter> = _sections
    val searches: LiveData<DictionaryTranslationsAdapter> = _searches

    val translations = DictionaryTranslationsAdapter(
        app.applicationContext,
        DictionaryDatasource().loadTranslations(app.applicationContext),
        favoritesViewModel
    )

    fun selectedTranslations(translationIds: List<String>):List<DictionaryTranslationsData>? {
        if (translationIds.isEmpty())
            return translations.getSelectedTranslations(translations.favoritesIds)
        return translations.getSelectedTranslations(translationIds)
    }

    private val _searchQuery = MutableLiveData<String>()

    val searchQuery: LiveData<String>
        get() = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

class DictionaryViewModelFactory(private val application: Application, private val favoritesViewModel: FavoritesViewModel): ViewModelProvider.NewInstanceFactory()  {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return DictionaryViewModel(application, favoritesViewModel) as T
    }
}