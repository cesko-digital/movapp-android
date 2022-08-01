package cz.movapp.app.ui.dictionary

import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.data.LanguagePair

class DictionaryFavoritesAdapter(
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
    private val languagePair: LanguagePair,
) : DictionaryPhraseSectionDetailAdapter(wholeDataset, favoritesViewModel, languagePair)