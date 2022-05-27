package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import cz.movapp.android.hideKeyboard
import cz.movapp.android.textChanges
import cz.movapp.app.FavoritesViewModel
import cz.movapp.app.FavoritesViewModelFactory
import cz.movapp.app.R
import cz.movapp.app.data.FavoritesDatabase
import cz.movapp.app.databinding.FragmentDictionaryBinding
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

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

    private val dictionarySharedViewModel: DictionaryViewModel by activityViewModels {
        DictionaryViewModelFactory(
            requireActivity().application, favoritesViewModel
        )
    }

    private val TAB_POSITION_SECTIONS_OR_TRANSLATIONS = 0
    private val TAB_POSITION_FAVORITES = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupTabLayout()

        binding.searchView.hint = resources.getString(R.string.search_word)

        viewLifecycleOwner.lifecycleScope.launch {
            binding.searchView.textChanges()
                .debounce(300)
                .drop(1)
                .collect { text ->
                    dictionarySharedViewModel.setSearchQuery(text.toString())
                }
        }

        dictionarySharedViewModel.searchQuery.observe(viewLifecycleOwner) {
            if (it != null) {
                if (binding.tab.selectedTabPosition == TAB_POSITION_SECTIONS_OR_TRANSLATIONS) {
                    if (it.isEmpty()) {
                        navigateToSectionsStack(false)
                    } else {
                        if(findNavController().currentDestination?.id != R.id.nav_search){
                            findNavController().navigate(R.id.nav_search)
                        }
                    }
                }
            }
        }

        return root
    }

    private fun setupTabLayout() {
        binding.tab.getTabAt(0)?.select()

        binding.tab.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    selectTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                private fun selectTab(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        TAB_POSITION_FAVORITES -> {
                            navigateToFavoritesStack()
                        }
                        TAB_POSITION_SECTIONS_OR_TRANSLATIONS -> {
                            navigateToSectionsStack(true)
                        }
                    }
                }
            }
        )

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val listener = object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    when (destination.route) {
                        "favorites" -> {
                            binding.tab.selectTab(binding.tab.getTabAt(TAB_POSITION_FAVORITES))
                        }
                        else -> {
                            binding.tab.selectTab(
                                binding.tab.getTabAt(
                                    TAB_POSITION_SECTIONS_OR_TRANSLATIONS
                                )
                            )
                        }
                    }
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                findNavController().addOnDestinationChangedListener(listener)
            }

            override fun onPause(owner: LifecycleOwner) {
                findNavController().removeOnDestinationChangedListener(listener)
            }
        })
    }


    fun findNavController(): NavController {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_dictionary)
    }


    fun navigateToSectionsStack(restoreState: Boolean) {
        val navController = findNavController()

        navController.navigate("sections") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            this.restoreState = restoreState
        }
    }

    fun navigateToFavoritesStack() {
        val navController = findNavController()

        navController.navigate("favorites") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard(view, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}