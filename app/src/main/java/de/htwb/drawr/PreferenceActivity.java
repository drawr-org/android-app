package de.htwb.drawr;

import android.os.Bundle;

/**
 * Created by laokoon on 12/21/16.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
