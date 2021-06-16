package morales.david.android.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import morales.david.android.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

}