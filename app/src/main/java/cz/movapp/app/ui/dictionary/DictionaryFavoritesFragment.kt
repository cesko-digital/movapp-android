package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cz.movapp.android.hideKeyboard
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.databinding.FragmentDictionaryFavoritesBinding

class DictionaryFavoritesFragment : Fragment() {

    private var _binding: FragmentDictionaryFavoritesBinding? = null
    private var favoritesIds = mutableListOf<String>()

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
        _binding = FragmentDictionaryFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryFavorites
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.favorites

        (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

            dictionarySharedViewModel.favorites.selectTranslations(favoritesIds)
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