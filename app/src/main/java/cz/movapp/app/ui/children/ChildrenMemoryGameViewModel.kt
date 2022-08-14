package cz.movapp.app.ui.children

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import cz.movapp.app.App
import cz.movapp.app.appModule
import cz.movapp.app.data.ChildrenDatasource
import cz.movapp.app.data.LanguagePair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ChildrenMemoryGameViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private fun appModule() = getApplication<App>().appModule()

    val mute = MutableLiveData<Boolean>().apply { value = false }

    val edgeSize = 4

    lateinit var discovered: List<MutableList<Boolean>>

    val bestScore = MutableLiveData<Int>().apply { value = 9999 }

    private val _score = MutableLiveData<Int>().apply { value = 0 }

    val score: MutableLiveData<Int> = _score

    private val _memoryGameAdapter = MutableLiveData<ChildrenMemoryGameAdapter>().apply {
        value = randomizedAdapter(LanguagePair.getDefault())
    }

    val memoryGameAdapter: MutableLiveData<ChildrenMemoryGameAdapter> = _memoryGameAdapter

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val storedMute =
                appModule().dataStore.restoreState(ChildrenMemoryGameKeys.MUTE)

            val muteValue = storedMute.first()
            withContext(Dispatchers.Main) {
                muteValue?.let { mute.value = it }
            }

            val storedScore =
                appModule().dataStore.restoreState(ChildrenMemoryGameKeys.BEST_SCORE)

            val scoreValue = storedScore.first()
            withContext(Dispatchers.Main) {
                scoreValue?.let {  bestScore.value = it }
            }
        }
    }

    private fun randomizedAdapter(lanPair: LanguagePair) : ChildrenMemoryGameAdapter {
        val randomized = mutableListOf<ChildrenDictionaryData>()
        val used = mutableListOf<Int>()
        val cardList = mutableListOf<MemoryGameGridViewData>()
        val data = ChildrenDatasource().loadChildren(context, lanPair)

        val shuffled = data.shuffled()
        for (i in 0 until (2 * edgeSize)) {
            randomized.add(shuffled[i])
            used.add(2)
        }

        for (y in 0 until edgeSize) {
            for (x in 0 until edgeSize) {
                var image: Drawable? = null

                var rndCard = (0 until (2 * edgeSize)).shuffled()[0]
                while ( used[rndCard] == 0) {
                    rndCard = (0 until (2 * edgeSize)).shuffled()[0]
                }

                used[rndCard] -= 1

                image = try {
                    val imageStream = context.assets.open(randomized[rndCard].image_path)
                    Drawable.createFromStream(imageStream, null)
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                    null
                }

                val mainSound = used[rndCard] == 0

                cardList.add(MemoryGameGridViewData(x, y, randomized[rndCard], mainSound, image))
            }
        }

        cardList.shuffle()

        return ChildrenMemoryGameAdapter(cardList)
    }

    fun createNewGame(lanPair: LanguagePair) {
        discovered = List<MutableList<Boolean>>(edgeSize){
            kotlin.collections.MutableList<kotlin.Boolean>(edgeSize) { false }
        }
        _memoryGameAdapter.value = randomizedAdapter(lanPair)
    }

    fun increaseFaults() {
        _score.value = _score.value?.plus(1)
    }

    fun toggleVolume() {
        mute.value = !mute.value!!
    }

    fun store() {
        appModule().dataStore.saveState(
            ChildrenMemoryGameKeys.MUTE,
            mute.value!!
        )
    }

    fun setBestScore() {
        if (bestScore.value!! < _score.value!!) {
            return
        }

        bestScore.value = _score.value

        if (bestScore.value!! < 9999) {
            appModule().dataStore.saveState(
                ChildrenMemoryGameKeys.BEST_SCORE,
                bestScore.value!!
            )
        }
    }

    override fun onCleared() {
        store()
    }
}