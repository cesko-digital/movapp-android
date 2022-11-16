package cz.movapp.app.ui.children

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cz.movapp.app.data.FairyTale
import cz.movapp.app.data.FairyTalesDatasource
import cz.movapp.app.data.MetaFairyTale

class ChildrenFairyTalesViewModel(application: Application) : AndroidViewModel(application) {

    private var context = application.applicationContext

    private val _fairyTales = MutableLiveData<ChildrenFairyTalesAdapter>().apply {
        value = ChildrenFairyTalesAdapter(FairyTalesDatasource(application.applicationContext).loadFairyTales())
    }

    var fairyTales: MutableLiveData<ChildrenFairyTalesAdapter> = _fairyTales

    fun getFairyTaleAdapter(slug: String, isReversed: Boolean): ChildrenFairyTalePlayerAdapter {
        return ChildrenFairyTalePlayerAdapter(FairyTalesDatasource(context).getFairyTale(slug)!!, isReversed)
    }

    fun getMetaFairyTale(slug: String): MetaFairyTale {
        return FairyTalesDatasource(context).getMetaFairyTale(slug)!!
    }

    fun getFairyTale(slug: String): FairyTale {
        return FairyTalesDatasource(context).getFairyTale(slug)!!
    }

    fun getFairyTaleDrawable(slug: String): Drawable {
        return FairyTalesDatasource(context).getFairyTaleDrawables()[slug]!!
    }

    override fun onCleared() {
        super.onCleared()
        context = null
    }
}