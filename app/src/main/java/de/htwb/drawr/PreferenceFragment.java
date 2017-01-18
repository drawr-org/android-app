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

    private LinearLayout userList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.preference_qr_view);
        TextView textView = (TextView) view.findViewById(R.id.preference_session_id);
        String sessionId = getArguments().getString(MainActivity.EXTRAS_KEY_SESSION_ID, "");
        if(sessionId.isEmpty()) {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        } else {
            textView.setText(sessionId);
            try {
                Resources r = getActivity().getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                QRGEncoder encoder = new QRGEncoder(sessionId, null, QRGContents.Type.TEXT, Math.round(px));
                ((TextView) view.findViewById(R.id.preference_session_id)).setText(sessionId);

                imageView.setImageBitmap(encoder.encodeAsBitmap());
            } catch (WriterException e) {
                Log.e("PreferenceFragment", "Writer Exception for QRCode thrown: ", e);
            }
        }
        return view;
    }
}
