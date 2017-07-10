package com.repositoryworks.datarepository.utils.dbaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ajay3 on 7/6/2017.
 */

class UsersDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DataRepository.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersContract.User.TABLE_NAME + " (" +
                    UsersContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UsersContract.User.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_LAST_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_USER_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_EMAIL + " TEXT," +
                    UsersContract.User.COLUMN_NAME_PASSWORD + " TEXT," +
                    UsersContract.User.COLUMN_NAME_IMAGE + " BLOB);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersContract.User.TABLE_NAME;

    private static UsersDBHelper sDBHelper;
    private UsersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * SingleTon Instance
     * @param context
     * @return returns the class object
     */
    static UsersDBHelper getInstance(Context context){
        if(sDBHelper == null){
            sDBHelper = new UsersDBHelper(context);
        }
        return sDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}