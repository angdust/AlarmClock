package com.example.alarmclock.alarmdetails


import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.alarmclock.R
import com.example.alarmclock.alarmdetails.Res.EmptyLabel
import com.example.alarmclock.alarmdetails.Res.OK
import com.example.alarmclock.alarmservice.AlarmService
import com.example.alarmclock.alarmservice.REQUEST_CODE
import com.example.alarmclock.alarmservice.makePending
import com.example.alarmclock.database.AlarmEntitiy
import com.example.alarmclock.database.AlarmsRepository
import com.example.alarmclock.databinding.FragmentAlarmDetailsBinding
import java.util.*
import kotlin.math.min


class AlarmDetailsFragment : Fragment() {

    private lateinit var viewModel: AlarmDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val repository = AlarmsRepository(application)
        val id = AlarmDetailsFragmentArgs.fromBundle(arguments!!).alarmId
        val factory = AlarmDetailsViewModelFactory(repository, id)
        viewModel = ViewModelProviders.of(this, factory).get(AlarmDetailsViewModel::class.java)


        val binding: FragmentAlarmDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_details, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.confirmRes.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    EmptyLabel -> Toast.makeText(
                        requireContext(),
                        getString(R.string.empty_label_toast),
                        Toast.LENGTH_LONG
                    ).show()
                    OK -> {
                        viewModel.confirmChanges()
                        this.findNavController().navigateUp()
                    }
                }
                viewModel.onConfirmStopClicking()
            }
        })

        viewModel.alarmEntitiy.observe(viewLifecycleOwner, Observer { alarm ->
            binding.alarmDetailsTimePicker.apply {
                hour = alarm.hours
                minute = alarm.minutes
                setOnTimeChangedListener { _, hourOfDay, minute ->
                    alarm.hours = hourOfDay
                    alarm.minutes = minute
                }
            }

            binding.alarmDetailsProbPicker.apply {
                maxValue = 100
                minValue = 1
                setOnValueChangedListener { _, _, newVal ->
                    alarm.probability = newVal
                }
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.confirm_changes) {
            viewModel.confirmClick()
            setAlarm(viewModel.alarmEntitiy.value!!)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAlarm(alarmEntity: AlarmEntitiy) {
        val time = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, alarmEntity.hours)
            set(Calendar.MINUTE, alarmEntity.minutes)
        }.timeInMillis

        val manager = getSystemService(requireContext(), AlarmManager::class.java)

        manager?.cancel(makePending(requireContext(), alarmEntity))
        manager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            makePending(requireContext(), alarmEntity)
        )
    }

}
