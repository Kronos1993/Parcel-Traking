package com.kronos.parcel.tracking.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.kronos.parcel.tracking.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_settings, rootKey)
    }
}