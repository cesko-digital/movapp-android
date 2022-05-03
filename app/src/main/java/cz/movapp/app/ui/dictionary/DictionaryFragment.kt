package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.android.textChanges
import cz.movapp.app.*
import cz.movapp.app.adapter.DictionaryAdapter
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

    private val mainSharedModel: MainViewModel by viewModels()

    private var favoritesIds = mutableListOf<String>()

    private lateinit var searchJob : Job

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val favoritesDatabase: FavoritesDatabase by lazy {
        FavoritesDatabase.getDatabase(
            requireContext()
        )
    }
    private val favoritesViewModel: FavoritesViewModel by activityViewModels {
        FavoritesViewModelFactory(
            favoritesDatabase.favoritesDao()
        )
    }

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels {
        DictionaryViewModelFactory(
            requireActivity().application, favoritesViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerViewDictionary
        recyclerView.setHasFixedSize(true)
        dictionarySharedViewModel.sections.observe(viewLifecycleOwner) {
            recyclerView.adapter = it
            it.langPair = mainSharedViewModel.selectedLanguage.value!!
        }

        mainSharedViewModel.selectedLanguage.observe(viewLifecycleOwner) {
            (recyclerView.adapter as DictionaryAdapter).langPair =
                mainSharedViewModel.selectedLanguage.value!!
            recyclerView.adapter?.notifyDataSetChanged()
        }


        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf()

            it.forEach { favoritesIds.add(it.translationId) }

            (recyclerView.adapter as DictionaryAdapter).favoritesIds = favoritesIds
        }


        //TODO remove duplicity in dictionary and favorites, refactor viewmodel usage

        binding.tab.getTabAt(0)?.select()

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
                        1 -> {
                            findNavController().navigate(DictionaryFragmentDirections.navigateToFavorites())
                        }
                    }
                }
            }
        )

        binding.toolbar.searchView.hint = resources.getString(R.string.title_search)

        searchJob = lifecycleScope.launch {
            binding.toolbar.searchView.textChanges()
                .debounce(300)
                .collect { text ->
                    searchDictionary(text.toString())
                }
        }

        binding.toolbar.flagView.setOnClickListener {
            mainSharedModel.selectLanguage(LanguagePair.nextLanguage(mainSharedModel.selectedLanguage.value!!))
        }

        mainSharedModel.selectedLanguage.observe(viewLifecycleOwner, Observer { fromUa ->
            binding.toolbar.flagView.setImageResource(fromUa.from.flagResId)
        })

        return root
    }

    fun searchDictionary(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            binding.recyclerViewDictionary.adapter = dictionarySharedViewModel.sections.value
        }
        if (query != null) {
            if (query.isNotEmpty()) {
                binding.recyclerViewDictionary.adapter = dictionarySharedViewModel.searches.value
                try {
                    /**
                     *  if this fails then we need to change the fragment
                     *  to fragment with search results
                     */
                    findNavController().getBackStackEntry(R.id.dictionary_translations_fragment)
                } catch (ex: IllegalArgumentException) {
                    //TODO

                    (binding.recyclerViewDictionary.adapter as DictionaryTranslationsAdapter).search(
                        query, false
                    )
                }

            }
            dictionarySharedViewModel.setSearchQuery(query)
            return true
        }

        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        /**
         *  we need to cancel the job in order to prevent leaking bound object inside of the job
         *  because lifecycleScope is alive between onCreate and onDestroy
         */
        searchJob.cancel()

        binding.recyclerViewDictionary.adapter = null
        _binding = null
    }
}