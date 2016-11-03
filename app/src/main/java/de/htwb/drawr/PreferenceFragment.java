package de.htwb.drawr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Lao on 03.11.2016.
 */

public class PreferenceFragment extends Fragment {

    private LinearLayout userList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        userList = (LinearLayout) view.findViewById(R.id.preference_user_list);

        String[] testArray = new String[30];
        for(int i = 0; i < testArray.length; i++) {
            testArray[i] = "User"+(i+1);

            View v = inflater.inflate(R.layout.list_item_user_preference, null, false);
            ((TextView)v.findViewById(R.id.list_item_user_preference_name)).setText(testArray[i]);
            userList.addView(v);
        }
        return view;
    }
}
