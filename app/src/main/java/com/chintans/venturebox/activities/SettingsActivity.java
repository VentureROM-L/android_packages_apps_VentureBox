package com.chintans.venturebox.activities;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chintans.venturebox.R;
import com.chintans.venturebox.helpers.SettingsHelper;

public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(getResources().getColor(R.color.toolbar_tint));

        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpToFromChild(SettingsActivity.this,
                        IntentCompat.makeMainActivity(new ComponentName(SettingsActivity.this,
                                MainActivity.class)));
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener{
        private SettingsHelper mSettingsHelper;
        private ListPreference mCheckTime;
        private CheckBoxPreference mCheckGapps;
        private ListPreference mGappsType;
        private CheckBoxPreference mDeviceSelect;


        /* Empty constructor */
        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupPreferencesScreen();

            mSettingsHelper = new SettingsHelper(getActivity());
            mCheckTime = (ListPreference) findPreference(SettingsHelper.PROPERTY_CHECK_TIME);
            mCheckGapps = (CheckBoxPreference) findPreference(SettingsHelper.PROPERTY_CHECK_GAPPS);
            mGappsType = (ListPreference) findPreference(SettingsHelper.PROPERTY_GAPPS_TYPE);
            mDeviceSelect = (CheckBoxPreference) findPreference(SettingsHelper.PROPERTY_DEVICE_SELECT);

            mCheckTime.setValue(String.valueOf(mSettingsHelper.getCheckTime()));
            mCheckGapps.setChecked(mSettingsHelper.getCheckGapps());
            mGappsType.setValue(String.valueOf(mSettingsHelper.getGappsType()));
            mDeviceSelect.setChecked(mSettingsHelper.getDeviceSelect());

            updateSummaries();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SettingsHelper.PROPERTY_CHECK_TIME.equals(key)) {
                //Utils.setAlarm(getActivity(), mSettingsHelper.getCheckTime(), false, true);
            }
            if (SettingsHelper.PROPERTY_GAPPS_TYPE.equals(key)) {
                updateSummaries();
            }
        }

        private void updateSummaries() {
            final CharSequence gappsType = mGappsType.getEntry();
            if (gappsType != null) {
                mGappsType.setSummary(gappsType);
            } else {
                mGappsType.setSummary(R.string.settings_gappstype_summary);
            }
        }

        private void setupPreferencesScreen() {
            // Add 'general' preferences.
            addPreferencesFromResource(R.xml.settings_preferences);
        }
    }

}
