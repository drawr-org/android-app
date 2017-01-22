package de.htwb.drawr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.balysv.materialmenu.MaterialMenuDrawable;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_ONLINE = "online";
    public static final String EXTRAS_KEY_NEW_SESSION = "new_session";
    public static final String EXTRAS_KEY_SESSION_ID = "session_id";


    private DrawerLayout mDrawerLayout;
    private String[] drawerCaptions;
    private ListView mDrawerList;

    private CanvasFragment canvasFragment = new CanvasFragment();
    private PreferenceFragment preferenceFragment = new PreferenceFragment();

    private MaterialMenuDrawable materialMenu;
    private boolean isDrawerOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        canvasFragment.setArguments(getIntent().getExtras());
        preferenceFragment.setArguments(getIntent().getExtras());
        drawerCaptions = getResources().getStringArray(R.array.nav_drawer_list_array);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon(materialMenu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrawerOpened) {
                    mDrawerLayout.closeDrawer(GravityCompat.START, true);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START, true);
                }
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                materialMenu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpened ? 2 - slideOffset : slideOffset
                );
                if(!isDrawerOpened) {
                    canvasFragment.showMenus(true);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        mDrawerList.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                drawerCaptions));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String caption = drawerCaptions[position];
                if(getString(R.string.canvas).equals(caption)) {
                    showCanvas();
                } else if(getString(R.string.preferences).equals(caption)) {
                    showPreferences();
                } else if(getString(R.string.quit).equals(caption)) {
                    leaveSession();
                }
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, canvasFragment)
                .commit();
    }

    private void showCanvas() {
        showFragment(getSupportFragmentManager(), canvasFragment, preferenceFragment);
    }

    private void showPreferences() {
        showFragment(getSupportFragmentManager(), preferenceFragment, canvasFragment);
    }

    private static void showFragment(FragmentManager manager, Fragment fragmentA, Fragment fragmentB) {
        FragmentTransaction transaction = manager.beginTransaction();
        if(manager.getFragments()!= null && !manager.getFragments().contains(fragmentA)) {
            transaction.add(R.id.content_frame, fragmentA);
        }
        if(manager.getFragments() != null && !manager.getFragments().contains(fragmentB)) {
            transaction.add(R.id.content_frame, fragmentB);
        }

        transaction.hide(fragmentB);
        transaction.show(fragmentA);
        transaction.commit();
    }

    public void updateSessionId(String sessionId) {
        canvasFragment.getArguments().putString(EXTRAS_KEY_SESSION_ID, sessionId);
        preferenceFragment.getArguments().putString(EXTRAS_KEY_SESSION_ID, sessionId);
    }

    @Override
    public void onBackPressed() {
        leaveSession();
    }

    private void leaveSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.leaving_session_confirmation_title);
        builder.setMessage(R.string.leave_session_confimation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.show();
    }
}
