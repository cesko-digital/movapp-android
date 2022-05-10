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

class DictionaryFavoritesAdapter(
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : ListAdapter<DictionaryTranslationsData, DictionaryFavoritesAdapter.ItemViewHolder>(
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
                textFrom.text = item.translation_to
                textFromTrans.text = brackets(item.transcription_to)
                textTo.text = item.translation_from
                textToTrans.text = brackets(item.transcription_from)
            } else {
                textFrom.text = item.translation_from
                textFromTrans.text = brackets(item.transcription_from)
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

    fun selectTranslations(translationsIds: List<String>) {
        val result = if (translationsIds.isEmpty()) {
            listOf<DictionaryTranslationsData>()
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset.filter { it.id in translationsIds }.forEach { filtered.add(it) }
            filtered
        }

        submitList(result)
    }
}