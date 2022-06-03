package cz.movapp.app.ui.onboarding

import com.google.gson.reflect.TypeToken
import cz.movapp.android.DataStoreKey

class OnBoardingStateKeys {
    companion object {
        val ON_BOARDING_DONE =
            DataStoreKey<Boolean>(
                "OnBoardingState.done",
                object : TypeToken<Boolean>() {}.type
            )
    }
}