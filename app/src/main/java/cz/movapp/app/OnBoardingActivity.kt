package cz.movapp.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import cz.movapp.app.ui.onboarding.OnBoardingFragmentAdapter
import cz.movapp.app.databinding.ActivityOnBoardingBinding

val ONBOARDING_PASSED_RESULT_CODE = 1
val ONBOARDING_LEFT_RESULT_CODE = 2

class OnBoardingActivity  : AppCompatActivity() {

    lateinit var binding: ActivityOnBoardingBinding

    companion object{
        fun registerOnBoardingResult(activity: ComponentActivity, onOnBoardingLeft: () -> Unit = {}, onOnBoardingPassed: () -> Unit = {}): ActivityResultLauncher<Intent> {
            return activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    when (result.resultCode) {
                        ONBOARDING_LEFT_RESULT_CODE -> onOnBoardingLeft()
                        ONBOARDING_PASSED_RESULT_CODE -> onOnBoardingPassed()
                        else -> {}
                    }
                }
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pagerOnBoarding.adapter = OnBoardingFragmentAdapter(this)

        binding.dotsOnBoarding
        TabLayoutMediator(binding.dotsOnBoarding,binding.pagerOnBoarding) { tab, position ->
        }.attach()
    }

    override fun onBackPressed() {
        if (binding.pagerOnBoarding.currentItem != 0) {
            binding.pagerOnBoarding.setCurrentItem(
                binding.pagerOnBoarding.currentItem - 1,
                true
            )
        } else {
            this.setResult(ONBOARDING_LEFT_RESULT_CODE)
            super.onBackPressed()
        }
        return
    }
}