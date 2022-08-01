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
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentOnBoardingLanguageNativeBinding

class OnBoardingLanguageNativeFragment : Fragment() {
    private var _binding: FragmentOnBoardingLanguageNativeBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingLanguageNativeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.imageFlagUa.setOnClickListener {
            mainSharedViewModel.selectNativeLanguage(Language.Ukrainian)
            mainSharedViewModel.selectLanguage(LanguagePair.UkToCs)
            mainSharedViewModel.storeLanguage()

            val pager = activity?.findViewById<ViewPager2>(R.id.pager_on_boarding)
            (pager?.adapter as OnBoardingFragmentAdapter).removeFirst()
        }

        binding.imageFlagCz.setOnClickListener {
            goToInfo(LanguagePair.CsToUk)
        }

        binding.imageFlagSk.setOnClickListener {
            goToInfo(LanguagePair.SkToUk)
        }

        binding.imageFlagPl.setOnClickListener {
            goToInfo(LanguagePair.PlToUk)
        }

        return root
    }

    private fun goToInfo(langPair: LanguagePair) {
         mainSharedViewModel.selectNativeLanguage(langPair.from)
         mainSharedViewModel.selectLanguage(langPair)
         mainSharedViewModel.storeLanguage()

        val pager = activity?.findViewById<ViewPager2>(R.id.pager_on_boarding)
        val pagerAdapter = activity?.findViewById<ViewPager2>(R.id.pager_on_boarding)?.adapter as OnBoardingFragmentAdapter

        pagerAdapter.removeFirst()
        pagerAdapter.removeFirst()

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