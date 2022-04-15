package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.R
import digital.cesko.movapp.ui.dictionary.DictionaryTranslationsData
import digital.cesko.movapp.FavoritesViewModel
import java.text.Normalizer
import java.util.*

class DictionaryContentAdapter (
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>,
    private val favoritesViewModel: FavoritesViewModel,
    ): ListAdapter<DictionaryTranslationsData, DictionaryContentAdapter.ItemViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DictionaryTranslationsData>() {
            override fun areItemsTheSame(oldItem: DictionaryTranslationsData, newItem: DictionaryTranslationsData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DictionaryTranslationsData, newItem: DictionaryTranslationsData): Boolean {
                return oldItem == newItem
            }
        }
    }

    var fromUa = true
    var favoritesIds = mutableListOf<String>()

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textFrom: TextView = view.findViewById(R.id.text_dictionary_from)
        val textTo: TextView = view.findViewById(R.id.text_dictionary_to)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutDictionaryContentItem)
        val imageFavorites: ImageView = view.findViewById(R.id.image_favorites)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dictionary_content_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)

        if (position % 2 == 1) {
            //holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.oddItem))
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.odd_outline)
        } else {
            val typedValue = TypedValue()
            val theme: Resources.Theme = context.theme
            val got: Boolean = theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
            holder.layout.setBackgroundColor(typedValue.data)
        }

        holder.textFrom.text = "%s\n[%s]".format(
            when(fromUa) {
                true -> item.translation_to
                false -> item.translation_from
            },
            when(fromUa) {
                true -> item.transcription_to
                false -> item.transcription_from
            }
        )
        holder.textFrom.setOnClickListener {

        }

        holder.textTo.text = "%s\n[%s]".format(
            when(fromUa) {
                false -> item.translation_to
                true -> item.translation_from
            },
            when(fromUa) {
                false -> item.transcription_to
                true -> item.transcription_from
            }
        )
        holder.textTo.setOnClickListener {

        }

        setFavoriteStar(holder, favoritesIds.contains(item.id))

        holder.imageFavorites.setOnClickListener {
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
    }

    private fun setFavoriteStar(holder: ItemViewHolder, isSet: Boolean) {
        if (isSet) {
            holder.imageFavorites.setImageResource(android.R.drawable.star_big_on)
            holder.imageFavorites.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondaryColor))
        } else {
            holder.imageFavorites.setImageResource(android.R.drawable.star_big_off)
            holder.imageFavorites.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primaryColor))
        }
    }

    fun getSelectedTranslations(translationsIds: List<String>): List<DictionaryTranslationsData> {
        return if (translationsIds.isEmpty()) {
            wholeDataset
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
        val result =  if (searchString.isEmpty()) {
            wholeDataset
        } else {
            val filtered = mutableListOf<DictionaryTranslationsData>()
            wholeDataset
                .filter { (stripAccents(it.translation_from).lowercase(Locale.getDefault()).contains(searchString) or
                        (stripAccents(it.translation_to).lowercase(Locale.getDefault()).contains(searchString))) }
                .forEach { filtered.add(it) }
            filtered
        }

        submitList(result)
    }
}