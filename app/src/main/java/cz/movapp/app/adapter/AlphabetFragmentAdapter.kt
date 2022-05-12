package cz.movapp.app.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cz.movapp.app.ui.alphabet.AlphabetFromFragment
import cz.movapp.app.ui.alphabet.AlphabetToFragment

class AlphabetFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position) {
            0 -> AlphabetFromFragment()
            1 -> AlphabetToFragment()
            else -> AlphabetFromFragment()
        }

        return fragment
    }
}