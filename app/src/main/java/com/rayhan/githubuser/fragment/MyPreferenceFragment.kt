package com.rayhan.githubuser.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.rayhan.githubuser.R
import com.rayhan.githubuser.alarm.ReminderReceiver

class MyPreferenceFragment : PreferenceFragmentCompat() {
    private lateinit var REMINDER: String
    private lateinit var LANGUAGE: String

    private lateinit var reminderPreference: SwitchPreference
    private lateinit var languagePreference: Preference

    private lateinit var reminderReceiver: ReminderReceiver

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        reminderReceiver = ReminderReceiver()
        init()

        reminderPreference.isChecked = reminderReceiver.isAlarmSet(requireContext(), ReminderReceiver.EXTRA_TYPE)
        reminderPreference.setOnPreferenceChangeListener { preference, newValue ->
            if(newValue as Boolean){
                reminderReceiver.setRepeatingAlarm(requireContext(), "09:00", ReminderReceiver.EXTRA_TYPE)
                reminderPreference.setIcon(R.drawable.ic_baseline_notifications_active_24)
                Snackbar.make(requireView(), requireContext().resources.getString(R.string.set_reminder_label), Snackbar.LENGTH_SHORT).show()
            } else {
                reminderReceiver.cancelAlarm(requireContext())
                reminderPreference.setIcon(R.drawable.ic_baseline_notifications_24)
                Snackbar.make(requireView(), requireContext().resources.getString(R.string.disable_reminder_label), Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        languagePreference.setOnPreferenceClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
            true
        }
    }

    private fun init () {
        REMINDER = resources.getString(R.string.key_reminder)
        LANGUAGE = resources.getString(R.string.key_language)

        reminderPreference = findPreference<SwitchPreference>(REMINDER) as SwitchPreference
        languagePreference = findPreference<Preference>(LANGUAGE) as Preference

    }
}