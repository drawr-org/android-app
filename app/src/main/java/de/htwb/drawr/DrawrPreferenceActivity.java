package de.htwb.drawr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.*;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.htwb.drawr.util.SessionUtil;

import java.net.HttpURLConnection;

/**
 * Created by laokoon on 12/21/16.
 */
public class DrawrPreferenceActivity extends PreferenceActivity {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_HOST_URL = "host_url";
    public static final String KEY_HOST_PORT = "port";

    public static final String DUMMY_SESSION_ID = "__test__";

    private final DrawrPreferenceFragment fragment;

    public DrawrPreferenceActivity() {
        fragment = new DrawrPreferenceFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.preferences);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment).commit();

        TextView textView = new TextView(this);
        textView.setText(getString(R.string.version)+": "+BuildConfig.VERSION_NAME);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM|Gravity.CENTER;
        ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
        rootView.addView(textView, params);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fragment.checkHostSetting();
        fragment.checkPortSetting();
    }

    public static final class DrawrPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);

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
                            Toast.makeText(getActivity(), reference, Toast.LENGTH_LONG).show();
                        }
                    });

                    return true;
                }
            });

            checkPortSetting();
        }

        private void checkHostSetting() {
            String host = ((EditTextPreference)findPreference(KEY_HOST_URL)).getText();
            if(host != null && host.startsWith("http://")) {
                host = host.replace("http://", "");
            }
            ((EditTextPreference)findPreference(KEY_HOST_URL)).setText(host);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putString(KEY_HOST_URL, host).commit();
        }

        private void checkPortSetting() {
            String port = ((EditTextPreference)findPreference(KEY_HOST_PORT)).getText();
            int intValue = 0;
            if(!(port == null || port.isEmpty())) {
                intValue = Integer.parseInt(port);
            }
            if(intValue < 1 || intValue > 65535) {
                intValue = 3000;
            }
            ((EditTextPreference)findPreference(KEY_HOST_PORT)).setText(Integer.toString(intValue));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putString(KEY_HOST_PORT, Integer.toString(intValue)).commit();
        }
    }
}
