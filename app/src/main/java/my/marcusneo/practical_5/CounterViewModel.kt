package my.marcusneo.practical_5

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel(){
    val _counter: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>() //create and instance of mutableLiveData
    }

    init {
        _counter.value = 0
        Log.d("ViewModel", "VM initialized")
    }

    override fun onCleared(){
        super.onCleared()
        Log.d("ViewModel", "VM cleared")
    }

    fun increment(){
        //_counter++
        _counter.value = _counter.value?.plus(1)
    }

    fun decrement(){
        //_counter--
        _counter.value = _counter.value?.minus(1)
    }
}