package com.rayhan.githubuser.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import com.rayhan.githubuser.R
import com.rayhan.githubuser.alarm.ReminderReceiver

class MyPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var REMINDER: String
    private lateinit var LANGUAGE: String

    private lateinit var reminderPreference: SwitchPreference
    private lateinit var languagePreference: Preference

    private lateinit var reminderReceiver: ReminderReceiver

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        reminderReceiver = ReminderReceiver()
        init()
        setSummarize()

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

    private fun setSummarize() {
        val sh = preferenceManager.sharedPreferences
        reminderPreference.isChecked = sh.getBoolean(REMINDER, false)
        if (reminderPreference.isChecked) reminderPreference.setIcon(R.drawable.ic_baseline_notifications_active_24)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        if (key == REMINDER) {
            reminderPreference.isChecked = sharedPreferences.getBoolean(REMINDER, false)
            if (reminderPreference.isChecked) {
                // disertai set repeating alarm
                reminderReceiver.setRepeatingAlarm(requireContext())
                reminderPreference.setIcon(R.drawable.ic_baseline_notifications_active_24)
                Snackbar.make(requireView(), requireContext().resources.getString(R.string.set_reminder_label), Snackbar.LENGTH_SHORT).show()
            }
            else {
                // disertai unset repeating alarm
                reminderReceiver.cancelAlarm(requireContext())
                reminderPreference.setIcon(R.drawable.ic_baseline_notifications_24)
                Snackbar.make(requireView(), requireContext().resources.getString(R.string.disable_reminder_label), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}