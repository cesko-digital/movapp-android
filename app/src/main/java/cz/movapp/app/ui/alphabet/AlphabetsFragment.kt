package cz.movapp.app.ui.alphabet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.data.Language
import cz.movapp.app.databinding.FragmentAlphabetsBinding

class AlphabetsFragment : Fragment() {

    private var _binding: FragmentAlphabetsBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    private val langCaptionsArray = mapOf(
        Language.Ukrainian.langCode to R.string.alphabet_ukrainian,
        Language.Czech.langCode to R.string.alphabet_czech,
        Language.Slovak.langCode to R.string.alphabet_slovak,
        Language.Polish.langCode to R.string.alphabet_polish,
    )

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

        val langPair = mainSharedViewModel.selectedLanguage.value!!

        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            when (position) {
                0 -> tab.setText(langCaptionsArray[langPair.to.langCode]!!)
                1 -> tab.setText(langCaptionsArray[langPair.from.langCode]!!)
            }
        }.attach()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}