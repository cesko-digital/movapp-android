package cz.movapp.app.ui.children

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cz.movapp.app.data.Language
import cz.movapp.app.data.LanguagePair

class ChildrenFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    var langPair = LanguagePair.getDefault()

    override fun getItemCount(): Int {
        var numFragments: Int = 2

        if (langPair.from == Language.Czech || langPair.to == Language.Czech) {
            numFragments ++ /* +1 for fairy tales */
        }

        return numFragments
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position) {
            0 -> ChildrenDictionaryFragment()
            1 -> ChildrenMemoryGameFragment()
            2 -> ChildrenDictionaryFragment()
            else -> throw IndexOutOfBoundsException("AlphabetFragment: unexpected position = $position")
        }

        return fragment
    }
}