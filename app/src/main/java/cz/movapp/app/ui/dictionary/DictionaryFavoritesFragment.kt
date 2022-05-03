package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.MainActivity
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.databinding.FragmentDictionaryFavoritesBinding

class DictionaryFavoritesFragment : Fragment() {

    private var _binding: FragmentDictionaryFavoritesBinding? = null
    private var favoritesIds = mutableListOf<String>()

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).setupTopAppBarWithSearchWithMenu()

        _binding = FragmentDictionaryFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryFavorites
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.translations

        (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

            (recyclerView.adapter as DictionaryTranslationsAdapter).submitList(
                dictionarySharedViewModel.selectedTranslations(favoritesIds)
            )
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (recyclerView.adapter as DictionaryTranslationsAdapter).langPair =
                mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter?.notifyDataSetChanged()
        }

        binding.tab.getTabAt(1)?.select()

        binding.tab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Do not Implement
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    selectTab(tab)
                }

                private fun selectTab(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            findNavController().navigate(DictionaryFragmentDirections.navigateToDictionary())
                        }
                        1 -> {
                        }
                    }
                }
            }
        )



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)

        (binding.recyclerViewDictionaryFavorites.adapter as DictionaryTranslationsAdapter).submitList(
            dictionarySharedViewModel.selectedTranslations(favoritesIds)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewDictionaryFavorites.adapter = null
        _binding = null
    }
}