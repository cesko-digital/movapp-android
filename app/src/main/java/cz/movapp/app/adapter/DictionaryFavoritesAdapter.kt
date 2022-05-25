package cz.movapp.app.adapter

import android.content.Context
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData

class DictionaryFavoritesAdapter(
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : DictionaryTranslationsAdapter(context, wholeDataset, favoritesViewModel)