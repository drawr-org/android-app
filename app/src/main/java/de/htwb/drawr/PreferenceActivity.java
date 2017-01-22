package de.htwb.drawr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.widget.Toast;
import de.htwb.drawr.util.SessionUtil;

import java.net.HttpURLConnection;

/**
 * Created by laokoon on 12/21/16.
 */
public class PreferenceActivity extends android.preference.PreferenceActivity {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_HOST_URL = "host_url";
    public static final String KEY_HOST_PORT = "port";

    public static final String DUMMY_SESSION_ID = "__test__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
        setTitle(R.string.preferences);

        Preference button = findPreference("test_connection");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do
                String host = ((EditTextPreference)findPreference(KEY_HOST_URL)).getText();
                String port = ((EditTextPreference)findPreference(KEY_HOST_PORT)).getText();
                SessionUtil.validateSessionAtHost(DUMMY_SESSION_ID, host, port, new SessionUtil.AsyncWaiterListener<Integer>() {
                    @Override
                    public void resultDelivered(Integer result) {
                        int reference;
                        if(result == HttpURLConnection.HTTP_OK) {
                            reference = R.string.connection_successful;
                        } else {
                            reference = R.string.connection_failed;
                        }
                        Toast.makeText(PreferenceActivity.this, reference, Toast.LENGTH_LONG).show();
                    }
                });

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkHostSetting();
        checkPortSetting();
    }

    private void checkHostSetting() {
        String host = ((EditTextPreference)findPreference(KEY_HOST_URL)).getText();
        if(host.startsWith("http://")) {
            host = host.replace("http://", "");
        }
        ((EditTextPreference)findPreference(KEY_HOST_URL)).setText(host);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(KEY_HOST_URL, host).commit();
    }

    private void checkPortSetting() {
        String port = ((EditTextPreference)findPreference(KEY_HOST_PORT)).getText();
        int intValue = 0;
        if(!port.isEmpty()) {
            intValue = Integer.parseInt(port);
        }
        if(intValue < 1 || intValue > 65535) {
            intValue = 3000;
        }
        ((EditTextPreference)findPreference(KEY_HOST_PORT)).setText(Integer.toString(intValue));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(KEY_HOST_PORT, Integer.toString(intValue)).commit();
    }
}
