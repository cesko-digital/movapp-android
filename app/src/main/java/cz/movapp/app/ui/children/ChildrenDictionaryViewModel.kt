package cz.movapp.app.ui.children

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.movapp.app.App
import cz.movapp.app.appModule
import cz.movapp.app.data.ChildrenDatasource
import cz.movapp.app.data.LanguagePair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChildrenDictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    val childrenState = MutableLiveData<Int>(0)

    private val _children = MutableLiveData<ChildrenDictionaryAdapter>().apply {
        value = ChildrenDictionaryAdapter(ChildrenDatasource().loadChildren(context, LanguagePair.getDefault()))
    }

    val children: MutableLiveData<ChildrenDictionaryAdapter> = _children

    init {
        viewModelScope.launch(Dispatchers.IO) {

            val storedScrollPositions =
                appModule().dataStore.restoreState(ChildrenDictionaryStateKeys.SCROLL_POSITIONS)

            val scrollPositions = storedScrollPositions.first()

            withContext(Dispatchers.Main) {
                childrenState.setValue(scrollPositions ?: 0)
            }
        }
    }

    fun storeState(scrollPosition: Int) {
        childrenState.value = scrollPosition
    }

    fun onLanguageChanged(langPair: LanguagePair) {
        _children.value = ChildrenDictionaryAdapter(ChildrenDatasource().loadChildren(context, langPair))
    }

    private fun appModule() = getApplication<App>().appModule()

    fun store() {
        appModule().dataStore.saveState(
            ChildrenDictionaryStateKeys.SCROLL_POSITIONS,
            childrenState.value!!
        )
    }

    override fun onCleared() {
        store()
    }
}