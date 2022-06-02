package cz.movapp.app.adapter

import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData

class DictionaryFavoritesAdapter(
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : DictionaryPhraseSectionDetailAdapter(wholeDataset, favoritesViewModel)