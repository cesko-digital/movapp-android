package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
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
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

    private var searchesAdapterDataObservers = mutableListOf<RecyclerView.AdapterDataObserver>()

    private lateinit var searchJob : Job

    private lateinit var translationsIdsArgs: MutableList<String>

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

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels {
        DictionaryViewModelFactory(
            requireActivity().application, favoritesViewModel
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            translationsIdsArgs =
                it.getStringArray("translation_ids")?.toMutableList() ?: mutableListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (translationsIdsArgs.isEmpty())
            dictionarySharedViewModel.translationsIds.value = mutableListOf()
        else
            dictionarySharedViewModel.translationsIds.value = translationsIdsArgs

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            var favoritesIds = mutableListOf<String>()

            it.forEach { favoritesIds.add(it.translationId) }

            dictionarySharedViewModel.searches.value?.favoritesIds = favoritesIds
            dictionarySharedViewModel.translations.favoritesIds = favoritesIds
        }

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
                            setSectionsOrTranslations()
                        }
                        1 -> {
                            childFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, DictionaryFavoritesFragment())
                                    .commit()
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

                dictionarySharedViewModel.translations.search(
                    dictionarySharedViewModel.searchQuery.value!!, favorites
                )
            }
        }

        dictionarySharedViewModel.translationsIds.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                setTranslations(it)
        }

        setSectionsOrTranslations()

        return root
    }

    private fun setTranslations(translationIds: MutableList<String>) {
        if (childFragmentManager.findFragmentByTag("TRANSLATIONS") == null) {
            val translationsFragment = DictionaryTranslationsFragment()
            val args = Bundle()
            args.putStringArray("translation_ids", translationIds.toTypedArray())
            translationsFragment.arguments = args
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, translationsFragment, "TRANSLATIONS")
                .commit()
        }
    }

    private fun setSectionsOrTranslations() {
        if (dictionarySharedViewModel.translationsIds.value!!.isEmpty())
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, DictionarySectionsFragment()).commit()
        else
            setTranslations(dictionarySharedViewModel.translationsIds.value!!)
    }

    private fun searchDictionary(query: String?): Boolean {
        if (query != null) {
            if (query.isNotEmpty()) {
                if (childFragmentManager.findFragmentByTag("SEARCH") == null)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, DictionaryTranslationsFragment(),"SEARCH")
                            .commit()

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

        _binding = null
    }
}