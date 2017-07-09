package com.repositoryworks.datarepository;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.repositoryworks.datarepository.activities.LoginActivity;
import com.repositoryworks.datarepository.activities.StartActivity;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.dbaccess.DBManager;

public class MainActivity extends AppCompatActivity {

    Intent mIntent;
    public static final DisplayMetrics sDisplayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display width handy
        getWindowManager().getDefaultDisplay().getMetrics(sDisplayMetrics);

        final SharedPreferences PackagePreferences =
                getSharedPreferences(Constants.APP_PACKAGE,MODE_PRIVATE);

        final SharedPreferences ActivityPreferences =
                getSharedPreferences(Constants.APP_ACTIVITIES,MODE_PRIVATE);

        new AsyncTask<Void,Void,Void>(){

            boolean first_run;

            @Override
            protected void onPreExecute() {
                first_run = PackagePreferences.getBoolean("first run",true);
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(ActivityPreferences.getBoolean(Constants.IS_LOGGED_IN,false)){
                    mIntent = new Intent(MainActivity.this, StartActivity.class);
                }else{
                    mIntent = new Intent(MainActivity.this,LoginActivity.class);
                }

                if(first_run){
                    mIntent.putExtra(Constants.LOGIN_FRAGMENT_POSITION,1);
                    PackagePreferences.edit().putBoolean("first run",false).apply();
                }else{
                    mIntent.putExtra(Constants.LOGIN_FRAGMENT_POSITION,0);
                }

                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
                finish();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}