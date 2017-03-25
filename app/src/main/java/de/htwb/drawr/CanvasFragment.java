package de.htwb.drawr;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;

import android.webkit.WebViewClient;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import de.htwb.drawr.util.DrawingUtil;
import de.htwb.drawr.util.PenSettings;
import de.htwb.drawr.view.DrawingDialog;
import de.htwb.drawr.view.OnDialogClosedListener;
import de.htwb.drawr.web.DrawrChromeClient;

import static de.htwb.drawr.MainActivity.EXTRAS_KEY_NEW_SESSION;
import static de.htwb.drawr.MainActivity.EXTRAS_KEY_ONLINE;
import static de.htwb.drawr.MainActivity.EXTRAS_KEY_SESSION_ID;

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
    private PenSettings penSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);
        webView = (WebView)view.findViewById(R.id.canvas_web_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                processExtras();
            }
        });

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

        penSettings = PenSettings.getInstance();

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
                updateFabFullscreenText();
            }
        });

        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawrChromeClient.callJavaScript("clearCanvas()");
            }
        });

        fab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menusShown = ((AppCompatActivity) getActivity()).getSupportActionBar().isShowing();
                updateFabFullscreenText();
            }
        });

        setFabColor(penSettings.getColor());

        ((AppCompatActivity) getActivity()).getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                menusShown = isVisible;
                updateFabFullscreenText();
            }
        });
        return view;
    }

    private void updateFabFullscreenText() {
        if(menusShown) {
            fab_fullscreen.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_fullscreen_white_24dp));
            fab_fullscreen.setLabelText(getActivity().getResources().getString(R.string.fullscreen));
        } else {
            fab_fullscreen.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_fullscreen_exit_white_24dp));
            fab_fullscreen.setLabelText(getActivity().getResources().getString(R.string.exit_fullscreen));
        }
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
        int intColor =  penSettings.getColor();
        setFabColor(intColor);
        fab_pen.setLabelText(DrawingUtil.getStringForTool(getActivity(), penSettings.getTool()));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawing_menu, menu);
        menu.findItem(R.id.menu_undo).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawrChromeClient.callJavaScript("undo()");
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void processExtras() {
        Bundle extras = getArguments();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = prefs.getString(DrawrPreferenceActivity.KEY_USERNAME, "");
        String host = prefs.getString(DrawrPreferenceActivity.KEY_HOST_URL, "");
        String port = prefs.getString(DrawrPreferenceActivity.KEY_HOST_PORT, "3000");
        boolean online = extras.getBoolean(EXTRAS_KEY_ONLINE, false);
        if(online) {
            boolean newSession = extras.getBoolean(EXTRAS_KEY_NEW_SESSION, false);
            if(newSession) {
                drawrChromeClient.callJavaScript("connectAndNewSession('"+username+"','"+host+"','"+port+"')");
            } else {
                String sessionId = extras.getString(EXTRAS_KEY_SESSION_ID, "");
                if(sessionId != null && sessionId.isEmpty()) {
                    Log.d("MainActivity", "processExtras: SessionId is empty!");
                    getActivity().finish();
                } else {
                    Log.d("CanvasFragment", "joining session...");
                    drawrChromeClient.callJavaScript("connectAndJoinSession('"+username+"','"+host+"','"+port+"','"+sessionId+"')");
                }
            }
        }
    }
}
