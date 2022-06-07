package cz.movapp.app.ui.alphabet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.android.playSound
import cz.movapp.app.R
import cz.movapp.app.databinding.AlphabetItemBinding


class AlphabetAdapter(
        private val dataset: List<AlphabetData>
) : RecyclerView.Adapter<AlphabetAdapter.ItemViewHolder>() {

    var inflater: LayoutInflater? = null

    class ItemViewHolder(val binding: AlphabetItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        inflater = LayoutInflater.from(parent.context)
        val binding = AlphabetItemBinding.inflate(inflater!!, parent, false)

        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.apply {
            binding.textAlphabetLetter.text = "${item.letter_capital} ${item.letter ?: ""}"
            binding.textAlphabetTranscription.text = item.transcription

            if (item.letterSoundAssetFile != null){
                binding.imageLatterPlaySound.visibility = View.VISIBLE
                binding.imageLatterPlaySound.setOnClickListener { view ->
                    playSound(view.context, item.letterSoundAssetFile)
                }
            } else {
                binding.imageLatterPlaySound.visibility = View.GONE
                binding.imageLatterPlaySound.setOnClickListener(null)
            }

            binding.layoutAlphabetExamples.removeAllViewsInLayout()

            for (example in item.examples.listIterator()) {
                val exampleLayout = inflater!!.inflate(R.layout.template_alphabet_example, binding.layoutAlphabetExamples, false)

                exampleLayout.findViewById<TextView>(R.id.text_view_alphabet_example).apply {
                    text = "${example.example} [${example.transcription}]"
                }

                exampleLayout.findViewById<ImageView>(R.id.image_play_sound_alphabet_example).apply {
                    if (example.file_name == null) {
                        visibility = View.GONE
                    } else {
                        setOnClickListener { view ->
                            playSound(view.context, example.file_name!!)
                        }
                    }
                }

                binding.layoutAlphabetExamples.addView(exampleLayout)
            }
        }
    }
}

