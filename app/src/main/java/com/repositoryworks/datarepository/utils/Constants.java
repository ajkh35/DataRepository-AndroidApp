package com.repositoryworks.datarepository.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import com.repositoryworks.datarepository.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by ajay3 on 7/3/2017.
 */

public class Constants {

    // Fragment number key for bundle arguments
    public static final String FRAGMENT_NUMBER = "FRAGMENT_NUMBER";

    // Item type helpers for Adapters
    public static final int ITEM_TYPE_VIEW = 0;
    public static final int ITEM_TYPE_PROGRESS = 1;

    // DB find User by -> filter parameter
    public static final int FIND_BY_EMAIL = 100;
    public static final int FIND_BY_USER_NAME = 101;

    // Show register on first app launch
    public static final String LOGIN_FRAGMENT_POSITION = "LOGIN_FRAGMENT_POSITION";

    // SharedPreferences Keys
    public static final String APP_PACKAGE = "com.repositoryworks.datarepository";
    public static final String APP_ACTIVITIES = "com.repositoryworks.datarepository.activities";

    // Logged-In Flag
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String CURRENT_USER_ID = "CurrentUser";

    // Application Flow Log Tags
    public static final String APP_TAG = "APP_TAG";

    /**
     * Get SD Card storage path
     * @return returns the path string
     */
    public static String getExternalStoragePath(){
        String path="";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path;
    }

    /**
     * Get internal storage path
     * @param context
     * @return return the path string
     */
    public static String getInternalStoragePath(Context context){
        String path="";
        path = context.getFilesDir().getAbsolutePath();
        return path;
    }

    /**
     * Get default image
     * @param context
     * @return returns the image byte array
     */
    public static byte[] getDefaultImage(Context context){
        Bitmap bmp = ((BitmapDrawable) context.getResources()
                .getDrawable(R.drawable.ic_account_circle_48pt_3x)).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}