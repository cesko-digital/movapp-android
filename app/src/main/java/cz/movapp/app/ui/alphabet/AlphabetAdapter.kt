package cz.movapp.app.ui.alphabet

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.generateViewId
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.android.playSound
import cz.movapp.app.R
import cz.movapp.app.databinding.AlphabetItemBinding


class AlphabetAdapter(
        private val dataset: List<AlphabetData>
) : RecyclerView.Adapter<AlphabetAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: AlphabetItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = AlphabetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                binding.layoutAlphabetExamples.addView(
                    createDynamicExample(holder, example)
                )
            }
        }
    }

    private fun createDynamicExample(holder: ItemViewHolder, exampleData: LetterExampleData): RelativeLayout {
        lateinit var paramsExample: RelativeLayout.LayoutParams

        /* create relative layout for one example */
        val layout = RelativeLayout(holder.itemView.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val paddingPixels = resources.getDimension(R.dimen.items_with_play_padding).toInt()
            setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels)
        }

        /* create textview for one example */
        val textExample = TextView(holder.itemView.context).apply {
            text =  "${exampleData.example} [${exampleData.transcription}]"

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.alphabet_example_text_size)
            )

            id = generateViewId()
        }


        /* create imageview - play icon for one example */
        val imagePlayExample = ImageView(holder.itemView.context).apply {
            setImageResource(R.drawable.ic_play)
            layoutParams = ViewGroup.LayoutParams(
                resources.getDimension(R.dimen.playSize).toInt(),
                resources.getDimension(R.dimen.playSize).toInt()
            )

            id = generateViewId()

            if (exampleData.file_name == null) {
                visibility = View.GONE
            }
        }

        /* set layout params of textview */
        paramsExample = RelativeLayout.LayoutParams(textExample.layoutParams)
        paramsExample.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        paramsExample.addRule(RelativeLayout.CENTER_VERTICAL)
        paramsExample.addRule(RelativeLayout.LEFT_OF, imagePlayExample.id)
        textExample.layoutParams = paramsExample

        /* set layout params of imageview */
        paramsExample = RelativeLayout.LayoutParams(imagePlayExample.layoutParams)
        paramsExample.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        paramsExample.addRule(RelativeLayout.CENTER_VERTICAL)
        imagePlayExample.layoutParams = paramsExample

        imagePlayExample.setOnClickListener { view ->
            playSound(view.context, exampleData.file_name!!)
        }

        layout.addView(textExample)
        layout.addView(imagePlayExample)

        return layout
    }
}

