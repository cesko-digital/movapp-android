package cz.movapp.app.ui.onboarding

import com.google.gson.reflect.TypeToken
import cz.movapp.android.StateStore

class OnBoardingStateKeys {
    companion object {
        val ON_BOARDING_DONE =
            StateStore.Key<Boolean>(
                "OnBoardingState.done",
                object : TypeToken<Boolean>() {}.type
            )
    }
}