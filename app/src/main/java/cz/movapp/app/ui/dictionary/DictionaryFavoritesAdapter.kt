package cz.movapp.app.ui.dictionary

import cz.movapp.app.FavoritesViewModel

class DictionaryFavoritesAdapter(
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : DictionaryPhraseSectionDetailAdapter(wholeDataset, favoritesViewModel)