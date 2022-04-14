package digital.cesko.movapp

import androidx.lifecycle.*
import digital.cesko.movapp.data.Favorites
import digital.cesko.movapp.data.FavoritesDAO
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesDao: FavoritesDAO): ViewModel() {

    val allFavorites: LiveData<List<Favorites>> = favoritesDao.getAllFavorites().asLiveData()


    private fun insertItem(item: Favorites) {
        viewModelScope.launch {
            favoritesDao.insert(item)
        }
    }

    private fun deleteItem(item: Favorites) {
        viewModelScope.launch {
            favoritesDao.delete(item)
        }
    }

    fun deleteFavorite(translationId: String) {
        val favorites = favoritesDao.getFavorite(translationId).asLiveData().value as List<Favorites>
        for (favorite in favorites) {
            deleteItem(favorite)
        }
    }

    private fun getNewItemEntry(favoritesId: String): Favorites {
        return Favorites(
            translationId = favoritesId
        )
    }

    fun addNewItem(favoritesId: String) {
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