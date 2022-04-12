package digital.cesko.movapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _fromUa = MutableLiveData<Boolean>(true)

    val fromUa: LiveData<Boolean>
        get() = _fromUa

    fun setFromUa(fromUa: Boolean) {
        _fromUa.value = fromUa
    }
}