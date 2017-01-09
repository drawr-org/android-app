package de.htwb.drawr;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import android.webkit.WebViewClient;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import de.htwb.drawr.util.DrawingUtil;
import de.htwb.drawr.view.DrawingDialog;
import de.htwb.drawr.view.OnDialogClosedListener;
import de.htwb.drawr.web.DrawrChromeClient;

/**
 * Created by Lao on 03.11.2016.
 */

public class CanvasFragment extends Fragment implements OnDialogClosedListener {

    private WebView webView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_pen;
    private FloatingActionButton fab_fullscreen;
    private FloatingActionButton fab_clear;
    private boolean menusShown = true;
    private DrawrChromeClient drawrChromeClient;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);
        webView = (WebView)view.findViewById(R.id.canvas_web_view);

        drawrChromeClient = new DrawrChromeClient(getActivity(), webView);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(fab_menu.isOpened()) {
                    fab_menu.close(true);
                }
                return false;
            }
        });

        fab_menu = (FloatingActionMenu) view.findViewById(R.id.canvas_fab_menu);
        fab_pen = (FloatingActionButton) view.findViewById(R.id.pen_settings);
        fab_fullscreen = (FloatingActionButton)view.findViewById(R.id.toggle_fullscreen);
        fab_clear = (FloatingActionButton)view.findViewById(R.id.clear_canvas);

        sharedPreferences =  getActivity().getSharedPreferences(DrawingDialog.DRAWING_SHARED_PREF_KEY, Context.MODE_PRIVATE);

        fab_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        fab_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenus(!menusShown);
                if(menusShown) {
                    fab_fullscreen.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_fullscreen_white_24dp));
                    fab_fullscreen.setLabelText(getActivity().getResources().getString(R.string.fullscreen));
                } else {
                    fab_fullscreen.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_fullscreen_exit_white_24dp));
                    fab_fullscreen.setLabelText(getActivity().getResources().getString(R.string.exit_fullscreen));
                }
            }
        });

        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawrChromeClient.callJavaScript("myCanvas.clearCanvas()");
            }
        });

        setFabColor(sharedPreferences.getInt(DrawingDialog.SHARED_PREF_KEY_COLOR, DrawingDialog.SHARED_PREF_DEFAULT_COLOR));

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
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            } else {
                int mUIFlag =  View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                getActivity().getWindow().getDecorView()
                        .setSystemUiVisibility(mUIFlag);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
            webView.requestLayout();
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
        int intColor =  sharedPreferences.getInt(DrawingDialog.SHARED_PREF_KEY_COLOR,
                DrawingDialog.SHARED_PREF_DEFAULT_COLOR);
        setFabColor(intColor);
        drawrChromeClient.callJavaScript("updateOptions()");
    }

    private void setFabColor(int color) {
        int withAlpha = color;
        withAlpha |= (255 << 24);
        fab_pen.setColorNormal(withAlpha);
        fab_pen.setColorRipple(withAlpha);
        fab_pen.setColorPressed(withAlpha);
        fab_pen.invalidate();
    }
}
