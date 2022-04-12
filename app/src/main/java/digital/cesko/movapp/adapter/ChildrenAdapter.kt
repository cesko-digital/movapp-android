package digital.cesko.movapp.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import digital.cesko.movapp.MainActivity
import digital.cesko.movapp.R
import digital.cesko.movapp.ui.children.ChildrenData

class ChildrenAdapter (
    private val context: Context,
    private val dataset: List<ChildrenData>
): RecyclerView.Adapter<ChildrenAdapter.ItemViewHolder>() {

    var fromUa = true

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val imageChildren: ImageView = view.findViewById(R.id.image_children_main)
        val textFrom: TextView = view.findViewById(R.id.text_children_from)
        val textTo: TextView = view.findViewById(R.id.text_children_to)

        val imageFlagFrom: ImageView = view.findViewById(R.id.image_children_flag_from)
        val imageFlagTo: ImageView = view.findViewById(R.id.image_children_flag_to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenAdapter.ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.children_item, parent, false)
        return ChildrenAdapter.ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ChildrenAdapter.ItemViewHolder, position: Int) {
        val item = dataset[position]
        var strFrom = ""
        var strTo = ""

        var flagFrom: Int
        var flagTo: Int

        /* for dark theme, use white color as tint */
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                holder.imageChildren.imageTintList =  ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            }
            else -> {
                holder.imageChildren.imageTintList =  ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
            }
        }

        holder.imageChildren.setImageDrawable(item.image)

        when(fromUa) {
            true -> {
                strFrom = "%s [%s]".format(item.translation_from, item.transcription_from)
                strTo = "%s [%s]".format(item.translation_to, item.transcription_to)

                flagFrom = R.drawable.ua
                flagTo = R.drawable.cz
            }

            false -> {
                strTo = "%s [%s]".format(item.translation_from, item.transcription_from)
                strFrom = "%s [%s]".format(item.translation_to, item.transcription_to)

                flagTo = R.drawable.ua
                flagFrom = R.drawable.cz
            }
        }

        holder.textFrom.text = strFrom
        holder.textTo.text = strTo
        holder.imageFlagFrom.setImageResource(flagFrom)
        holder.imageFlagTo.setImageResource(flagTo)
    }
}