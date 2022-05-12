package cz.movapp.app.ui.children

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.movapp.app.App
import cz.movapp.app.adapter.ChildrenAdapter
import cz.movapp.app.appModule
import cz.movapp.app.data.ChildrenDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChildrenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    val childrenState = MutableLiveData<Int?>()

    private val _children = MutableLiveData<ChildrenAdapter>().apply {
        value = ChildrenAdapter(ChildrenDatasource().loadChildren(context))
    }

    val children: MutableLiveData<ChildrenAdapter> = _children

    init {
        viewModelScope.launch(Dispatchers.IO) {

            val storedScrollPositions =
                appModule().dataStore.restoreState(ChildrenStateKeys.SCROLL_POSITIONS)

            val scrollPositions = storedScrollPositions.first()

            withContext(Dispatchers.Main) {
                childrenState.setValue(scrollPositions)
            }
        }
    }

    fun storeState(scrollPosition: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            childrenState.value = scrollPosition
        }
    }

    private fun appModule() = getApplication<App>().appModule()

    override fun onCleared() {
        appModule().dataStore.saveState(
            ChildrenStateKeys.SCROLL_POSITIONS,
            childrenState.value!!
        )
    }
}