package cz.movapp.app.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.R
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import java.util.*

class DictionarySearchAdapter(
    private var wholeDataset: List<DictionaryTranslationsData>
) : RecyclerView.Adapter<DictionarySearchAdapter.ItemViewHolder>() {

    var langPair = LanguagePair.getDefault()
    var favoritesIds = mutableListOf<String>()

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFromTo: TextView = view.findViewById(R.id.text_dictionary_from_to)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutDictionaryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.dictionary_translation_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return wholeDataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = wholeDataset[position]

        val context = holder.layout.context

        if (position % 2 == 1) {
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.odd_outline)
        } else {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.color.surfaceColor, typedValue, true)
            holder.layout.setBackgroundColor(typedValue.data)
        }

        if (langPair.isReversed)
            holder.textFromTo.text = "%s - %s".format(item.stripped_to, item.stripped_from)
        else
            holder.textFromTo.text = "%s - %s".format(item.stripped_from, item.stripped_to)
    }

    fun search(constraint: String, favorites: Boolean = false) {
        val searchString = constraint.lowercase(Locale.getDefault())
        var result = if (searchString.isEmpty()) {
            wholeDataset
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset
                .filter {
                    it.stripped_from.contains(searchString) or
                            it.stripped_to.contains(searchString)
                }
                .forEach { filtered.add(it) }
            filtered.sortedWith(object : Comparator<DictionaryTranslationsData> {

                var direction: DictionaryTranslationsAdapter.LevDirection =
                    DictionaryTranslationsAdapter.LevDirection.UNSET

                override fun compare(
                    t1: DictionaryTranslationsData,
                    t2: DictionaryTranslationsData
                ): Int {

                    var levT1From = levenshteinDistance(constraint, t1.stripped_from)
                    var levT2From = levenshteinDistance(constraint, t2.stripped_from)

                    var levT1To = levenshteinDistance(constraint, t1.stripped_to)
                    var levT2To = levenshteinDistance(constraint, t2.stripped_to)

                    /**
                     * detect Levenshtein direction
                     * Levenshtein can not be used on both direction at the same time
                     * (because it says A > B and B > C but C < A)
                     *
                     * in order to detect direction we simply look at sum of lev. dist.
                     * of both strings and we select the smaller sum
                     */
                    if (direction == DictionaryTranslationsAdapter.LevDirection.UNSET) {
                        direction = if (levT1To + levT2To < levT1From + levT2From)
                            DictionaryTranslationsAdapter.LevDirection.TO
                        else
                            DictionaryTranslationsAdapter.LevDirection.FROM
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

                    return if (direction == DictionaryTranslationsAdapter.LevDirection.FROM)
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

        wholeDataset = result
        notifyDataSetChanged()
    }
}