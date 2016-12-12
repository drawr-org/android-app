package de.htwb.drawr;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.htwb.drawr.view.DrawingDialog;
import de.htwb.drawr.view.OnDialogClosedListener;
import de.htwb.drawr.web.DrawrChromeClient;

/**
 * Created by Lao on 03.11.2016.
 */

public class CanvasFragment extends Fragment implements OnDialogClosedListener {

    private WebView webView;
    private FloatingActionButton fab;
    private boolean menusShown = true;
    private DrawrChromeClient drawrChromeClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);
        webView = (WebView)view.findViewById(R.id.canvas_web_view);

        drawrChromeClient = new DrawrChromeClient(getActivity(), webView);

        fab = (FloatingActionButton) view.findViewById(R.id.canvas_fab);
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
                showDialog();
            }
        });

        return view;
    }

    void showDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new DrawingDialog();
        ((DrawingDialog)newFragment).setOnDialogClosedListener(this);
        newFragment.show(ft, "dialog");
    }

    public void showMenus(boolean visible) {
        if(visible != menusShown) {
            menusShown = visible;
            if (menusShown) {
                getActivity().getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                fab.show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            } else {
                int mUIFlag =  View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                getActivity().getWindow().getDecorView()
                        .setSystemUiVisibility(mUIFlag);
                fab.hide();
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onDialogClosed() {
        drawrChromeClient.callJavaScript("updateOptions()");
    }
}
