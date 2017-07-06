package com.repositoryworks.datarepository.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.activityAdapters.DrawerAdapter;
import com.repositoryworks.datarepository.fragments.GamesFragment;
import com.repositoryworks.datarepository.fragments.HomeFragment;
import com.repositoryworks.datarepository.fragments.MoviesFragment;
import com.repositoryworks.datarepository.fragments.MusicFragment;
import com.repositoryworks.datarepository.fragments.PlaceHolderFragment;
import com.repositoryworks.datarepository.utils.Constants;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
            MusicFragment.OnFragmentInteractionListener, MoviesFragment.OnFragmentInteractionListener,
            GamesFragment.OnFragmentInteractionListener, PlaceHolderFragment.OnFragmentInteractionListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.content_frame)
    FrameLayout mFrame;

    @BindView(R.id.left_drawer)
    ListView mDrawerList;

    @BindArray(R.array.drawer_list)
    String[] mDrawerListItems;

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    private TextView mTitle;
    private static DisplayMetrics mDisplayMetrics = new DisplayMetrics();
    public static SharedPreferences sSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        // Display width handy
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        // Get SharedPreferences
        sSharedPreferences = getSharedPreferences(Constants.APP_ACTIVITIES,MODE_PRIVATE);

        // ActionBar stuff
        setActionBar();

        // Load Home Screen content
        loadFragment(0);

        // Initialize the listener object for Open/Close Drawer
        final ActionBarDrawerToggle Toggler = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,
                R.string.drawer_open,R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        // Prepare the List of contents for Drawer
        DrawerAdapter DrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(DrawerAdapter);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((TextView) view).getText().toString().equals(getString(R.string.log_out))){
                    viewAlertDialog();
                }else{
                    loadFragment(position);
                }
            }
        });

        // Drawer handling
        mDrawerLayout.addDrawerListener(Toggler);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
                params.width = (int) (mDisplayMetrics.widthPixels/ 1.3);
                mDrawerList.setLayoutParams(params);
                Toggler.syncState();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            boolean logged_in = sSharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false);
            if(!logged_in){
                Intent intent = new Intent(this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * Load the fragment chosen from Drawer menu
     * @param position
     */
    private void loadFragment(int position){

        FragmentManager fragmentManager;
        Fragment fragment;

        if((fragment = getSupportFragmentManager().findFragmentByTag(mDrawerListItems[position])) != null){
            // No content required
        }else{
            fragment = whichFragment(position);
        }

        if(fragment != null){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,fragment,mDrawerListItems[position])
                    .addToBackStack(mDrawerListItems[position])
                    .commit();

            mDrawerList.setItemChecked(position,true);
            mTitle.setText(mDrawerListItems[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }else{
            fragment = new PlaceHolderFragment();

            // Pop BackStack fragments
            fragmentManager = getSupportFragmentManager();
            for(int i=0; i<fragmentManager.getBackStackEntryCount();++i){
                fragmentManager.popBackStack();
            }

            // Show the helper fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,fragment)
                    .commit();
            mTitle.setText(getResources().getString(R.string.error_title));
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /**
     * Find the fragment to load when loading for the first time
     * @param position
     * @return Fragment instance
     */
    @Nullable
    @org.jetbrains.annotations.Contract(pure = true)
    private Fragment whichFragment(int position){
        switch(mDrawerListItems[position]){
            case "Home":
                return HomeFragment.newInstance(position);
            case "Music":
                return MusicFragment.newInstance(position);
            case "Movies":
                return MoviesFragment.newInstance(position);
            case "Games":
                return GamesFragment.newInstance(position);
            case "Settings":
                return null;
            default:
                return null;
        }
    }

    /**
     * Set the action bar contents
     */
    private void setActionBar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.my_action_bar,null),
                new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK
        ));
        mTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title);
    }

    /**
     * Alert Dialog to log out
     */
    private void viewAlertDialog(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.confirmation));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sSharedPreferences.edit().clear().apply();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show
        alert.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about:
                Toast.makeText(this,"About",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.help:
                Toast.makeText(this,"Help",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}