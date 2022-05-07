package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.android.textChanges
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.MainViewModel
import cz.movapp.app.R
import cz.movapp.app.adapter.DictionaryTranslationsAdapter
import cz.movapp.app.databinding.FragmentDictionaryFavoritesBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class DictionaryFavoritesFragment : Fragment() {

    private var _binding: FragmentDictionaryFavoritesBinding? = null
    private var favoritesIds = mutableListOf<String>()

    private val mainSharedViewModel: MainViewModel by activityViewModels()
    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels()
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val mainSharedModel: MainViewModel by viewModels()
    private val viewModel by viewModels<DictionaryFavoritesViewModel>()
    private lateinit var searchJob : Job

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

        recyclerView.adapter = dictionarySharedViewModel.translations

        (recyclerView.adapter as DictionaryTranslationsAdapter).favoritesIds = favoritesIds

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { it ->
            favoritesIds = mutableListOf()

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

        mainSharedModel.selectedLanguage.observe(viewLifecycleOwner) { fromUa ->
            binding.toolbar.flagView.setImageResource(fromUa.from.flagResId)
        }

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            if (!viewModel.searchQuery.value.isNullOrEmpty()) {
                (recyclerView.adapter as DictionaryTranslationsAdapter).search(
                    viewModel.searchQuery.value!!, true
                )
            }
        }

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

        /**
         *  we need to cancel the job in order to prevent leaking bound object inside of the job
         *  because lifecycleScope is alive between onCreate and onDestroy
         */
        searchJob.cancel()

        binding.recyclerViewDictionaryFavorites.adapter = null
        _binding = null
    }

    private fun searchDictionary(query: String?): Boolean {
        if (query != null) {
            if (query.isNotEmpty()) {
                viewModel.setSearchQuery(query)
                return true
            }
        }

        return false
    }
}