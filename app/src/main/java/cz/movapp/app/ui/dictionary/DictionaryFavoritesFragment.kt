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
import cz.movapp.app.adapter.DictionarySearchAdapter
import cz.movapp.app.databinding.FragmentDictionaryFavoritesBinding

class DictionaryFavoritesFragment : Fragment() {

    private var _binding: FragmentDictionaryFavoritesBinding? = null

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryFavorites
        recyclerView.setHasFixedSize(true)


        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            val searchAdapter = dictionarySharedViewModel.favoritesSearches.value

            val favoritesIds = mutableListOf<String>()
            it.forEach { favoritesIds.add(it.translationId) }

            searchAdapter?.favoritesIds = favoritesIds

            recyclerView.adapter = searchAdapter
            searchAdapter?.search("",true)
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            val favoritesAdapter =
                binding.recyclerViewDictionaryFavorites.adapter as DictionarySearchAdapter

            favoritesAdapter.langPair = lang
            favoritesAdapter.notifyDataSetChanged()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.recyclerViewDictionaryFavorites.adapter = null
        _binding = null
    }

}