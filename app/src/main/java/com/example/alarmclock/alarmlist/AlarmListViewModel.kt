package com.example.alarmclock.alarmlist

import androidx.lifecycle.*
import com.example.alarmclock.database.AlarmEntitiy
import com.example.alarmclock.database.AlarmsRepository
import kotlinx.coroutines.launch

class AlarmListViewModel(private val repo: AlarmsRepository) : ViewModel() {


    val alarms = repo.alarms


    private val _alarm = MutableLiveData<AlarmEntitiy>()
    val alarm: LiveData<AlarmEntitiy>
        get() = _alarm

    fun pickAlarm(alarmEntitiy: AlarmEntitiy) {
        _alarm.value = alarmEntitiy
    }

    fun stopPicking() {
        _alarm.value = null
    }

    fun updateAlarm(alarmEntitiy: AlarmEntitiy) {
        viewModelScope.launch {
            repo.update(alarmEntitiy)
        }
    }

    fun delete(pos: Int) {
        viewModelScope.launch {
            alarms.value?.get(pos)?.let {
                repo.delete(it)
            }
        }
    }

    fun getByPos(pos: Int) = alarms.value?.get(pos)
}


class AlarmViewModelFactory(private val repo: AlarmsRepository) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmListViewModel::class.java)) {
            return AlarmListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
