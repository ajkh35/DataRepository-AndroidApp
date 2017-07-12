package com.repositoryworks.datarepository.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.repositoryworks.datarepository.MainActivity;
import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.activityAdapters.DrawerAdapter;
import com.repositoryworks.datarepository.fragments.DocumentsFragment;
import com.repositoryworks.datarepository.fragments.GamesFragment;
import com.repositoryworks.datarepository.fragments.HomeFragment;
import com.repositoryworks.datarepository.fragments.MoviesFragment;
import com.repositoryworks.datarepository.fragments.MusicFragment;
import com.repositoryworks.datarepository.fragments.PlaceHolderFragment;
import com.repositoryworks.datarepository.fragments.SettingsFragment;
import com.repositoryworks.datarepository.models.UserModel;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;
import com.theartofdev.edmodo.cropper.CropImage;


import java.io.IOException;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
            MusicFragment.OnFragmentInteractionListener, MoviesFragment.OnFragmentInteractionListener,
            GamesFragment.OnFragmentInteractionListener, PlaceHolderFragment.OnFragmentInteractionListener,
            DocumentsFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.content_frame)
    FrameLayout mFrame;

    @BindView(R.id.left_drawer)
    LinearLayout mDrawer;

    @BindView(R.id.drawer_list_view)
    ListView mDrawerList;

    @BindView(R.id.profile_pic)
    CircleImageView mProfilePic;

    @BindArray(R.array.drawer_list)
    String[] mDrawerListItems;

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    private TextView mTitle;
    private RelativeLayout mMyActionBar;

    public static SharedPreferences sSharedPreferences;

    private UserModel mUser;
    private DBManager mDBManager;
    private static final int FILE_REQUEST_CODE = 100;

    private int mFragmentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        // Get SharedPreferences
        sSharedPreferences = getSharedPreferences(Constants.APP_ACTIVITIES,MODE_PRIVATE);

        // Link to database
        mDBManager = new DBManager(this);

        // ActionBar stuff
        setActionBar();

        // Load Home Screen content
        loadFragment(mFragmentPosition);

        // Initialize the listener object for Open/Close Drawer
        final ActionBarDrawerToggle Toggler = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,
                R.string.drawer_open,R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                loadFragment(mFragmentPosition);
                super.onDrawerClosed(drawerView);
            }
        };

        // Prepare the List of contents for Drawer
        DrawerAdapter drawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = ButterKnife.findById(view,R.id.list_item_text);
                if(textView.getText().toString().equals(getString(R.string.log_out))){
                    viewAlertDialog();
                }else{
                    mFragmentPosition = position;
                    mTitle.setText(mDrawerListItems[position]);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setFragmentColor(position);
                    }
                    mDrawerLayout.closeDrawer(mDrawer);
                }
            }
        });

        // Drawer handling
        mDrawerLayout.addDrawerListener(Toggler);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mDrawer.getLayoutParams();
                params.width = (int) (MainActivity.sDisplayMetrics.widthPixels/ 1.35);
                mDrawer.setLayoutParams(params);
                setProfilePic();
                Toggler.syncState();
            }
        });

        // Set listener for profile pic
        handleProfilePicListeners();
    }

    /**
     * Return DBManager instance
     */
    public DBManager getDBManager(){
        return mDBManager;
    }

    /**
     * Configure the user profile pic
     */
    public void setProfilePic(){
        mDBManager.databaseOpenToRead();
        mUser = mDBManager.findUserByID(sSharedPreferences.getLong(Constants.CURRENT_USER_ID,-1));
        mProfilePic.setImageBitmap(mUser.getImageBitmap());
    }

    /**
     * Handle click on profile picture
     */
    private void handleProfilePicListeners(){

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFileChooser();
            }
        });
    }

    /**
     * Show the FileChooser
     */
    private void launchFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,"Select a file to add"),FILE_REQUEST_CODE);
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
     * @param position FragmentAdapter Item position
     */
    private void loadFragment(int position){

        FragmentManager fragmentManager;
        Fragment fragment;

        if((fragment = getSupportFragmentManager().findFragmentByTag(mDrawerListItems[position])) != null){
            Log.i(Constants.APP_TAG,fragment.getTag());
            if(Constants.USER_JUST_UPDATED && mDrawerListItems[position].equals(getString(R.string.settings))){
                Constants.USER_JUST_UPDATED = false;
                fragment = whichFragment(position);
            }
        }else{
            fragment = whichFragment(position);
        }

        if(fragment != null){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame,fragment,mDrawerListItems[position])
                    .addToBackStack(mDrawerListItems[position])
                    .commit();

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mDrawer.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mMyActionBar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            }
        }
    }

    /**
     * Find the fragment to load when loading for the first time
     * @param position Fragment adapter position
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
            case "Documents":
                return DocumentsFragment.newInstance(position);
            case "Settings":
                return SettingsFragment.newInstance(position,mUser);
            default:
                return null;
        }
    }

    /**
     * Set the action bar color with fragment change
     * @param position Fragment Position
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFragmentColor(int position){
        switch(mDrawerListItems[position]){
            case "Home":
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mMyActionBar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                mDrawer.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                break;
            case "Music":
                getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
                mMyActionBar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
                mToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
                mDrawer.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
                break;
            case "Movies":
                getWindow().setStatusBarColor(Color.MAGENTA);
                mMyActionBar.setBackgroundColor(Color.MAGENTA);
                mToolbar.setBackgroundColor(Color.MAGENTA);
                mDrawer.setBackgroundColor(Color.MAGENTA);
                break;
            case "Games":
                getWindow().setStatusBarColor(Color.RED);
                mMyActionBar.setBackgroundColor(Color.RED);
                mToolbar.setBackgroundColor(Color.RED);
                mDrawer.setBackgroundColor(Color.RED);
                break;
            case "Documents":
                getWindow().setStatusBarColor(Color.BLACK);
                mMyActionBar.setBackgroundColor(Color.BLACK);
                mToolbar.setBackgroundColor(Color.BLACK);
                mDrawer.setBackgroundColor(Color.BLACK);
                break;
            case "Settings":
                break;
            default:
                break;
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
        mMyActionBar = (RelativeLayout) getSupportActionBar().getCustomView().findViewById(R.id.custom_action_bar);
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

    /**
     * Update the database image for user
     * @param uri Image URI
     */
    private void updateDBImage(Uri uri) throws IOException {
        mDBManager.databaseOpenToWrite();
        if(mDBManager.updateUserImageByID(uri.getPath(),sSharedPreferences.getLong(Constants.CURRENT_USER_ID,-1))){
            setProfilePic();
            Log.i(Constants.APP_TAG,getString(R.string.log_update_complete));
        }else{
            Toast.makeText(this,getString(R.string.could_not_update),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case FILE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    Log.i(Constants.APP_TAG,uri.toString());
                    String mimeType = getContentResolver().getType(uri);

                    if(mimeType != null){
                        if(mimeType.equals("image/jpeg") || mimeType.equals("image/png")){
                            Log.i(Constants.APP_TAG,mimeType);
                            CropImage.activity(uri).start(this);
                        }else{
                            Toast.makeText(this,getString(R.string.select_image_file),Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        Toast.makeText(this,getString(R.string.internal_error),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(this, getString(R.string.file_chooser_error),Toast.LENGTH_SHORT).show();
                }
            break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == LoginActivity.RESULT_OK){
                    Uri resultUri = result.getUri();
                    Log.i(Constants.APP_TAG,resultUri.toString());
                    try {
                        updateDBImage(resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    try {
                        throw error;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            break;

            default: break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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