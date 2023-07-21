package cz.movapp.app.ui.exercise.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentExercisePlayBinding
import cz.movapp.app.ui.exercise.ExerciseState
import cz.movapp.app.ui.exercise.ExerciseType
import cz.movapp.app.ui.exercise.ExerciseViewModel

class ExercisePlayFragment : Fragment() {

    private lateinit var binding: FragmentExercisePlayBinding
    private lateinit var adapter: ExercisePlayAdapter

    private val exerciseViewModel: ExerciseViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exerciseViewModel.exerciseState.observe(this) { exerciseState ->
            when (exerciseState) {
                is ExerciseState.Exercise -> {
                    binding.next.visibility = View.GONE
                    val question = exerciseState.question
                    binding.word.text = question.phrases[question.phraseCorrectIndex].source_translation

                    val phrases = question.phrases.map {
                        when (exerciseState.exerciseType) {
                            ExerciseType.TEXT_IDENTIFICATION -> TextExercisePlayItem(
                                it.id,
                                it.main_translation
                            )
                            ExerciseType.AUDIO_IDENTIFICATION -> AudioExercisePlayItem(
                                it.id,
                                it.main_translation
                            )
                        }
                    }
                    adapter.setData(phrases)
                }
                is ExerciseState.Complete -> {
                    binding.next.visibility = View.VISIBLE
                }
                is ExerciseState.End -> {
                    findNavController().navigate(R.id.nav_exercise_finish)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExercisePlayBinding.inflate(inflater, container, false)

        binding.apply {

            adapter = ExercisePlayAdapter(
                onAnswerSelected = { answer, position ->
                    exerciseViewModel.playLocalSound(requireContext(), answer.id)
                    val ret = exerciseViewModel.onAnswerSelected(answer)
                    adapter.notifyItemChanged(position)
                    ret
                },
                onPlayNormal = { answer ->
                    exerciseViewModel.playLocalSound(requireContext(), answer.id)

                },
                onPlaySlow = { answer ->
                    exerciseViewModel.playLocalSound(requireContext(), answer.id, 0.75f)
                }
            )
            recyclerViewExercise.adapter = adapter

            home.setOnClickListener {
                findNavController().popBackStack()
            }

            next.setOnClickListener {
                exerciseViewModel.onNextButtonClick()
            }
        }

        return binding.root
    }
}