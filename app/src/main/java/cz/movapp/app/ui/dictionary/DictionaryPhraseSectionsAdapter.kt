package cz.movapp.app.ui.dictionary

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.R
import cz.movapp.app.data.LanguagePair

class DictionaryPhraseSectionsAdapter(
    private var dataset: List<DictionarySectionsData>,
    var onItemClicked: (DictionarySectionsData) -> Unit = {},
) : RecyclerView.Adapter<DictionaryPhraseSectionsAdapter.ItemViewHolder>() {

    var langPair = LanguagePair.getDefault()

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textFromTo: TextView = view.findViewById(R.id.text_dictionary_from_to)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutDictionaryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =  LayoutInflater.from(parent.context)
                .inflate(R.layout.dictionary_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        val context = holder.layout.context

        if (position % 2 == 1) {
            holder.layout.background = ContextCompat.getDrawable(context, R.drawable.odd_outline)
        } else {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.color.surfaceColor, typedValue, true)
            holder.layout.setBackgroundColor(typedValue.data)
        }

        if (langPair.isReversed)
            holder.textFromTo.text = "%s - %s".format(item.source, item.main)
        else
            holder.textFromTo.text = "%s - %s".format(item.main, item.source)

        holder.itemView.setOnClickListener {
            onItemClicked(item)

        }
    }
}