package cz.movapp.app.ui.exercise.setup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.databinding.ExerciseSetupItemBinding


class ExerciseSetupAdapter(
    private val dataSet: List<Item>
) : RecyclerView.Adapter<ExerciseSetupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExerciseSetupItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]
        viewHolder.binding.apply {
            textExerciseName.text = item.name
            root.isSelected = item.selected
            root.setOnClickListener {
                item.selected = !item.selected
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = dataSet.size

    fun selectedCategories(): MutableList<String> {
        val selectedCats = mutableListOf<String>()
        dataSet.forEach {
            if (it.selected) {
                selectedCats.addAll(it.metaCategories)
            }
        }
        return selectedCats
    }

    class ViewHolder(
        val binding: ExerciseSetupItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    data class Item(
        val name: String,
        val metaCategories: List<String>,
        var selected: Boolean = false
    )
}