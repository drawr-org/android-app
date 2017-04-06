package de.htwb.drawr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import com.google.zxing.WriterException;
import com.google.zxing.common.StringUtils;
import de.htwb.drawr.util.UriUtil;

/**
 * Created by Lao on 03.11.2016.
 */

public class ShareFragment extends Fragment {

    private LinearLayout userList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.preference_qr_view);
        TextView textView = (TextView) view.findViewById(R.id.preference_session_id);
        final String sessionId = getArguments().getString(MainActivity.EXTRAS_KEY_SESSION_ID, "");
        if(sessionId.isEmpty()) {
            textView.setText(R.string.offline_session_id);
            imageButton.setVisibility(View.GONE);
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String host = sharedPreferences.getString(DrawrPreferenceActivity.KEY_HOST_URL, "");
            String port = sharedPreferences.getString(DrawrPreferenceActivity.KEY_HOST_PORT, "");
            final Uri uri = UriUtil.createUri(host, port, sessionId);
            textView.setText(sessionId);
            try {
                Resources r = getActivity().getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
                QRGEncoder encoder = new QRGEncoder(uri.toString(), null, QRGContents.Type.TEXT, Math.round(px));
                ((TextView) view.findViewById(R.id.preference_session_id)).setText(sessionId);

                imageButton.setImageBitmap(encoder.encodeAsBitmap());
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sessionId);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });
            } catch (WriterException e) {
                Log.e("ShareFragment", "Writer Exception for QRCode thrown: ", e);
            }
        }
        return view;
    }
}
