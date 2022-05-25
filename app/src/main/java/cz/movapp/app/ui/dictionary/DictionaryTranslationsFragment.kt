package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.databinding.FragmentDictionaryTranslationsBinding

class DictionaryTranslationsFragment : Fragment() {

    private var _binding: FragmentDictionaryTranslationsBinding? = null

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val mainSharedViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryTranslationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionaryTranslations
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = dictionarySharedViewModel.translations.value

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            val favoritesIds = mutableListOf<String>()
            it.forEach { favoritesIds.add(it.translationId) }

            dictionarySharedViewModel.translations.value?.favoritesIds = favoritesIds
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner, Observer { lang ->
            val adapter =
                binding.recyclerViewDictionaryTranslations.adapter as DictionaryTranslationsAdapter

            if(adapter.langPair != lang){
                adapter.langPair = lang
                adapter.notifyDataSetChanged()
            }
        })

        dictionarySharedViewModel.translationsIds.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                //FIXME use adapter without diff - it blinks when going to different section
                dictionarySharedViewModel.translations.value?.selectTranslations(it)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.recyclerViewDictionaryTranslations.adapter = null
        _binding = null
    }
}