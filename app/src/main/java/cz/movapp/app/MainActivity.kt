package cz.movapp.app

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.movapp.android.textChanges
import cz.movapp.app.LanguagePair.Companion.nextLanguage
import cz.movapp.app.databinding.ActivityMainBinding
import cz.movapp.app.databinding.ToolbarSearchBinding
import cz.movapp.app.ui.dictionary.DictionaryViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var searchBinding: ToolbarSearchBinding
    private lateinit var navController: NavController

    private val dictionarySharedViewModel: DictionaryViewModel by viewModels()
    private val mainSharedModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dictionary, R.id.navigation_favorites, R.id.navigation_alphabet, R.id.navigation_children
            )
        )

        binding.topAppBar.setupWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

        searchBinding = setupTopAppBarSearch()
//        setupTopAppBarWithSearchWithMenu()
    }

    /**
     * setup toolbar
     */
    fun setupTopAppBarWithSearchWithMenu() {
        binding.topAppBar.menu.clear()
        binding.topAppBar.inflateMenu(R.menu.top_menu)


        binding.topAppBar.menu.findItem(R.id.top_menu_about).setOnMenuItemClickListener {
            navController.navigate(R.id.navigation_about)
            true
        }

        binding.topAppBar.menu.findItem(R.id.top_menu_switch_language).setOnMenuItemClickListener {
            mainSharedModel.selectLanguage(nextLanguage(mainSharedModel.selectedLanguage.value!!))
            true
        }

        mainSharedModel.selectedLanguage.observe(this, Observer { fromUa ->
            val languageItem = binding.topAppBar.menu?.findItem(R.id.top_menu_switch_language)
            languageItem?.setIcon(fromUa.from.flagResId)
        })

        searchBinding.root.visibility = View.VISIBLE
        binding.topAppBar.title = ""
    }

    private fun setupTopAppBarSearch(): ToolbarSearchBinding {
        val binding =
            ToolbarSearchBinding.inflate(this.layoutInflater, binding.topAppBar, false)

        this.binding.topAppBar.addView(binding.root)
        binding.searchView.hint = resources.getString(R.string.title_search)

        lifecycleScope.launch {
            binding.searchView.textChanges()
                .debounce(300)
                .collect{ text ->
                    searchDictionary(text.toString())
                }
        }

        binding.flagView.setOnClickListener { view ->
            mainSharedModel.selectLanguage(nextLanguage(mainSharedModel.selectedLanguage.value!!))
        }

        mainSharedModel.selectedLanguage.observe(this, Observer { fromUa ->
            binding.flagView.setImageResource(fromUa.from.flagResId)
        })

        return binding
    }

    fun searchDictionary(query: String?): Boolean {
        if (query != null) {
            if (query.isNotEmpty()) {
                try {
                    /**
                     *  if this fails then we need to change the fragment
                     *  to fragment with search results
                     */
                    this@MainActivity.navController.getBackStackEntry(R.id.dictionary_translations_fragment)
                } catch (ex: IllegalArgumentException) {
                    this@MainActivity.navController.navigate(R.id.dictionary_translations_fragment)
                }

            }
            dictionarySharedViewModel.setSearchQuery(query)
            return true
        }

        return false
    }
}