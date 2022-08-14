package cz.movapp.app.ui.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentChildrenBinding

class ChildrenFragment : Fragment() {

    private var _binding: FragmentChildrenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildrenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.pager.adapter = ChildrenFragmentAdapter(this)

        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.setText(R.string.title_dictionary)
                1 -> tab.setText(R.string.memory_game)
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}