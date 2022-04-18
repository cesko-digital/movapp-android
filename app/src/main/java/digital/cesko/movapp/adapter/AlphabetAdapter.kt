package digital.cesko.movapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.databinding.AlphabetItemBinding
import digital.cesko.movapp.ui.alphabet.AlphabetData

class AlphabetAdapter(
        private val dataset: List<AlphabetData>
) : RecyclerView.Adapter<AlphabetAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: AlphabetItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlphabetAdapter.ItemViewHolder {
        val binding = AlphabetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: AlphabetAdapter.ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.apply {
            binding.textAlphabetLetter.text = "%s %s".format(item.letter_capital,
                    when (item.letter) {
                        "null" -> ""
                        else -> item.letter
                    })
            binding.textAlphabetTranscription.text = "%s".format(item.transcription)

            var examples = ""
            for (i in item.examples) {
                examples += "%s [%s]\n".format(i.example, i.transcription)
            }
            binding.textAlphabetExamples.text = examples.trim()

        }

    }
}