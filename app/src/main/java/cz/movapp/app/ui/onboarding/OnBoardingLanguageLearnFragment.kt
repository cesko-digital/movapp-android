package cz.movapp.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentOnBoardingLanguageLearnBinding

class OnBoardingLanguageLearnFragment : Fragment() {
    private var _binding: FragmentOnBoardingLanguageLearnBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingLanguageLearnBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.imageFlagCz.setOnClickListener {
            goToInfo(LanguagePair.UkToCs)
        }

        binding.imageFlagSk.setOnClickListener {
            goToInfo(LanguagePair.UkToSk)
        }

        binding.imageFlagPl.setOnClickListener {
            goToInfo(LanguagePair.UkToPl)
        }

        return root
    }

    private fun goToInfo(langPair: LanguagePair) {
        mainSharedViewModel.selectNativeLanguage(langPair.from)
        mainSharedViewModel.selectLanguage(langPair)
        mainSharedViewModel.storeLanguage()

        val pager =  activity?.findViewById<ViewPager2>(R.id.pager_on_boarding)
        val pagerAdapter = pager?.adapter as OnBoardingFragmentAdapter

        pagerAdapter?.removeFirst()
        if (pagerAdapter.size == pagerAdapter.originalSize - 1) {
            pagerAdapter.removeFirst()
        }

        pager?.setCurrentItem(0, true)
    }

    override fun onResume() {
        super.onResume()

        val dots = activity?.findViewById<TabLayout>(R.id.dots_on_boarding)
        dots?.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}