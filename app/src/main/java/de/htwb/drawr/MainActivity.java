package de.htwb.drawr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.balysv.materialmenu.MaterialMenuDrawable;


public class MainActivity extends AppCompatActivity {

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
                    showFragment(canvasFragment);
                } else if(getString(R.string.preferences).equals(caption)) {
                    showFragment(preferenceFragment);
                } else if(getString(R.string.quit).equals(caption)) {
                    finish();
                }
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        showFragment(canvasFragment);
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
