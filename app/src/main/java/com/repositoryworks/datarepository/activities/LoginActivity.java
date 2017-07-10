package com.repositoryworks.datarepository.activities;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.activityAdapters.LoginPagerAdapter;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.animators.DepthPageTransformer;
import com.repositoryworks.datarepository.fragments.LoginFragment;
import com.repositoryworks.datarepository.fragments.PlaceHolderFragment;
import com.repositoryworks.datarepository.fragments.RegisterFragment;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,PlaceHolderFragment.OnFragmentInteractionListener {

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.title_strip)
    PagerTabStrip mTitleStrip;

    private DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Link to the database
        mDBManager = new DBManager(this);

        LoginPagerAdapter PageAdapter = new LoginPagerAdapter(this,getSupportFragmentManager());

        mTitleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
        mTitleStrip.setTextColor(Color.WHITE);
        mTitleStrip.setTabIndicatorColor(Color.WHITE);

        mPager.setAdapter(PageAdapter);
        mPager.setPageTransformer(true,new DepthPageTransformer());
        mPager.setCurrentItem(getIntent().getIntExtra(Constants.LOGIN_FRAGMENT_POSITION,0));
    }

    /**
     * Get an instance of the DBManager
     * @return returns pre-initiated DBManager
     */
    public DBManager getDBManager(){
        return mDBManager;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}