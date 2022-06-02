package cz.movapp.app.ui.about

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.divider.MaterialDivider
import cz.movapp.app.R
import cz.movapp.app.data.Section
import cz.movapp.app.databinding.FragmentAboutTeamBinding

class AboutTeamFragment : Fragment() {

    private var _binding: FragmentAboutTeamBinding? = null

    private val teamViewModel: AboutTeamViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutTeamBinding.inflate(inflater, container, false)

        for (section in teamViewModel.team.sections!!) {
            binding.linearLayout.addView(createSectionTextView(section.name))
            binding.linearLayout.addView(createSectionView(section))
        }

        return binding.root
    }

    private fun createSectionView(section: Section): CardView {
        val cardViewMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, requireContext().resources.displayMetrics)

        val cardViewLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = cardViewMargin.toInt()
            bottomMargin = cardViewMargin.toInt()
        }

        val cardView = CardView(requireContext()).apply {
            layoutParams = cardViewLayoutParams
            radius = cardViewMargin
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondarySurfaceColor))
        }

        val layoutMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, requireContext().resources.displayMetrics)

        val layoutLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            marginStart = layoutMargin.toInt()
            marginEnd = layoutMargin.toInt()
        }

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = layoutLayoutParams
        }

        for (member in section.members) {
            val memberTextView = (layoutInflater.inflate(R.layout.template_team_member, null) as TextView).apply {
                text = member.name
            }

            layout.addView(memberTextView)

            if (section.members.indexOf(member) < (section.members.size - 1))
                layout.addView(createDivider())
        }

        cardView.addView(layout)

        return cardView
    }

    private fun createSectionTextView(label: String): TextView {
        val layoutPar = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val textView = TextView(context).apply {
            text = label
            isAllCaps = true
            setTextColor(ContextCompat.getColor(context, R.color.greyTextColor))
            layoutParams = layoutPar
        }

        return textView
    }

    private fun createDivider(): MaterialDivider {
        val layoutPar = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return MaterialDivider(requireContext()).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.dividerColor))
            layoutParams = layoutPar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}