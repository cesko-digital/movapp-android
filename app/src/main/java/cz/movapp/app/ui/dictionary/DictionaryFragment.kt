package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.FavoritesViewModelFactory
import cz.movapp.app.MainActivity
import cz.movapp.app.MainViewModel
import cz.movapp.app.adapter.DictionaryAdapter
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null

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
                        }
                        1 -> {
                            findNavController().navigate(DictionaryFragmentDirections.navigateToFavorites())
                        }
                    }
                }
            }
        )


        setupTopAppBarWithSearchWithMenu()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    /**
     * Activity binding is not init when application starts
     * thus postponing it to onStart when app starts/binding is null, otherwise do it right away
     */
    private fun setupTopAppBarWithSearchWithMenu() {
        val mainActivity = requireActivity() as MainActivity
        try {
            mainActivity.setupTopAppBarWithSearchWithMenu()
        } catch (e: UninitializedPropertyAccessException) {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    //must be in onStart otherwise activity binding not init yet
                    mainActivity.setupTopAppBarWithSearchWithMenu()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewDictionary.adapter = null
        _binding = null
    }
}