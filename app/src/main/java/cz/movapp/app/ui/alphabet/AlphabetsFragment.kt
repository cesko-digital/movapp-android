package cz.movapp.app.ui.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import cz.movapp.android.hideKeyboard
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentAlphabetsBinding

class AlphabetsFragment : Fragment() {

    private var _binding: FragmentAlphabetsBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlphabetsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.pager.adapter = AlphabetFragmentAdapter(this)

        TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
            if (mainSharedViewModel.selectedLanguage.value!!.isReversed) {
                when (position) {
                    0 -> tab.setText(R.string.alphabet_czech)
                    1 -> tab.setText(R.string.alphabet_ukrainian)
                }
            } else {
                when (position) {
                    0 -> tab.setText(R.string.alphabet_ukrainian)
                    1 -> tab.setText(R.string.alphabet_czech)
                }
            }
        }.attach()

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