package cz.movapp.app.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.adapter.DictionaryFavoritesAdapter
import cz.movapp.app.adapter.DictionarySearchAdapter
import cz.movapp.app.adapter.DictionarySectionsAdapter
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.data.DictionaryDatasource

class DictionaryViewModel(app: Application, favoritesViewModel: FavoritesViewModel) :
    AndroidViewModel(app) {


    val sections: LiveData<DictionarySectionsAdapter> =
        MutableLiveData<DictionarySectionsAdapter>().apply {
            value =
                DictionarySectionsAdapter(DictionaryDatasource().loadSections(app.applicationContext))
        }

    val translations: LiveData<DictionaryTranslationsAdapter> =
        MutableLiveData<DictionaryTranslationsAdapter>().apply {
            value = DictionaryTranslationsAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext),
                favoritesViewModel
            )
        }

    val favorites: LiveData<DictionaryFavoritesAdapter> =
        MutableLiveData<DictionaryFavoritesAdapter>().apply {
            value = DictionaryFavoritesAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext),
                favoritesViewModel
            )
        }


    val translationsSearches: LiveData<DictionarySearchAdapter> =
        MutableLiveData<DictionarySearchAdapter>().apply {
            value = DictionarySearchAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext),
                favoritesViewModel
            )
        }

    val favoritesSearches: LiveData<DictionarySearchAdapter> =
        MutableLiveData<DictionarySearchAdapter>().apply {
            value = DictionarySearchAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext),
                favoritesViewModel
            )
        }

    val translationsIds: MutableLiveData<MutableList<String>> =
        MutableLiveData<MutableList<String>>().apply {
            value = mutableListOf()
        }

    val searchQuery = MutableLiveData<String>()

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}

class DictionaryViewModelFactory(private val application: Application, private val favoritesViewModel: FavoritesViewModel): ViewModelProvider.NewInstanceFactory()  {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return DictionaryViewModel(application, favoritesViewModel) as T
    }
}