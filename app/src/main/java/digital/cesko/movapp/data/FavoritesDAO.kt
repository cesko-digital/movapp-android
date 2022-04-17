package digital.cesko.movapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritesDAO {

    @Query("DELETE from favorites WHERE translation_id = :translationId")
    fun deleteTranslationId(translationId: String)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): LiveData<List<Favorites>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Favorites)

    @Delete
    fun delete(item: Favorites)
}