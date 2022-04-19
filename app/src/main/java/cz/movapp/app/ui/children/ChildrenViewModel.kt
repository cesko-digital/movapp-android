package cz.movapp.app.ui.children

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cz.movapp.app.adapter.ChildrenAdapter
import cz.movapp.app.data.ChildrenDatasource

class ChildrenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _children = MutableLiveData<ChildrenAdapter>().apply {
        value = ChildrenAdapter(context, ChildrenDatasource().loadChildren(context))
    }

    val children: MutableLiveData<ChildrenAdapter> = _children
}