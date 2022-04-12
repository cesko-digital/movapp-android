package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.MainActivity
import digital.cesko.movapp.R
import digital.cesko.movapp.ui.dictionary.DictionaryTranslationsData

class DictionaryContentAdapter (
    private val context: Context,
    private val wholeDataset: List<DictionaryTranslationsData>
    ): RecyclerView.Adapter<DictionaryContentAdapter.ItemViewHolder>(), Filterable {

    private var dataset = wholeDataset
    private var _translationsIds: List<String> = listOf()

    var fromUa = true

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textFrom: TextView = view.findViewById(R.id.text_dictionary_from)
        val textTo: TextView = view.findViewById(R.id.text_dictionary_to)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutDictionaryContentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dictionary_content_item, parent, false)
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
        holder.textFrom.setOnClickListener {

        }
    }

    fun setSelectedTranslationIds(translations: List<String>) {
        _translationsIds = translations
        filter.filter("")
    }

    override fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val searchString = constraint.toString().lowercase()
                val filteredDataset = if (searchString.isEmpty())
                    wholeDataset
                else {
                    val filtered = mutableListOf<DictionaryTranslationsData>()
                    wholeDataset
                        .filter {
                            (it.id in _translationsIds) or (it.translation_from.lowercase().contains(searchString) or (it.translation_to.lowercase().contains(searchString)))
                        }
                        .forEach { filtered.add(it) }
                    filtered
                }
                return FilterResults().apply { values = filteredDataset }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataset = if (results?.values == null)
                    listOf()
                else {
                    if (fromUa)
                            (results.values as List<DictionaryTranslationsData>).sortedBy { it.translation_from.lowercase() }
                    else
                        (results.values as List<DictionaryTranslationsData>).sortedBy { it.translation_to.lowercase() }
                }

                notifyDataSetChanged()
            }
        }
    }
}