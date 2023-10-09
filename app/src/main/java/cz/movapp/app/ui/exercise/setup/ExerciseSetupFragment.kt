package cz.movapp.app.ui.exercise.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cz.movapp.app.BuildConfig
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentExerciseSetupBinding
import cz.movapp.app.ui.dictionary.DictionaryMetaCategoryData
import cz.movapp.app.ui.dictionary.DictionaryViewModel
import cz.movapp.app.ui.exercise.ExerciseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class ExerciseSetupFragment : Fragment() {

    private lateinit var binding: FragmentExerciseSetupBinding

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val exerciseViewModel: ExerciseViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseSetupBinding.inflate(inflater, container, false)

        binding.apply {
            start.setOnClickListener {
                lifecycleScope.launch {
                    val selectedCategories = (binding.recyclerViewExercise.adapter as ExerciseSetupAdapter).selectedCategories()
                    val numExercises = binding.groupLength.checkedRadioButtonId.numExercises()
                    val error = when {
                        selectedCategories.isEmpty() -> getString(R.string.exercise_setup_error_select_categories)
                        numExercises == 0 -> getString(R.string.exercise_setup_error_select_length)
                        else -> null
                    }
                    if (error == null) {
                        binding.error.visibility = View.GONE
                        exerciseViewModel.prepareExercise(selectedCategories, numExercises)
                        exerciseViewModel.startNewExercise()
                        findNavController().navigate(R.id.to_play)
                    }
                    else {
                        binding.error.visibility = View.VISIBLE
                        binding.error.text = error
                    }
                }
            }
        }

        dictionarySharedViewModel.metaCategories.observe(viewLifecycleOwner) {
            if (BuildConfig.DEBUG) {
                Timber.d("metaCats:\n${it.joinToString("\n")}")
            }
            setupButtons(it)
        }

        return binding.root
    }

    private fun setupButtons(metaCategories: List<DictionaryMetaCategoryData>) {

        val items = metaCategories.map {
            ExerciseSetupAdapter.Item(
                it.nameSource,
                it.metaCategories
            )
        }

        binding.recyclerViewExercise.adapter = ExerciseSetupAdapter(items)
    }

    private fun Int.numExercises(): Int {
        return when (this) {
            R.id.length_1 -> 1
            R.id.length_5 -> 5
            R.id.length_10 -> 10
            else -> 0
        }
    }
}
