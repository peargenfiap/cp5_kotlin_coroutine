package br.com.fiapcorrida

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class HorseRaceViewModel : ViewModel() {

    var corridaIsFinished = MutableLiveData<Int>()

    suspend fun calculatePoint(sequenceNumber: Int) : Int {
        return withContext(Dispatchers.Default) {
            val result = async {
                delay(1500)
                calculate(sequenceNumber)
            }.await()
            withContext(Dispatchers.Main) {
                corridaIsFinished.value = result
            }
            result
        }
    }

    private fun calculate(sequenceNumber: Int): Int {
        if (sequenceNumber <= 1) {
            return sequenceNumber
        } else {
            return calculate(sequenceNumber - 1) +
                    calculate(sequenceNumber -2)
        }
    }
}