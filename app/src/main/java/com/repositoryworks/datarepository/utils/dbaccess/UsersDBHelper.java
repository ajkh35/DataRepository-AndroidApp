package com.repositoryworks.datarepository.utils.dbaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ajay3 on 7/6/2017.
 */

class UsersDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UsersContract.User.TABLE_NAME + " (" +
                    UsersContract.User._ID + " INTEGER PRIMARY KEY," +
                    UsersContract.User.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_LAST_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_USER_NAME + " TEXT," +
                    UsersContract.User.COLUMN_NAME_EMAIL + " TEXT," +
                    UsersContract.User.COLUMN_NAME_PASSWORD + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UsersContract.User.TABLE_NAME;

    UsersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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