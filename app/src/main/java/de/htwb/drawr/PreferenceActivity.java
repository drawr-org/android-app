package de.htwb.drawr;

import android.os.Bundle;

/**
 * Created by laokoon on 12/21/16.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_HOST_URL = "host_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
