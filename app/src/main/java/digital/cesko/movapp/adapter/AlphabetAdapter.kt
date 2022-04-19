package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
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
            binding.textAlphabetLetter.text = "${item.letter_capital} ${item.letter ?: ""}"
            binding.textAlphabetTranscription.text = item.transcription

            var examples = ""
            for (i in item.examples) {
                examples += "${i.example} [${i.transcription}]\n"
            }
            binding.textAlphabetExamples.text = examples.trim()

            if (item.letterSoundAssetFile != null){
                binding.imageLatterPlaySound.visibility = View.VISIBLE
                binding.layout.setOnClickListener { view ->
                    playSound(view.context, item.letterSoundAssetFile)
                }
            } else {
                binding.imageLatterPlaySound.visibility = View.GONE
                binding.layout.setOnClickListener(null)
            }

        }

    }



}

/**
 * @return null if failed
 */
fun playSound(
    context: Context,
    assetFileName: String
): MediaPlayer? {
    val afd: AssetFileDescriptor = context.assets.openFd(assetFileName)
    var player:MediaPlayer? = MediaPlayer()
    player?.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());

    player?.setOnCompletionListener { mp ->
        mp?.reset()
        mp?.release()
        player = null
    }
    player?.prepare()
    player?.start()

    return player
}