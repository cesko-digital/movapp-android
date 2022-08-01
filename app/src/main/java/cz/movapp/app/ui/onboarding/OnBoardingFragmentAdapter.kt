package cz.movapp.app.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    val originalSize = 6
    var size = originalSize
    var removed = 0
    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        var pos = position

        pos += removed

        val infoPosition = pos - 2

        val fragment = when(pos) {
            0 -> OnBoardingLanguageNativeFragment()
            1 -> OnBoardingLanguageLearnFragment()
            2 -> OnBoardingInfoFragment(infoPosition)
            3 -> OnBoardingInfoFragment(infoPosition)
            4 -> OnBoardingInfoFragment(infoPosition)
            5 -> OnBoardingInfoFragment(infoPosition)
            else -> OnBoardingLanguageNativeFragment()
        }

        return fragment
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position + removed)
    }

    fun removeFirst() {
        size -= 1
        removed += 1
        notifyDataSetChanged()
    }
}