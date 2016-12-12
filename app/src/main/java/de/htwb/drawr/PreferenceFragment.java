package de.htwb.drawr;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import com.google.zxing.WriterException;

/**
 * Created by Lao on 03.11.2016.
 */

public class PreferenceFragment extends Fragment {

    private static final String DUMMY_SESSION_ID = "1337";

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
        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.preference_qr_view);
            Resources r = getActivity().getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
            QRGEncoder encoder = new QRGEncoder(DUMMY_SESSION_ID, null, QRGContents.Type.TEXT, Math.round(px));
            ((TextView)view.findViewById(R.id.preference_session_id)).setText(DUMMY_SESSION_ID);

            imageView.setImageBitmap(encoder.encodeAsBitmap());
        } catch (WriterException e) {
            Log.e("PreferenceFragment", "Writer Exception for QRCode thrown: ", e);
        }

        return view;
    }
}
