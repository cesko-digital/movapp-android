package cz.movapp.app.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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
                if(findNavController().currentDestination?.route?.contains("sections") == true){
                    if (it.isEmpty()) {
                        navigateToDictionarySections()
                    } else {
                        if(findNavController().currentDestination?.id != R.id.nav_phrases_search){
                            findNavController().navigate(R.id.nav_phrases_search)
                        }
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            NavigationUI.navigateUp(
                findNavController(),
                AppBarConfiguration(findNavController().graph)
            )
        }

        return root
    }

    private fun navigateToDictionarySections() {
        navigateToSectionsStack(false)
    }

    private fun setupTabLayout() {
        binding.tabs.getTabAt(0)?.select()

        binding.tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    navigateByTab(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                private fun navigateByTab(tab: TabLayout.Tab?) {
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
                            binding.tabs.selectTab(binding.tabs.getTabAt(TAB_POSITION_FAVORITES))
                        }
                        else -> if(destination.route?.contains("sections") == true){
                            binding.tabs.selectTab(
                                binding.tabs.getTabAt(
                                    TAB_POSITION_SECTIONS_OR_TRANSLATIONS
                                )
                            )
                        }
                    }

                    when (destination.route) {
                        "sections" -> binding.topAppBar.navigationIcon = null
                        "favorites" -> binding.topAppBar.navigationIcon = null
                        else -> binding.topAppBar.navigationIcon = AppCompatResources
                            .getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)
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