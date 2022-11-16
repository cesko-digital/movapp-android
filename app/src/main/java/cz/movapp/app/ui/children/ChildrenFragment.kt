package cz.movapp.app.ui.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentChildrenBinding

class ChildrenFragment : Fragment() {

    private var _binding: FragmentChildrenBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

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
                2 -> tab.setText(R.string.fairy_tales)
            }
        }.attach()

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            val adapter = binding.pager.adapter as ChildrenFragmentAdapter
            adapter.langPair = it
            adapter.notifyDataSetChanged()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}