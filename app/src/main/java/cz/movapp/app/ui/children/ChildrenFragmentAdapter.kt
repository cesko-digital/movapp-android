package cz.movapp.app.ui.children

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChildrenFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position) {
            0 -> ChildrenDictionaryFragment()
            1 -> ChildrenMemoryGameFragment()
            else -> throw IndexOutOfBoundsException("AlphabetFragment: unexpected position = $position")
        }

        return fragment
    }
}