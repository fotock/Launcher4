/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sanfriend.launcher4;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preferences);

            SwitchPreference rotatePref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            rotatePref.setPersistent(false);

            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            Bundle value = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY, extras);
            rotatePref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));
            rotatePref.setOnPreferenceChangeListener(this);

            ListPreference textColorPref = (ListPreference) findPreference(
                    Utilities.ALLAPP_TEXTCOLOR_PREFERENCE_KEY);
            textColorPref.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle extras = new Bundle();
            String method = "";
            if (preference.getKey().equals(Utilities.ALLOW_ROTATION_PREFERENCE_KEY)) {
                extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
                method = LauncherSettings.Settings.METHOD_SET_BOOLEAN;
            } else if (preference.getKey().equals(Utilities.ALLAPP_TEXTCOLOR_PREFERENCE_KEY)){
                extras.putString(LauncherSettings.Settings.EXTRA_VALUE, (String) newValue);
                method = LauncherSettings.Settings.METHOD_SET_TEXTCOLOR;
            }

            getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    method,
                    preference.getKey(),
                    extras);
            return true;
        }

    }
}
