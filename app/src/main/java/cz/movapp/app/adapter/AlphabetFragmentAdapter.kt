package cz.movapp.app.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cz.movapp.app.ui.alphabet.AlphabetDirection
import cz.movapp.app.ui.alphabet.AlphabetFragment

class AlphabetFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position) {
            0 -> AlphabetFragment.newInstance(AlphabetDirection.TO)
            1 -> AlphabetFragment.newInstance(AlphabetDirection.FROM)
            else -> throw IndexOutOfBoundsException("AlphabetFragment: unexpected position = $position")
        }

        return fragment
    }
}