package com.repositoryworks.datarepository.activityAdapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.repositoryworks.datarepository.R;
import com.repositoryworks.datarepository.fragments.LoginFragment;
import com.repositoryworks.datarepository.fragments.PlaceHolderFragment;
import com.repositoryworks.datarepository.fragments.RegisterFragment;
import com.repositoryworks.datarepository.utils.Constants;

/**
 * Created by ajay3 on 7/5/2017.
 */

public class LoginPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitleArray;

    public LoginPagerAdapter(Activity activity, FragmentManager fm) {
        super(fm);
        mTitleArray = activity.getResources().getStringArray(R.array.loginTitleArray);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = getFragment(position);
        return (fragment != null) ? fragment : new PlaceHolderFragment();
    }

    @Override
    public int getCount() {
        return mTitleArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArray[position];
    }

    /**
     * Get fragment instance by it's position
     *
     * @param position
     * @return
     */
    @Nullable
    private Fragment getFragment(int position){
        switch(position){
            case 0:
                return LoginFragment.newInstance(position);
            case 1:
                return RegisterFragment.newInstance(position);
            default:
                return null;
        }
    }
}