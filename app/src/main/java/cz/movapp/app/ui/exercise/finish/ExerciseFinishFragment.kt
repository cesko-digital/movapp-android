package cz.movapp.app.ui.exercise.finish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentExerciseFinishBinding
import cz.movapp.app.ui.exercise.ExerciseViewModel

class ExerciseFinishFragment : Fragment() {

    private lateinit var binding: FragmentExerciseFinishBinding

    private val exerciseViewModel: ExerciseViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseFinishBinding.inflate(inflater, container, false)

        binding.apply {
            restart.setOnClickListener {
                exerciseViewModel.restartExercise()
                findNavController().popBackStack()
            }

            setup.setOnClickListener {
                findNavController().popBackStack(R.id.nav_exercise_setup, false)
            }
        }

        return binding.root
    }
}