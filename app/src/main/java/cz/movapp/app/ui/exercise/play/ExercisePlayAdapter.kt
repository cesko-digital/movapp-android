package cz.movapp.app.ui.exercise.play

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.databinding.ExerciseAudioItemBinding
import cz.movapp.app.databinding.ExercisePlayItemBinding


class ExercisePlayAdapter(
    private val onAnswerSelected: (ExercisePlayItem, Int) -> AnswerState?,
    private val onPlayNormal: (ExercisePlayItem) -> Unit,
    private val onPlaySlow: (ExercisePlayItem) -> Unit,
) : RecyclerView.Adapter<ViewHolder<ExercisePlayItem>>() {

    companion object {
        private const val VIEW_TYPE_TEXT = 1
        private const val VIEW_TYPE_AUDIO = 2
    }

    private val dataSet: MutableList<ExercisePlayItem> = mutableListOf()

    fun setData(data: List<ExercisePlayItem>) {
        dataSet.clear()
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder<ExercisePlayItem> {
        return when (viewType) {
            VIEW_TYPE_TEXT -> TextViewHolder(
                onAnswerSelected,
                ExercisePlayItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )

            VIEW_TYPE_AUDIO -> AudioViewHolder(
                onPlayNormal,
                onPlaySlow,
                onAnswerSelected,
                ExerciseAudioItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )

            else -> throw IllegalStateException("unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<ExercisePlayItem>, position: Int) {
        val item = dataSet[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = dataSet.size

    override fun getItemViewType(position: Int) = when (dataSet[position]) {
        is TextExercisePlayItem -> VIEW_TYPE_TEXT
        is AudioExercisePlayItem -> VIEW_TYPE_AUDIO
    }
}