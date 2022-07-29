package cz.movapp.app.ui.dictionary

import android.app.Application
import androidx.lifecycle.*
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.data.DictionaryDatasource
import cz.movapp.app.data.LanguagePair

class DictionaryViewModel(private val app: Application, private val favoritesViewModel: FavoritesViewModel, private var langPair: LanguagePair) :
    AndroidViewModel(app) {


    val sections: MutableLiveData<DictionaryPhraseSectionsAdapter> =
        MutableLiveData<DictionaryPhraseSectionsAdapter>().apply {
            value =
                DictionaryPhraseSectionsAdapter(DictionaryDatasource().loadSections(app.applicationContext, langPair))
        }

    val translations: MutableLiveData<DictionaryPhraseSectionDetailAdapter> =
        MutableLiveData<DictionaryPhraseSectionDetailAdapter>().apply {
            value = DictionaryPhraseSectionDetailAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )
        }

    val favorites: MutableLiveData<DictionaryFavoritesAdapter> =
        MutableLiveData<DictionaryFavoritesAdapter>().apply {
            value = DictionaryFavoritesAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )
        }


    val translationsSearches: MutableLiveData<DictionaryPhrasesSearchAllAdapter> =
        MutableLiveData<DictionaryPhrasesSearchAllAdapter>().apply {
            value = DictionaryPhrasesSearchAllAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )
        }

    val favoritesSearches: MutableLiveData<DictionaryPhrasesSearchAllAdapter> =
        MutableLiveData<DictionaryPhrasesSearchAllAdapter>().apply {
            value = DictionaryPhrasesSearchAllAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )
        }

    fun onLanguageChanged(newLangPair: LanguagePair) {
        langPair = newLangPair

        sections.value =
            DictionaryPhraseSectionsAdapter(DictionaryDatasource().loadSections(app.applicationContext, langPair))

        translations.value =
            DictionaryPhraseSectionDetailAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )

        favorites.value =
            DictionaryFavoritesAdapter(
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )

        translationsSearches.value =
            DictionaryPhrasesSearchAllAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
                favoritesViewModel
            )

        favoritesSearches.value =
            DictionaryPhrasesSearchAllAdapter(
                app,
                DictionaryDatasource().loadTranslations(app.applicationContext, langPair),
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
        return DictionaryViewModel(application, favoritesViewModel, LanguagePair.getDefault()) as T
    }
}