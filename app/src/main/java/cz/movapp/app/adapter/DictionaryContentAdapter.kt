package cz.movapp.app.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.LanguagePair
import cz.movapp.app.R
import cz.movapp.app.databinding.DictionaryContentItemBinding
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import java.text.Normalizer
import java.util.*

class DictionaryContentAdapter(
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : ListAdapter<DictionaryTranslationsData, DictionaryContentAdapter.ItemViewHolder>(DiffCallback) {

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

    class ItemViewHolder(binding: DictionaryContentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(DictionaryContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        if (position % 2 == 1) {
            holder.binding.layout.background = ContextCompat.getDrawable(context, R.drawable.odd_outline)
        } else {
            val typedValue = TypedValue()
            val theme: Resources.Theme = context.theme
            val got: Boolean =
                theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
            holder.binding.layout.setBackgroundColor(typedValue.data)
        }

        holder.binding.apply {
            if (langPair.isReversed) {
                textFrom.text = item.translation_to
                textTo.text = item.translation_from
                textToTrans.text = brackets(item.transcription_from)
            } else {
                textFrom.text = item.translation_from
                textTo.text = item.translation_to
                textToTrans.text = brackets(item.transcription_to)
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

        holder.binding.imagePlaySound.setOnClickListener {
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
                    context, R.color
                        .secondaryColor
                )
            )
        else
            holder.binding.imageFavorites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color
                        .primaryColor
                )
            )
    }

    fun getSelectedTranslations(translationsIds: List<String>): List<DictionaryTranslationsData> {
        return if (translationsIds.isEmpty()) {
            listOf<DictionaryTranslationsData>()
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset.filter { it.id in translationsIds }.forEach { filtered.add(it) }
            filtered
        }
    }

    private fun stripAccents(input: String): String {
        var output = Normalizer.normalize(input, Normalizer.Form.NFD)
        return output.replace(Regex("[^\\p{ASCII}]"), "")
    }

    fun search(constraint: String) {
        val searchString = stripAccents(constraint.toString().lowercase(Locale.getDefault()))
        val result = if (searchString.isEmpty()) {
            wholeDataset
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset
                .filter {
                    (stripAccents(it.translation_from).lowercase(Locale.getDefault()).contains(searchString) or
                            (stripAccents(it.translation_to).lowercase(Locale.getDefault()).contains(searchString)))
                }
                .forEach { filtered.add(it) }
            filtered
        }

        submitList(result)
    }
}