package cz.movapp.app

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.movapp.android.TextWatcherAdapter
import cz.movapp.app.LanguagePair.Companion.nextLanguage
import cz.movapp.app.data.Favorites
import cz.movapp.app.databinding.ActivityMainBinding
import cz.movapp.app.databinding.ToolbarSearchBinding
import cz.movapp.app.ui.dictionary.DictionaryViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var searchBinding: ToolbarSearchBinding
    private lateinit var navController: NavController


    var favorites = listOf<Favorites>()

    private val dictionarySharedViewModel: DictionaryViewModel by viewModels()
    private val mainSharedModel: MainViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dictionary, R.id.navigation_favorites, R.id.navigation_alphabet, R.id.navigation_children
            )
        )

        binding.toolbar.setupWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

        favoritesViewModel.favorites.observe(this) {
            favorites = it
        }


        searchBinding = setupTopAppBarSearch()
//        setupTopAppBarWithSearchWithMenu()
    }

    /**
     * setup toolbar
     */
    fun setupTopAppBarWithSearchWithMenu() {
        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(R.menu.top_menu)


        binding.toolbar.menu.findItem(R.id.top_menu_about).setOnMenuItemClickListener {
            navController.navigate(R.id.navigation_about)
            true
        }

        binding.toolbar.menu.findItem(R.id.top_menu_switch_language).setOnMenuItemClickListener {
            mainSharedModel.selectLanguage(nextLanguage(mainSharedModel.selectedLanguage.value!!))
            true
        }

        mainSharedModel.selectedLanguage.observe(this, Observer { fromUa ->
            val languageItem = binding.toolbar.menu?.findItem(R.id.top_menu_switch_language)
            languageItem?.setIcon(fromUa.from.flagResId)
        })

        searchBinding.root.visibility = View.VISIBLE
        binding.toolbar.title = ""
    }

    private fun setupTopAppBarSearch(): ToolbarSearchBinding {
        val binding =
            ToolbarSearchBinding.inflate(this.layoutInflater, binding.toolbar, false)

        this.binding.toolbar.addView(binding.root)
        binding.searchView.hint = resources.getString(R.string.title_search)

        binding.searchView.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(text: Editable?) {
                searchDictionary(text.toString())
            }
        })

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