package digital.cesko.movapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import digital.cesko.movapp.data.Favorites
import digital.cesko.movapp.data.FavoritesDatabase
import digital.cesko.movapp.databinding.ActivityMainBinding
import digital.cesko.movapp.ui.dictionary.DictionaryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    var fromUa = true
    var favorites = listOf<Favorites>()

    private val dictionarySharedViewModel: DictionaryViewModel by viewModels()
    private val mainSharedModel: MainViewModel by viewModels()

    private val favoritesDatabase: FavoritesDatabase by lazy { FavoritesDatabase.getDatabase(this) }
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
                Toast.makeText(this, "This is Movapp", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.top_menu_switch_language -> {
                fromUa = mainSharedModel.fromUa.value == true
                fromUa = when (fromUa) {
                    true -> {
                        item.setIcon(R.drawable.cz)
                        false
                    }
                    false -> {
                        item.setIcon(R.drawable.ua)
                        true
                    }
                }

                mainSharedModel.setFromUa(fromUa)

                try {
                    /**
                     * if the next line fails, it is OK
                     * it just mean we are a different fragment
                     * and do not want to call the rest...
                     */
                    navController.getBackStackEntry(R.id.navigation_alphabet)

                    navController.popBackStack()
                    navController.navigate(R.id.navigation_alphabet)
                } catch (ex: IllegalArgumentException){
                    /* nothing */
                }

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