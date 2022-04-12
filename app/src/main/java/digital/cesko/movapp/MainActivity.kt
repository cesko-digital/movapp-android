package digital.cesko.movapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import digital.cesko.movapp.databinding.ActivityMainBinding
import digital.cesko.movapp.ui.dictionary.DictionaryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var fromUa = true

    private val dictionarySharedViewModel: DictionaryViewModel by viewModels()
    private val mainSharedModel: MainViewModel by viewModels()

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
                R.id.navigation_dictionary, R.id.navigation_alphabet, R.id.navigation_children
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.inputSearch.addTextChangedListener {
            if (binding.inputSearch.text.toString().isNotEmpty()) {
                try {
                    /**
                     *  if this fails then we need to change the fragment
                     *  to fragment with search results
                     */
                    navController.getBackStackEntry(R.id.dictionary_content_fragment)

                    dictionarySharedViewModel.setSelectedTranslationIds(listOf())
                    dictionarySharedViewModel.search(binding.inputSearch.text.toString())
                } catch (ex: IllegalArgumentException) {
                    val bundle = Bundle()
                    bundle.putString("constraint", binding.inputSearch.text.toString())
                    navController.navigate(R.id.dictionary_content_fragment, bundle)
                }
            }
        }

        binding.inputLayoutSearch.setEndIconOnClickListener {
            fromUa = mainSharedModel.fromUa.value == true
            fromUa = when (fromUa) {
                true -> {
                    binding.inputLayoutSearch.setEndIconDrawable(R.drawable.cz)
                    false
                }
                false -> {
                    binding.inputLayoutSearch.setEndIconDrawable(R.drawable.ua)
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.top_menu_about -> {
                Toast.makeText(this, "This is Movapp", Toast.LENGTH_SHORT).show()
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