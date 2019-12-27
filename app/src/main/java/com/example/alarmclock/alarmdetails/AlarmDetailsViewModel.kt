package com.example.alarmclock.alarmdetails

import androidx.lifecycle.*
import com.example.alarmclock.database.AlarmEntitiy
import com.example.alarmclock.database.AlarmsRepository
import kotlinx.coroutines.launch

class AlarmDetailsViewModel(private val repo: AlarmsRepository, id: Long) : ViewModel() {

    private val _alarmEntity = MutableLiveData<AlarmEntitiy>()
    val alarmEntitiy: LiveData<AlarmEntitiy>
        get() = _alarmEntity


    init {
        viewModelScope.launch {
            _alarmEntity.value = repo.getById(id) ?: AlarmEntitiy(label = "")
        }
    }

    private val _confirmRes = MutableLiveData<Res>()
    val confirmRes: LiveData<Res>
        get() = _confirmRes

    fun confirmClick() {
        _confirmRes.value =
            when {
                _alarmEntity.value?.label.isNullOrBlank() -> Res.EmptyLabel
                else -> Res.OK
            }
    }

    fun onConfirmStopClicking() {
        _confirmRes.value = null
    }

    fun confirmChanges() {
        viewModelScope.launch {
            repo.insert(alarmEntitiy.value ?: error("unexpected null, debug needed"))
        }
    }

}


enum class Res {
    EmptyLabel,
    OK
}


class AlarmDetailsViewModelFactory(
    private val repo: AlarmsRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmDetailsViewModel::class.java)) {
            return AlarmDetailsViewModel(repo, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
