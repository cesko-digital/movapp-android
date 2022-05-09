package cz.movapp.app

import androidx.lifecycle.*
import cz.movapp.app.data.Favorites
import cz.movapp.app.data.FavoritesDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesDao: FavoritesDAO): ViewModel() {

    val favorites: LiveData<List<Favorites>> = favoritesDao.getAllFavorites()

    private fun insertItem(item: Favorites) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.insert(item)
        }
    }

    private fun deleteItem(item: Favorites) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.delete(item)
        }
    }

    fun removeFavorite(translationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.deleteTranslationId(translationId)
        }
    }

    private fun getNewItemEntry(favoritesId: String): Favorites {
        return Favorites(
            translationId = favoritesId
        )
    }

    fun addFavorites(favoritesId: String) {
        val newItem = getNewItemEntry(favoritesId)
        insertItem(newItem)
    }
}

class FavoritesViewModelFactory(private val favoritesDao: FavoritesDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}