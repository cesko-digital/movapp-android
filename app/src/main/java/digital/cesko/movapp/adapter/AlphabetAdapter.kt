package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.databinding.AlphabetItemBinding
import digital.cesko.movapp.ui.alphabet.AlphabetData
import java.io.File


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
                        null -> ""
                        else -> item.letter
                    })
            binding.textAlphabetTranscription.text = "%s".format(item.transcription)

            var examples = ""
            for (i in item.examples) {
                examples += "%s [%s]\n".format(i.example, i.transcription)
            }
            binding.textAlphabetExamples.text = examples.trim()

            item.letterSoundAssetFile?.let {
                binding.imageLatterPlaySound.setOnClickListener { view ->
                    playSound2(view.context, item.letterSoundAssetFile)
                }
            }

        }

    }



}

/**
 * @return null if failed
 */
fun playSound2(
    context: Context,
    assetFileName: String
): MediaPlayer? {
    val uri = assetFileNameToUri(assetFileName)
    uri?.let{
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

    return null
}

/**
 * @return null if failed
 */
fun playSound(
    context: Context,
    assetFileName: String
): MediaPlayer? {
    val uri = assetFileNameToUri(assetFileName)
    uri?.let{
        var player = MediaPlayer.create(context, uri)
        player?.setOnCompletionListener { mp ->
            mp?.reset()
            mp?.release()
            player = null
        }
        player?.start()

        return player
    }

    return null
}

/**
 * @param assetFileName - path in asset folder
 */
fun assetFileNameToUri(assetFileName: String?): Uri? {
    return Uri.fromFile(File("//android_asset/$assetFileName"))
}
