package cz.movapp.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.movapp.app.adapter.OnBoardingFragmentAdapter
import cz.movapp.app.databinding.ActivityOnBoardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingActivity  : AppCompatActivity() {

    lateinit var binding: ActivityOnBoardingBinding

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
        }
        return
    }
}