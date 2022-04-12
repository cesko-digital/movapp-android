package digital.cesko.movapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.R
import digital.cesko.movapp.ui.alphabet.AlphabetData

class AlphabetAdapter (
    private val context: Context,
    private val dataset: List<AlphabetData>
): RecyclerView.Adapter<AlphabetAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textLetter: TextView = view.findViewById(R.id.text_alphabet_letter)
        val textTranscription: TextView = view.findViewById(R.id.text_alphabet_transcription)
        val textExamples: TextView = view.findViewById(R.id.text_alphabet_examples)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlphabetAdapter.ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.alphabet_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: AlphabetAdapter.ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.textLetter.text = "%s%s".format(item.letter_capital,
            when (item.letter) {
                "null" -> ""
                else -> item.letter
            })
        holder.textTranscription.text = "%s".format(item.transcription)

        var examples = ""
        for (i in item.examples) {
            examples += "%s [%s]\n".format(i.example, i.transcription)
        }
        holder.textExamples.text = examples
    }
}