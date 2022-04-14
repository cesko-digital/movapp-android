package digital.cesko.movapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDAO {

    @Query("SELECT * from favorites WHERE translation_id = :translationId")
    fun getFavorite(translationId: String): Flow<List<Favorites>>

    @Query("SELECT * FROM favorites")
    fun getAllFavorites():  Flow<List<Favorites>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Favorites)
    //suspend fun insert(item: Favorites)

    @Delete
    fun delete(item: Favorites)
    //suspend fun delete(item: Favorites)
}