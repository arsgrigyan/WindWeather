package com.southernsunrise.windweather

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.prefs, rootKey)
            /* val pref = findPreference<ListPreference>("TEMPERATURE_UNIT")
             val value = pref?.entry
             pref?.summary = value
             */
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(
                context
            )
            onSharedPreferenceChanged(sharedPrefs, "TEMPERATURE_UNIT");
            onSharedPreferenceChanged(sharedPrefs, "PRESSURE_UNIT");
            onSharedPreferenceChanged(sharedPrefs, "WIND_UNIT");


        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(p0: SharedPreferences, key: String) {
            val pref: Preference? = findPreference(key)
            if (pref is ListPreference) {
                val listPref: ListPreference = pref
                listPref.summary = listPref.entry

            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.navigateUpTo(parentActivityIntent)
    }
}






