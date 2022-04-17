package digital.cesko.movapp.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorites(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    /**
     *  value from json key: "sections[n].id"
     */
    @NonNull @ColumnInfo(name = "translation_id") val translationId: String,
)