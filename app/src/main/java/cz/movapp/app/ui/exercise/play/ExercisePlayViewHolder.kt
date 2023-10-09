package cz.movapp.app.ui.exercise.play

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.databinding.ExerciseAudioItemBinding
import cz.movapp.app.databinding.ExercisePlayItemBinding

sealed class ViewHolder<out ITEM : ExercisePlayItem>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: @UnsafeVariance ITEM, position: Int)
}

// TODO duplicate code for answerState

class TextViewHolder(
    val onItemClick: (ExercisePlayItem, Int) -> AnswerState?,
    val binding: ExercisePlayItemBinding
) : ViewHolder<TextExercisePlayItem>(binding.root) {

    override fun bind(item: TextExercisePlayItem, position: Int) {
        binding.apply {
            textExerciseName.text = item.name
            when (item.answerState) {
                AnswerState.UNANSWERED -> {
                    root.isSelected = false
                    root.isActivated = false
                }

                AnswerState.WRONG -> {
                    root.isSelected = true
                    root.isActivated = false
                }

                AnswerState.CORRECT -> {
                    root.isActivated = true
                }
            }
            root.setOnClickListener {
                onItemClick(item, position)?.let {
                    item.answerState = it
                }
            }
        }
    }
}

class AudioViewHolder(
    val onPlayNormalClick: (ExercisePlayItem) -> Unit,
    val onPlaySlowClick: (ExercisePlayItem) -> Unit,
    val onCheckClick: (ExercisePlayItem, Int) -> AnswerState?,
    val binding: ExerciseAudioItemBinding
) : ViewHolder<AudioExercisePlayItem>(binding.root) {

    override fun bind(item: AudioExercisePlayItem, position: Int) {
        binding.apply {
            playNormal.setOnClickListener {
                onPlayNormalClick(item)
            }

            playSlow.setOnClickListener {
                onPlaySlowClick(item)
            }

            check.setOnClickListener {
                onCheckClick(item, position)?.let {
                    item.answerState = it
                }
            }

            when (item.answerState) {
                AnswerState.UNANSWERED -> {
                    check.isSelected = false
                    check.isActivated = false
                }

                AnswerState.WRONG -> {
                    check.isSelected = true
                    check.isActivated = false
                }

                AnswerState.CORRECT -> {
                    check.isActivated = true
                }
            }
        }
    }
}