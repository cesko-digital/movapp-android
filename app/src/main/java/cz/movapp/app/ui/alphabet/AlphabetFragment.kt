package cz.movapp.app.ui.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.adapter.ChildrenAdapter
import cz.movapp.app.databinding.FragmentAlphabetBinding

class AlphabetFragment : Fragment() {

    private var _binding: FragmentAlphabetBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlphabetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            if (mainSharedViewModel.selectedLanguage.value!!.isReversed) {
                binding.tab.getTabAt(0)?.setText(R.string.alphabet_ukrainian)
                binding.tab.getTabAt(1)?.setText(R.string.alphabet_czech)
            } else {
                binding.tab.getTabAt(0)?.setText(R.string.alphabet_czech)
                binding.tab.getTabAt(1)?.setText(R.string.alphabet_ukrainian)
            }
        }

        binding.tab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Do not Implement
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    selectTab(tab)
                }

                private fun selectTab(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            if (childFragmentManager.findFragmentByTag("FROM") == null)
                                childFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_view, AlphabetFromTabFragment(), "FROM")
                                        .commit()
                        }
                        1 -> {
                            if (childFragmentManager.findFragmentByTag("TO") == null)
                                childFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container_view, AlphabetToTabFragment(), "TO")
                                        .commit()
                        }
                    }
                }
            }
        )

        binding.tab.getTabAt(0)?.select()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}