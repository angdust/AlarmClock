package com.example.alarmclock.alarmlist


import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import com.example.alarmclock.R
import com.example.alarmclock.alarmservice.AlarmService
import com.example.alarmclock.alarmservice.makePending
import com.example.alarmclock.database.AlarmEntitiy
import com.example.alarmclock.database.AlarmsRepository
import com.example.alarmclock.databinding.FragmentAlarmListBinding
import java.util.*

class AlarmListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application
        val repository = AlarmsRepository(application)
        val factory = AlarmViewModelFactory(repository)
        val viewModel = ViewModelProviders.of(this, factory).get(AlarmListViewModel::class.java)

        val binding: FragmentAlarmListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_list, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        val adapter = AlarmListAdapter(
            clickListener = ClickListener {
                viewModel.pickAlarm(it)
            },
            checkChangedListener = CheckChangedListener { alarm, bool ->
                alarm.enabled = bool
                viewModel.updateAlarm(alarm)
            }
        )

        viewModel.alarms.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            Log.d("List", it.toString())
            binding.noAlarmsText.isVisible = it.isNullOrEmpty()
        })

        binding.alarmListRecycler.adapter = adapter
        binding.alarmListFab.setOnClickListener {
            this.findNavController().navigate(
                AlarmListFragmentDirections.actionListDestToDetalilsDest(0L)
            )
        }

        viewModel.alarm.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(AlarmListFragmentDirections.actionListDestToDetalilsDest(it.id))
                viewModel.stopPicking()
            }
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean =
                false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewModel.delete(viewHolder.adapterPosition)
                viewModel.getByPos(viewHolder.adapterPosition)?.let { removeAlarm(it) }
            }
        }).attachToRecyclerView(binding.alarmListRecycler)

        return binding.root
    }


    private fun removeAlarm(alarmEntity: AlarmEntitiy) {
        val manager = ContextCompat.getSystemService(requireContext(), AlarmManager::class.java)
        manager?.cancel(makePending(requireContext(), alarmEntity))
    }

}
