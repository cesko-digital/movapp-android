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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.android.textChanges
import cz.movapp.app.*
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.data.SharedPrefsRepository
import cz.movapp.app.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

    private val mainSharedModel: MainViewModel by viewModels()

    private var searchesAdapterDataObservers = mutableListOf<RecyclerView.AdapterDataObserver>()

    private lateinit var translationIds: List<String>

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            translationIds = it.getStringArray("translation_ids")?.toList() ?: listOf<String>()
        }
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

        val pref = SharedPrefsRepository(requireContext())
        dictionarySharedViewModel.sections.value?.preferedLanguage = pref.getPreferedLanguage()!!
        dictionarySharedViewModel.translations.preferedLanguage = pref.getPreferedLanguage()!!
        dictionarySharedViewModel.searches.value?.preferedLanguage = pref.getPreferedLanguage()!!

        dictionarySharedViewModel.setSearchQuery("")

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            var favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            favoritesViewModel.favoritesIds.value = favoritesIds
        }

        favoritesViewModel.favoritesIds.observe(viewLifecycleOwner) { it ->
            dictionarySharedViewModel.searches.value?.favoritesIds = it
            dictionarySharedViewModel.translations.favoritesIds = it
        }

        /**
         * This data observer is used to scroll up in case of data change.
         * It is necessary in searching. As the user writes, the recyclerview
         * would remain on previously found result but the more accurate result
         * is on top.
         */
        dictionarySharedViewModel.searches.value!!.registerAdapterDataObserver(
            object: RecyclerView.AdapterDataObserver() {
                init {
                    searchesAdapterDataObservers.add(this)
                }

                override fun onChanged() {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    if (_binding != null)
                        binding.recyclerViewDictionary.scrollToPosition(0)
                }
            }
        )

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
                        0 -> {
                            if (translationIds.isEmpty())
                                selectSections()
                            else
                                dictionarySharedViewModel.translationsIds.value = translationIds.toMutableList()
                        }
                        1 -> {
                            // empty for favorites
                            dictionarySharedViewModel.translationsIds.value = mutableListOf()
                        }
                    }
                }
            }
        )

        binding.searchView.hint = resources.getString(R.string.title_search)

        searchJob = lifecycleScope.launch {
            binding.searchView.textChanges()
                .debounce(300)
                .collect { text ->
                    searchDictionary(text.toString())
                }
        }

        dictionarySharedViewModel.searchQuery.observe(viewLifecycleOwner) {
            if (!dictionarySharedViewModel.searchQuery.value.isNullOrEmpty()) {
                var favorites = false
                if (binding.tab.selectedTabPosition == 1)
                        favorites = true

                (recyclerView.adapter as DictionaryTranslationsAdapter).search(
                    dictionarySharedViewModel.searchQuery.value!!, favorites
                )
            }
        }

        dictionarySharedViewModel.translationsIds.observe(viewLifecycleOwner) {
            binding.recyclerViewDictionary.adapter = dictionarySharedViewModel.translations
            dictionarySharedViewModel.selectedTranslations(it)
        }

        if (translationIds.isEmpty())
            selectSections()
        else
            dictionarySharedViewModel.translationsIds.value = translationIds.toMutableList()

        return root
    }

    private fun selectSections() {
        dictionarySharedViewModel.sections.observe(viewLifecycleOwner) {
            binding.recyclerViewDictionary.adapter = it
        }
    }

    private fun searchDictionary(query: String?): Boolean {
        if (query != null) {
            if (query.isNotEmpty()) {
                binding.recyclerViewDictionary.adapter = dictionarySharedViewModel.searches.value
                dictionarySharedViewModel.setSearchQuery(query)
                return true
            }
        }

        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        for (observer in searchesAdapterDataObservers)
            dictionarySharedViewModel.searches.value?.unregisterAdapterDataObserver(observer)
        searchesAdapterDataObservers.clear()

        /**
         *  we need to cancel the job in order to prevent leaking bound object inside of the job
         *  because lifecycleScope is alive between onCreate and onDestroy
         */
        searchJob.cancel()

        binding.recyclerViewDictionary.adapter = null
        _binding = null
    }
}