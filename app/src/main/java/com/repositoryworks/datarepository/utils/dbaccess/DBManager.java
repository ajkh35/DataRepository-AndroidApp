package com.repositoryworks.datarepository.utils.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.repositoryworks.datarepository.models.UserModel;

/**
 * Created by ajay3 on 7/6/2017.
 */

public class DBManager {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private UsersDBHelper mDBHelper;

    public DBManager(Context context) {
        mContext = context;
        mDBHelper = new UsersDBHelper(context);
    }

    /**
     * INSERT a new record into the database
     * @param model
     * @return
     */
    public long insert(UserModel model){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersContract.User.COLUMN_NAME_FIRST_NAME,model.getFirstName());
        values.put(UsersContract.User.COLUMN_NAME_LAST_NAME,model.getLastName());
        values.put(UsersContract.User.COLUMN_NAME_USER_NAME,model.getUserName());
        values.put(UsersContract.User.COLUMN_NAME_EMAIL,model.getEmail());
        values.put(UsersContract.User.COLUMN_NAME_PASSWORD,model.getPassword());

        return db.insert(UsersContract.User.TABLE_NAME,null,values);
    }
}