package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.R
import digital.cesko.movapp.data.Favorites
import digital.cesko.movapp.ui.dictionary.DictionarySectionsData
import digital.cesko.movapp.ui.dictionary.DictionaryFragmentDirections

class DictionaryAdapter (
    private val context: Context,
    private val dataset: List<DictionarySectionsData>
    ): RecyclerView.Adapter<DictionaryAdapter.ItemViewHolder>() {

    var fromUa = true
    var favorites = listOf<Favorites>()

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textFromTo: TextView = view.findViewById(R.id.text_dictionary_from_to)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutDictionaryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dictionary_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        if (position % 2 == 1) {
            //holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.oddItem))
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.odd_outline)
        } else {
            val typedValue = TypedValue()
            val theme: Resources.Theme = context.theme
            val got: Boolean = theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
            holder.layout.setBackgroundColor(typedValue.data)
        }

        if (fromUa)
            holder.textFromTo.text = "%s - %s".format(item.to, item.from)
        else
            holder.textFromTo.text = "%s - %s".format(item.from, item.to)

        holder.itemView.setOnClickListener {
            val favoritesIds = mutableListOf<String>()
            favorites.forEach {
                if (item.translation_ids.contains(it.translationId))
                    favoritesIds.add(it.translationId)

            }
            val action = DictionaryFragmentDirections.actionNavigationDictionaryToDictionaryContentFragment(constraint = item.id, translationIds = item.translation_ids.toTypedArray(), favoritesIds = favoritesIds.toTypedArray())
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun getSectionTitle(sectionId: String): String {
        for (i in dataset) {
            if (sectionId == i.id) {
                return when (fromUa) {
                    true -> i.to
                    false -> i.from
                }
            }
        }
        return ""
    }
}