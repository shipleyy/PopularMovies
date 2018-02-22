package example.android.com.popularmovies.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import example.android.com.popularmovies.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.pref_movies);
  }
}
