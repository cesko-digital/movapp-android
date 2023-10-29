package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cz.movapp.android.hideKeyboard
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.MainViewModel
import cz.movapp.app.data.Favorites
import cz.movapp.app.databinding.FragmentDictionaryFavoritesBinding

class DictionaryFavoritesFragment : Fragment() {

    private var _binding: FragmentDictionaryFavoritesBinding? = null

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    private val binding get() = _binding!!

    private var forceFavoritesReload = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryFavorites
        recyclerView.setHasFixedSize(true)

        dictionarySharedViewModel.favoritesSearches.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
        }

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            reloadFavoritesIds(it)
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            val adapter = getRVAdapter()

            if (adapter.langPair != lang) {
                dictionarySharedViewModel.onLanguageChanged(lang)

                if (favoritesViewModel.favorites.value != null) {
                    reloadFavoritesIds(favoritesViewModel.favorites.value!!)
                }

                adapter.notifyDataSetChanged()
            }
        })

        dictionarySharedViewModel.searchQuery.observe(viewLifecycleOwner) {
            if (dictionarySharedViewModel.searchQuery.value != null) {

                dictionarySharedViewModel.favoritesSearches.value?.search(
                    dictionarySharedViewModel.searchQuery.value!!, true
                )
            }
        }

        return root
    }

    private fun reloadFavoritesIds(favoritesIds: List<Favorites>) {
        val adapter = getRVAdapter()

        adapter.favoritesIds = favoritesIds.map { it.translationId } as MutableList
        adapter.setFavorites(forceFavoritesReload)

        forceFavoritesReload = false
    }

    private fun getRVAdapter(): DictionaryPhrasesSearchAllAdapter {
        return binding.recyclerViewDictionaryFavorites.adapter as DictionaryPhrasesSearchAllAdapter
    }

    override fun onPause() {
        super.onPause()

        hideKeyboard(requireView(), requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.recyclerViewDictionaryFavorites.adapter = null
        _binding = null
        forceFavoritesReload = true
    }

}