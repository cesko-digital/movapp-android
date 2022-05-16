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
import cz.movapp.app.databinding.FragmentOnBoardingWelcomeBinding

class OnBoardingWelcomeFragment : Fragment() {
    private var _binding: FragmentOnBoardingWelcomeBinding? = null

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingWelcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonSelectFrom.setOnClickListener {
            goToInfo(LanguagePair.UkToCs)

        }

        binding.buttonSelectTo.setOnClickListener {
            goToInfo(LanguagePair.CsToUk)
        }

        return root
    }

    private fun goToInfo(langPair: LanguagePair) {
         mainSharedViewModel.selectLanguage(langPair)
         mainSharedViewModel.storeLanguage()

         val pager = activity?.findViewById<ViewPager2>(R.id.pager_on_boarding)
         pager?.setCurrentItem(1, true)
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