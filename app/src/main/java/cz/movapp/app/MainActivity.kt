package cz.movapp.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import cz.movapp.app.databinding.ActivityMainBinding
import cz.movapp.app.ui.onboarding.OnBoardingStateKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private fun appModule() = application.appModule()

    private val mainSharedModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            val onBoardingDone = appModule().stateStore.restoreState(OnBoardingStateKeys.ON_BOARDING_DONE).first()
            if (onBoardingDone == null || onBoardingDone == false)
                startActivity(Intent(applicationContext, OnBoardingActivity::class.java))
        }

        val navView: BottomNavigationView = binding.bottomNavigation

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dictionary, R.id.navigation_alphabet, R.id.navigation_children, R.id.navigation_about
            )
        )

        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

        /* get language after onboarding */
        mainSharedModel.restoreLanguage()
    }
}