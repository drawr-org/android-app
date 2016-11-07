package de.htwb.drawr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Lao on 03.11.2016.
 */

public class CanvasFragment extends Fragment {

    private WebView webView;
    private FloatingActionButton fab;
    private boolean menusShown = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);
        webView = (WebView)view.findViewById(R.id.canvas_web_view);
        fab = (FloatingActionButton) view.findViewById(R.id.canvas_fab);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            private float pointX;
            private float pointY;
            private int tolerance = 50;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance < event.getX();
                        boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance < event.getY();
                        if(sameX && sameY){
                            //The user "clicked" certain point in the screen or just returned to the same position an raised the finger
                            showMenus(!menusShown);
                        }
                }
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void showMenus(boolean visible) {
        if(visible != menusShown) {
            menusShown = visible;
            if (menusShown) {
                fab.show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            } else {
                fab.hide();
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.loadUrl("https://google.de");
    }
}
