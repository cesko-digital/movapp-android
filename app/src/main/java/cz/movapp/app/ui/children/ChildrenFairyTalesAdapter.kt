package cz.movapp.app.ui.children

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.R
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.MetaFairyTale
import cz.movapp.app.databinding.FairyTaleItemBinding
import java.io.IOException

class ChildrenFairyTalesAdapter(
    private val dataset: List<Pair<MetaFairyTale, FairyTale>>,
    var onItemClicked: (String) -> Unit = {},
): RecyclerView.Adapter<ChildrenFairyTalesAdapter.ItemViewHolder>() {
    var langPair = LanguagePair.getDefault()
    var supportedDataset: List<Pair<MetaFairyTale, FairyTale>> = dataset
    class ItemViewHolder(val binding: FairyTaleItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(FairyTaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun setSupportedDataset() {
        supportedDataset = dataset.filter {
            it.first.supportedLanguages.contains(langPair.from.langCode) &&
            it.first.supportedLanguages.contains(langPair.to.langCode)
        }
    }

    override fun getItemCount(): Int {
        return supportedDataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = supportedDataset[position]
        val context = holder.binding.root.context

        holder.binding.apply {
            try {
                val imageStream = context.assets.open("images/android/${item.first.slug}/${item.first.slug}.webp")
                fairyTaleImage.setImageDrawable(Drawable.createFromStream(imageStream, null))
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                fairyTaleImage.setImageDrawable(null)
            }

            fairyTaleNameFrom.text = item.first.title.getValue(langPair.to.langCode)
            fairyTaleNameTo.text = item.first.title.getValue(langPair.from.langCode)
            fairyTaleTime.text = "${item.first.duration} ${context.resources.getString(R.string.fairy_tales_minutes)}"

            fairyTaleOrigin.setImageResource(
                when(item.first.origin) {
                    "UA" -> R.drawable.ua
                    "CZ" -> R.drawable.cz
                    "SK" -> R.drawable.sk
                    else -> R.drawable.ua
                }
            )
        }

        holder.itemView.setOnClickListener{
            onItemClicked(item.first.slug)
        }
    }
}