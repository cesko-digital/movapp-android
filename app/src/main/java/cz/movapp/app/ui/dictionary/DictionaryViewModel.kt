package cz.movapp.app.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.adapter.DictionaryFavoritesAdapter
import cz.movapp.app.adapter.DictionaryPhrasesSearchAllAdapter
import cz.movapp.app.adapter.DictionaryPhraseSectionsAdapter
import cz.movapp.app.adapter.DictionaryPhraseSectionDetailAdapter
import cz.movapp.app.data.DictionaryDatasource

class DictionaryViewModel(app: Application, favoritesViewModel: FavoritesViewModel) :
    AndroidViewModel(app) {


    val sections: LiveData<DictionaryPhraseSectionsAdapter> =
        MutableLiveData<DictionaryPhraseSectionsAdapter>().apply {
            value =
                DictionaryPhraseSectionsAdapter(DictionaryDatasource().loadSections(app.applicationContext))
        }

    val translations: LiveData<DictionaryPhraseSectionDetailAdapter> =
        MutableLiveData<DictionaryPhraseSectionDetailAdapter>().apply {
            value = DictionaryPhraseSectionDetailAdapter(
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


    val translationsSearches: LiveData<DictionaryPhrasesSearchAllAdapter> =
        MutableLiveData<DictionaryPhrasesSearchAllAdapter>().apply {
            value = DictionaryPhrasesSearchAllAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext),
                favoritesViewModel
            )
        }

    val favoritesSearches: LiveData<DictionaryPhrasesSearchAllAdapter> =
        MutableLiveData<DictionaryPhrasesSearchAllAdapter>().apply {
            value = DictionaryPhrasesSearchAllAdapter(
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