package cz.movapp.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.movapp.app.data.Favorites
import cz.movapp.app.databinding.ActivityMainBinding
import cz.movapp.app.ui.dictionary.DictionaryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        favoritesViewModel.favorites.observe(this) {
            favorites = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)


        mainSharedModel.fromUa.observe(this, Observer { fromUa ->
            val languageItem = menu?.findItem(R.id.top_menu_switch_language)
            languageItem?.setIcon(if (fromUa) R.drawable.ua else R.drawable.cz)
        })

        val search = menu?.findItem(R.id.search_bar)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.title_search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean  = searchDictionary(query)
            override fun onQueryTextChange(query: String?): Boolean = searchDictionary(query)

            fun searchDictionary(query: String?): Boolean {
                if (query != null) {
                    if (query.isNotEmpty()) {
                        try {
                            /**
                             *  if this fails then we need to change the fragment
                             *  to fragment with search results
                             */
                            navController.getBackStackEntry(R.id.dictionary_content_fragment)

                            dictionarySharedViewModel.search(query)
                        } catch (ex: IllegalArgumentException) {
                            val bundle = Bundle()
                            bundle.putString("constraint", query)
                            navController.navigate(R.id.dictionary_content_fragment, bundle)
                        }
                    }
                    return true
                }

                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.top_menu_about -> {
                navController.navigate(R.id.navigation_about)
                return true
            }

            R.id.top_menu_switch_language -> {
                mainSharedModel.setFromUa(!mainSharedModel.fromUa.value!!)
                return true
            }

            //  Otherwise, do nothing and use the core event handling

            // when clauses require that all possible paths be accounted for explicitly,
            //  for instance both the true and false cases if the value is a Boolean,
            //  or an else to catch all unhandled cases.
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}