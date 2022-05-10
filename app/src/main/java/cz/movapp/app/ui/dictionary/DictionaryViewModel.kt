package cz.movapp.app.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.adapter.DictionarySectionsAdapter
import cz.movapp.app.adapter.DictionaryFavoritesAdapter
import cz.movapp.app.adapter.DictionarySearchAdapter
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.data.DictionaryDatasource

class DictionaryViewModel(app: Application, favoritesViewModel: FavoritesViewModel) : AndroidViewModel(app) {

    private val _sections = MutableLiveData<DictionarySectionsAdapter>().apply {
        value = DictionarySectionsAdapter(DictionaryDatasource().loadSections(app.applicationContext))
    }

    private val _translations = MutableLiveData<DictionaryTranslationsAdapter>().apply {
        value = DictionaryTranslationsAdapter(app, DictionaryDatasource().loadTranslations(app.applicationContext), favoritesViewModel)
    }

    private val _favorites = MutableLiveData<DictionaryFavoritesAdapter>().apply {
        value = DictionaryFavoritesAdapter(app, DictionaryDatasource().loadTranslations(app.applicationContext), favoritesViewModel)
    }

    private val _searches = MutableLiveData<DictionarySearchAdapter>().apply {
        value = DictionarySearchAdapter(app, DictionaryDatasource().loadTranslations(app.applicationContext), favoritesViewModel)
    }

    val sections: LiveData<DictionarySectionsAdapter> = _sections
    val translations: LiveData<DictionaryTranslationsAdapter> = _translations
    val favorites: LiveData<DictionaryFavoritesAdapter> = _favorites
    val searches: LiveData<DictionarySearchAdapter> = _searches

    private val _translationsIds = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    val translationsIds: MutableLiveData<MutableList<String>> = _translationsIds

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