package cz.movapp.app.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.R
import cz.movapp.app.databinding.DictionaryTranslationItemBinding
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import java.util.*

class DictionarySearchAdapter(
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : ListAdapter<DictionaryTranslationsData, DictionarySearchAdapter.ItemViewHolder>(
    DiffCallback
) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DictionaryTranslationsData>() {
            override fun areItemsTheSame(
                oldItem: DictionaryTranslationsData,
                newItem: DictionaryTranslationsData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DictionaryTranslationsData,
                newItem: DictionaryTranslationsData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    var langPair = LanguagePair.getDefault()

    var favoritesIds = mutableListOf<String>()

    class ItemViewHolder(binding: DictionaryTranslationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DictionaryTranslationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            if (langPair.isReversed) {
                textFrom.text = item.source_translation
                textFromTrans.text = brackets(item.source_transcription)
                textTo.text = item.main_translation
                textToTrans.text = brackets(item.main_transcription)
            } else {
                textFrom.text = item.main_translation
                textFromTrans.text = brackets(item.main_transcription)
                textTo.text = item.source_translation
                textToTrans.text = brackets(item.source_transcription)
            }
        }

        setFavoriteStar(holder, favoritesIds.contains(item.id))

        holder.binding.imageFavorites.setOnClickListener {
            if (favoritesIds.contains(item.id)) {
                favoritesIds.remove(item.id)
                favoritesViewModel.removeFavorite(item.id)
                setFavoriteStar(holder, false)
            } else {
                favoritesIds.add(item.id)
                favoritesViewModel.addFavorites(item.id)
                setFavoriteStar(holder, true)
            }
        }

        holder.binding.imagePlaySoundFrom.visibility = View.GONE
        holder.binding.imagePlaySoundFrom.setOnClickListener {
            // TODO: import sounds to assets and use it here
            //playSound(holder.itemView.context, item.soundAssetFile)
        }

        holder.binding.imagePlaySoundTo.visibility = View.GONE
        holder.binding.imagePlaySoundTo.setOnClickListener {
            // TODO: import sounds to assets and use it here
            //playSound(holder.itemView.context, item.soundAssetFile)
        }
    }

    private fun brackets(s: String): CharSequence? {
        return "[${s}]"
    }

    private fun setFavoriteStar(holder: ItemViewHolder, isSet: Boolean) {
        if (isSet)
            holder.binding.imageFavorites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color.secondaryColor
                )
            )
        else
            holder.binding.imageFavorites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color.primaryColor
                )
            )
    }

    enum class LevDirection {
        UNSET, FROM, TO
    }

    fun search(constraint: String, favorites : Boolean = false) {
        val searchString = constraint.lowercase(Locale.getDefault())
        var result = if (searchString.isEmpty()) {
            wholeDataset
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset
                .filter {
                    it.main_stripped.contains(searchString) or
                            it.source_stripped.contains(searchString)
                }
                .forEach { filtered.add(it) }
            filtered.sortedWith(object : Comparator<DictionaryTranslationsData> {

                var direction: LevDirection = LevDirection.UNSET

                override fun compare(
                    t1: DictionaryTranslationsData,
                    t2: DictionaryTranslationsData
                ): Int {

                    var levT1From = levenshteinDistance(constraint, t1.main_stripped)
                    var levT2From = levenshteinDistance(constraint, t2.main_stripped)

                    var levT1To = levenshteinDistance(constraint, t1.source_stripped)
                    var levT2To = levenshteinDistance(constraint, t2.source_stripped)

                    /**
                     * detect Levenshtein direction
                     * Levenshtein can not be used on both direction at the same time
                     * (because it says A > B and B > C but C < A)
                     *
                     * in order to detect direction we simply look at sum of lev. dist.
                     * of both strings and we select the smaller sum
                     */
                    if (direction == LevDirection.UNSET) {
                        direction = if (levT1To + levT2To < levT1From + levT2From)
                            LevDirection.TO
                        else
                            LevDirection.FROM
                    }

                    /**
                     *  higher priority (-10000) for favorites
                     *  can not be done before setting lev. direction
                     */
                    if (isFavorites(t1.id)) {
                        levT1From -= 10000
                        levT1To -= 10000
                    }

                    if (isFavorites(t2.id)) {
                        levT2From -= 10000
                        levT2To -= 10000
                    }

                    return if (direction == LevDirection.FROM)
                        levT1From - levT2From
                    else
                        levT1To - levT2To
                }

                private fun isFavorites(id: String): Boolean {
                    if (favoritesIds.contains(id))
                        return true

                    return false
                }

                private fun costOfSubst(a: Char, b: Char): Int {
                    if (a == b)
                        return 0
                    return 1
                }

                /**
                 * https://www.baeldung.com/java-levenshtein-distance
                 */
                private fun levenshteinDistance(s1: String, s2: String): Int {
                    val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

                    for (i in 0..s1.length) {
                        for (j in 0..s2.length) {
                            if (i == 0) {
                                dp[i][j] = j
                            } else if (j == 0)
                                dp[i][j] = i
                            else
                                dp[i][j] = minOf(
                                    dp[i - 1][j - 1] + costOfSubst(
                                        s1[i - 1].toChar(),
                                        s2[j - 1].toChar()
                                    ),
                                    dp[i - 1][j].toInt() + 1,
                                    dp[i][j - 1].toInt() + 1
                                )
                        }
                    }

                    return dp[s1.length][s2.length]
                }
            })
        }

        if(favorites){
            result = result.filter { favoritesIds.contains(it.id) }
        }

        submitList(result)
    }
}
