package cz.movapp.app.ui.children

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.movapp.app.R
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FairyTaleColumnBinding

enum class EmphasizerEvaluation {
    LOWER,
    EQUAL,
    GREATER
}

class ChildrenFairyTalePlayerAdapter (
    private val dataset: FairyTale,
    private val isReversed: Boolean,
    var onItemClicked: (String) -> Unit = {},
): RecyclerView.Adapter<ChildrenFairyTalePlayerAdapter.ItemViewHolder>() {
    var langPair = LanguagePair.getDefault()
    private var emphesizer : ((Int) -> EmphasizerEvaluation)? = null
    private var columnCallback : ((Int) -> Unit)? = null

    class ItemViewHolder(val binding: FairyTaleColumnBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(FairyTaleColumnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.sections!!.size
    }

    fun setEmphasizer(emp: (Int)-> EmphasizerEvaluation) {
        emphesizer = emp
    }

    fun setColumnCallback(c: (Int)-> Unit) {
        columnCallback = c
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset.sections!![position]
        val context = holder.binding.root.context

        holder.binding.apply {
            fairyTaleColumnText.text = item.getValue(
                if (isReversed) { langPair.to.langCode } else { langPair.from.langCode }
            )?.text ?: ""

            fairyTaleColumnText.setTextColor(
                ContextCompat.getColor(
                    context, R.color.playerUnRead
                )
            )

            fairyTaleColumnText.setOnClickListener {
                if (columnCallback != null) {
                    columnCallback!!(position)
                }
            }

            if (emphesizer != null) {
                val textColor = when (emphesizer!!(position)) {
                    EmphasizerEvaluation.LOWER -> R.color.playerRead
                    EmphasizerEvaluation.EQUAL -> R.color.playerReading
                    EmphasizerEvaluation.GREATER -> R.color.playerUnRead
                }

                fairyTaleColumnText.setTextColor(
                    ContextCompat.getColor(
                        context, textColor
                    )
                )

            }
        }
    }
}