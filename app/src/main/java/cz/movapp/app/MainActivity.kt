package cz.movapp.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cz.movapp.app.OnBoardingActivity.Companion.registerOnBoardingResult
import cz.movapp.app.databinding.ActivityMainBinding
import cz.movapp.app.ui.onboarding.OnBoardingStateKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private fun appModule() = application.appModule()

    private val mainSharedModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onBoardingResultLauncher = registerOnBoardingResult(this,
            onOnBoardingLeft = { finish() })
        lifecycleScope.launch(Dispatchers.IO) {
            val onBoardingDone = appModule().dataStore.restoreState(OnBoardingStateKeys.ON_BOARDING_DONE).first()
            if (onBoardingDone == null || onBoardingDone == false){
                withContext(Dispatchers.Main){
                    onBoardingResultLauncher.launch(
                        Intent(
                            applicationContext,
                            OnBoardingActivity::class.java
                        )
                    )
                }
            }
        }

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemReselectedListener {
                Timber.d("menuItem $it")
            }
        }
    }


    override fun onResume() {
        super.onResume()

        /* get language after onboarding */
        mainSharedModel.restoreLanguage()
    }
}