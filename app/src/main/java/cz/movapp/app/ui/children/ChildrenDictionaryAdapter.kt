package cz.movapp.app.ui.children

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.android.playSound
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.ChildrenItemBinding
import java.io.IOException

class ChildrenDictionaryAdapter (
    private val dataset: List<ChildrenDictionaryData>
): RecyclerView.Adapter<ChildrenDictionaryAdapter.ItemViewHolder>() {

    var langPair = LanguagePair.getDefault()

    class ItemViewHolder(val binding: ChildrenItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ChildrenItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        val context = holder.binding.root.context

        holder.binding.apply {
            try {
                val imageStream = context.assets.open(item.image_path)
                imageChildrenMain.setImageDrawable(Drawable.createFromStream(imageStream, null))
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                imageChildrenMain.setImageDrawable(null)
            }

            imageChildrenFlagFrom.setImageResource(langPair.from.flagResId)
            imageChildrenFlagTo.setImageResource(langPair.to.flagResId)

            if (!langPair.isReversed) {
                textChildrenFrom.text = formatTrans(item.main_translation, item.main_transcription)
                textChildrenTo.text = formatTrans(item.source_translation, item.source_transcription)

                imagePlaySoundFrom.setOnClickListener {
                    playSound(holder.itemView.context, item.main_sound_local)
                }
                imagePlaySoundTo.setOnClickListener {
                    playSound(holder.itemView.context, item.source_sound_local)
                }
            } else {
                textChildrenFrom.text = formatTrans(item.source_translation, item.source_transcription)
                textChildrenTo.text = formatTrans(item.main_translation, item.main_transcription)

                imagePlaySoundFrom.setOnClickListener {
                    playSound(holder.itemView.context, item.source_sound_local)
                }
                imagePlaySoundTo.setOnClickListener {
                    playSound(holder.itemView.context, item.main_sound_local)
                }
            }
        }
    }

    private fun formatTrans(translation: String, transcription: String) : String {
        return "$translation [${transcription}]"
    }
}