package de.htwb.drawr;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private static String[] drawerCaptions;
    private ListView mDrawerList;

    private CanvasFragment canvasFragment = new CanvasFragment();
    private PreferenceFragment preferenceFragment = new PreferenceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerCaptions = getResources().getStringArray(R.array.nav_drawer_list_array);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

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
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
