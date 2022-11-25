package cz.movapp.app.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.divider.MaterialDivider
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentAboutTeamBinding
import java.util.Locale

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

        binding.apply {
            topAboutBar.navigationIcon = AppCompatResources
                .getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)

            topAboutBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        for (section in teamViewModel.team.sections!!) {
            val sectionTemplateLayout = layoutInflater.inflate(R.layout.template_team_section, container, false)

            sectionTemplateLayout.findViewById<TextView>(R.id.text_team_section_name).text = section.sectionName.getValue(Locale.getDefault().language)

            val sectionLayout = sectionTemplateLayout.findViewById<LinearLayout>(R.id.linear_layout_section_team)

            for (member in section.members) {
                val memberTextView = (layoutInflater.inflate(R.layout.template_team_member, container, false) as TextView).apply {
                    text = member.name
                }

                sectionLayout.addView(memberTextView)

                if (section.members.indexOf(member) < (section.members.size - 1))
                    sectionLayout.addView(layoutInflater.inflate(R.layout.template_team_divider, container, false) as MaterialDivider)
            }

            binding.linearLayout.addView(sectionTemplateLayout)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}