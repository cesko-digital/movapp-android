package cz.movapp.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import cz.movapp.app.MainViewModel
import cz.movapp.app.ONBOARDING_PASSED_RESULT_CODE
import cz.movapp.app.R
import cz.movapp.app.databinding.FragmentOnBoardingInfoBinding



class OnBoardingInfoFragment(private val position: Int) : Fragment() {
    private var _binding: FragmentOnBoardingInfoBinding? = null

    private val onBoardingViewModel: OnBoardingViewModel by activityViewModels()

    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val exitListener = View.OnClickListener {
            onBoardingViewModel.onBoardingDone.value = true
            val activity = requireActivity()
            activity.setResult(ONBOARDING_PASSED_RESULT_CODE)
            activity.finish()
        }

        binding.textSkip.setOnClickListener(exitListener)
        binding.buttonExitOnBoarding.setOnClickListener(exitListener)

        when (position) {
            0 -> {
                binding.imageInfo.setImageResource(R.drawable.excellent)
                binding.textInfoTitle.setText(R.string.on_boarding_info_0_title)
                mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
                    binding.textInfoDescription.setText(String.format(
                        resources.getString(R.string.on_boarding_info_0_description),
                        resources.getString(it.to.adjectiveStringId)
                    ))
                }
                binding.textSkip.visibility = View.VISIBLE
                binding.buttonExitOnBoarding.visibility = View.INVISIBLE
            }
            1 -> {
                binding.imageInfo.setImageResource(R.drawable.dictionary)
                binding.textInfoTitle.setText(R.string.on_boarding_info_1_title)
                binding.textInfoDescription.setText(R.string.on_boarding_info_1_description)
                binding.textSkip.visibility = View.VISIBLE
                binding.buttonExitOnBoarding.visibility = View.INVISIBLE
            }
            2 -> {
                binding.imageInfo.setImageResource(R.drawable.alphabet)
                binding.textInfoTitle.setText(R.string.on_boarding_info_2_title)
                binding.textInfoDescription.setText(R.string.on_boarding_info_2_description)
                binding.textSkip.visibility = View.VISIBLE
                binding.buttonExitOnBoarding.visibility = View.INVISIBLE
            }
            3 -> {
                binding.imageInfo.setImageResource(R.drawable.child)
                binding.textInfoTitle.setText(R.string.on_boarding_info_3_title)
                binding.textInfoDescription.setText(R.string.on_boarding_info_3_description)
                binding.textSkip.visibility = View.INVISIBLE
                binding.buttonExitOnBoarding.visibility = View.VISIBLE
            }
        }
        return root
    }

    override fun onResume() {
        super.onResume()

        val dots = activity?.findViewById<TabLayout>(R.id.dots_on_boarding)
        dots?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}