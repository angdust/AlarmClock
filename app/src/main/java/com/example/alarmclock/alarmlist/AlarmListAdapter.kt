package com.example.alarmclock.alarmlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.database.AlarmEntitiy
import com.example.alarmclock.databinding.ItemAlarmBinding

class AlarmListAdapter(
    private val clickListener: ClickListener,
    private val checkChangedListener: CheckChangedListener
) :
    ListAdapter<AlarmEntitiy, AlarmListAdapter.ViewHolder>(DiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, checkChangedListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: ClickListener,
            checkChangedListener: CheckChangedListener,
            item: AlarmEntitiy
        ) {
            binding.entity = item
            binding.clickListener = clickListener
            binding.itemAlarmEnabledSwitch.setOnCheckedChangeListener { _, bool ->
                checkChangedListener.action(item, bool)
            }
            binding.executePendingBindings()
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAlarmBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class ClickListener(val action: (entity: AlarmEntitiy) -> Unit) {
    fun onClick(entity: AlarmEntitiy) = action(entity)
}

class CheckChangedListener(val action: (entity: AlarmEntitiy, bool: Boolean) -> Unit) {
    fun onCheck(entity: AlarmEntitiy, bool: Boolean) = action(entity, bool)
}

class DiffCallback : DiffUtil.ItemCallback<AlarmEntitiy>() {
    override fun areItemsTheSame(oldItem: AlarmEntitiy, newItem: AlarmEntitiy): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AlarmEntitiy, newItem: AlarmEntitiy): Boolean =
        false

}