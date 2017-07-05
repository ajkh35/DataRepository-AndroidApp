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
        if(fragment != null){
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.FRAGMENT_NUMBER,position);
            fragment.setArguments(bundle);
        }else{
            fragment = new PlaceHolderFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitleArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArray[position];
    }

    @Nullable
    private Fragment getFragment(int position){
        switch(position){
            case 0:
                return new LoginFragment();
            case 1:
                return new RegisterFragment();
            default:
                return null;
        }
    }
}