package cz.movapp.app.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cz.movapp.app.App
import cz.movapp.app.appModule

class OnBoardingViewModel(application: Application) :  AndroidViewModel(application) {

    private fun appModule() = getApplication<App>().appModule()

    private val _onBoardingDone = MutableLiveData<Boolean>().apply {
        value = false
    }

    val onBoardingDone: MutableLiveData<Boolean> = _onBoardingDone

    override fun onCleared() {
        appModule().stateStore.saveState(
            OnBoardingStateKeys.ON_BOARDING_DONE,
            onBoardingDone.value!!
        )
    }
}