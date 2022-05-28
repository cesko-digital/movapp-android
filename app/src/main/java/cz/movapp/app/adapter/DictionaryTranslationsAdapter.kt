package cz.movapp.app.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.android.playSound
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.R
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.DictionaryTranslationItemBinding
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData

open class DictionaryTranslationsAdapter(
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
) : ListAdapter<DictionaryTranslationsData, DictionaryTranslationsAdapter.ItemViewHolder>(
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

    class ItemViewHolder(val binding: DictionaryTranslationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

        bindDataToView(item, holder.binding)
    }

    fun bindDataToView(
        item: DictionaryTranslationsData,
        binding: DictionaryTranslationItemBinding
    ) {
        val context = binding.root.context

        binding.apply {
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

        setFavoriteStar(context, binding, favoritesIds.contains(item.id))

        binding.imageFavorites.setOnClickListener {
            if (favoritesIds.contains(item.id)) {
                favoritesIds.remove(item.id)
                favoritesViewModel.removeFavorite(item.id)
                setFavoriteStar(context, binding, false)
            } else {
                favoritesIds.add(item.id)
                favoritesViewModel.addFavorites(item.id)
                setFavoriteStar(context, binding, true)
            }
        }

        binding.imagePlaySoundFrom.setOnClickListener {
            if (langPair.isReversed)
                playSound(context, item.source_sound_local)
            else
                playSound(context, item.main_sound_local)
        }

        binding.imagePlaySoundTo.setOnClickListener {
            if (langPair.isReversed)
                playSound(context, item.main_sound_local)
            else
                playSound(context, item.source_sound_local)
        }
    }



    fun brackets(s: String): CharSequence? {
        return "[${s}]"
    }

    val favouritesIconSet by lazy { AppCompatResources.getDrawable(context, R.drawable.ic_baseline_star_24) }
    val favouritesIconNotSet by lazy { AppCompatResources.getDrawable(context, R.drawable.ic_baseline_star_outline_24) }

    fun setFavoriteStar(
        context: Context,
        binding: DictionaryTranslationItemBinding,
        isSet: Boolean
    ) {
        if (isSet) {
            binding.imageFavorites.setImageDrawable(favouritesIconSet)
            binding.imageFavorites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color.secondaryColor
                )
            )
        } else {
            binding.imageFavorites.setImageDrawable(favouritesIconNotSet)
            binding.imageFavorites.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, R.color.primaryTextColor
                )
            )
        }
    }

    fun selectTranslations(translationsIds: List<String>) {
        submitList(if (translationsIds.isEmpty()) {
            listOf()
        } else {
            wholeDataset.filter { it.id in translationsIds }
        })
    }
}
