package cz.movapp.app.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import cz.movapp.app.R
import cz.movapp.app.ui.onboarding.OnBoardingInfoFragment
import cz.movapp.app.ui.onboarding.OnBoardingWelcomeFragment

class OnBoardingFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position) {
            0 -> OnBoardingWelcomeFragment()
            1 -> OnBoardingInfoFragment(position)
            2 -> OnBoardingInfoFragment(position)
            3 -> OnBoardingInfoFragment(position)
            else -> OnBoardingWelcomeFragment()
        }

        return fragment
    }
}