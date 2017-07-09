package com.repositoryworks.datarepository.utils.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.repositoryworks.datarepository.models.UserModel;
import com.repositoryworks.datarepository.utils.Constants;
import com.repositoryworks.datarepository.utils.fileUtils.FileUtilities;

import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by ajay3 on 7/6/2017.
 */

public class DBManager {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private UsersDBHelper mDBHelper;

    public DBManager(Context context) {
        mContext = context;
        mDBHelper = UsersDBHelper.getInstance(context);
    }

    /**
     * Open database to write
     * @throws SQLException
     */
    public void databaseOpenToWrite() throws SQLException{
        mDatabase = mDBHelper.getWritableDatabase();
    }

    /**
     * Open database to read
     * @throws SQLException
     */
    public void databaseOpenToRead() throws SQLException{
        mDatabase = mDBHelper.getReadableDatabase();
    }

    /**
     * Close the database
     */
    public void databaseClose(){
        mDatabase.close();
    }

    /**
     * Create a new record into the database
     * @param model
     * @return ID of user added
     */
    public long createUser(UserModel model) throws NoSuchAlgorithmException,IOException {
        ContentValues values = new ContentValues();

        values.put(UsersContract.User.COLUMN_NAME_FIRST_NAME,model.getFirstName());
        values.put(UsersContract.User.COLUMN_NAME_LAST_NAME,model.getLastName());
        values.put(UsersContract.User.COLUMN_NAME_USER_NAME,model.getUserName());
        values.put(UsersContract.User.COLUMN_NAME_EMAIL,model.getEmail());
        values.put(UsersContract.User.COLUMN_NAME_PASSWORD,getMD5(model.getPassword()));

        byte[] image = FileUtilities.getFileBytes(model.getProfilePic());

        if(image != null){
            values.put(UsersContract.User.COLUMN_NAME_IMAGE,image);
        }else{
            values.put(UsersContract.User.COLUMN_NAME_IMAGE,Constants.getDefaultImage(mContext));
        }

        return mDatabase.insert(UsersContract.User.TABLE_NAME,null,values);
    }

    /**
     * Delete a user record by id
     * @param id
     * @return ID of user deleted
     */
    public long deleteUserByID(long id){

        String selection = UsersContract.User._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};

        mDatabase.beginTransaction();
        id = mDatabase.delete(UsersContract.User.TABLE_NAME,selection,selectionArgs);

        mDatabase.setTransactionSuccessful();
        mDatabase.close();

        return id;
    }

    /**
     * Update user by ID
     * @param id
     * @return returns true on success
     */
    public boolean updateUserImageByID(String imgPath,long id) throws IOException {

        int count = -1;
        byte[] image = FileUtilities.getFileBytes(imgPath);

        if(image != null){
            ContentValues values = new ContentValues();
            values.put(UsersContract.User.COLUMN_NAME_IMAGE,image);
            String where = UsersContract.User._ID+ "=?";
            String[] whereArgs = {String.valueOf(id)};

            count = mDatabase.update(UsersContract.User.TABLE_NAME,values,where,whereArgs);
        }else{}

        return count==1;
    }

    /**
     * Delete all users from the database
     */
    public void deleteAllUsers(){
        mDatabase.beginTransaction();
        mDatabase.delete(UsersContract.User.TABLE_NAME,null,null);

        mDatabase.setTransactionSuccessful();
        mDatabase.close();
    }

    /**
     * Checks if user exists to prevent SignUp duplicity
     * @param email
     * @param user_name
     * @return returns the answer to Register screen
     */
    public boolean checkForUser(String email, String user_name){

        boolean userExists = false;

        String[] projection = {
                UsersContract.User.COLUMN_NAME_EMAIL,
                UsersContract.User.COLUMN_NAME_USER_NAME
        };

        String selection = UsersContract.User.COLUMN_NAME_EMAIL + " = ?" + " OR " +
                UsersContract.User.COLUMN_NAME_USER_NAME + " = ?";

        String[] selectionArgs = {email,user_name};

        Cursor cursor = mDatabase.query(
                UsersContract.User.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,null,null
        );

        if(cursor != null){
            if(cursor.getCount() > 0){
                userExists = true;
            }
            cursor.close();
        }

        return userExists;
    }

    /**
     * LOGIN validation to check if details are correct
     *
     * @param email
     * @param password
     * @return True if correct details
     */
    public boolean validateUser(String email,String password) throws NoSuchAlgorithmException {

        boolean isUserValid = false;

        String[] projection = {
                UsersContract.User.COLUMN_NAME_EMAIL
        };

        String where = UsersContract.User.COLUMN_NAME_EMAIL+ " = ?" + " AND " +
                UsersContract.User.COLUMN_NAME_PASSWORD+ " = ?";

        String[] whereArgs = {email,getMD5(password)};

        Cursor cursor = mDatabase.query(
                UsersContract.User.TABLE_NAME,
                projection,
                where,
                whereArgs,
                null,null,null
        );

        if(cursor != null){
            if(cursor.getCount() == 1){
                isUserValid = true;
            }
            cursor.close();
        }

        return isUserValid;
    }

    /**
     * Find all users in the database
     * @return A list of users
     */
    public ArrayList<UserModel> findAllUsers(){

        ArrayList<UserModel> list = new ArrayList<>();

        String[] projection = {
                UsersContract.User.COLUMN_NAME_FIRST_NAME,
                UsersContract.User.COLUMN_NAME_LAST_NAME,
                UsersContract.User.COLUMN_NAME_USER_NAME,
                UsersContract.User.COLUMN_NAME_EMAIL,
                UsersContract.User.COLUMN_NAME_PASSWORD,
                UsersContract.User.COLUMN_NAME_IMAGE
        };

        Cursor cursor = mDatabase.query(UsersContract.User.TABLE_NAME,projection,null,null,null,null,null);

        if(cursor != null){
            cursor.moveToFirst();
            do{
                UserModel model = new UserModel("","","","","","");
                model.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_EMAIL)));
                model.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_FIRST_NAME)));
                model.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_LAST_NAME)));
                model.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_USER_NAME)));
                model.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_PASSWORD)));
                model.setImageBitmap(FileUtilities.getImageBitmap(
                        cursor.getBlob(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_IMAGE))
                ));
                list.add(model);
            }while (cursor.moveToNext());
            cursor.close();
        }

        return list;
    }

    /**
     * Search the database for a user by record ID
     * @param id
     * @return UserModel of the user
     */
    public UserModel findUserByID(long id){
        UserModel model = new UserModel("","","","","","");

        String[] projection = {
                UsersContract.User.COLUMN_NAME_FIRST_NAME, UsersContract.User.COLUMN_NAME_LAST_NAME,
                UsersContract.User.COLUMN_NAME_USER_NAME, UsersContract.User.COLUMN_NAME_EMAIL,
                UsersContract.User.COLUMN_NAME_PASSWORD, UsersContract.User.COLUMN_NAME_IMAGE
        };

        String selection = UsersContract.User._ID+ " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = null;
        if(id != -1){
            cursor = mDatabase.query(UsersContract.User.TABLE_NAME,projection,selection,selectionArgs,null,null,null);
        }

        if(cursor != null){
            cursor.moveToFirst();
            model.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_FIRST_NAME)));
            model.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_LAST_NAME)));
            model.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_USER_NAME)));
            model.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_PASSWORD)));
            model.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_EMAIL)));
            model.setImageBitmap(FileUtilities.getImageBitmap(
                    cursor.getBlob(cursor.getColumnIndexOrThrow(UsersContract.User.COLUMN_NAME_IMAGE)))
            );
            cursor.close();
        }

        return model;
    }

    /**
     * Get the logged in user id
     * @param email
     * @return returns the ID long value
     */
    public long getUserIDAfterLogin(String email){
        long id = 0;
        String[] projection = {UsersContract.User._ID};
        String where = UsersContract.User.COLUMN_NAME_EMAIL + " = ?";
        String[] whereArgs = {email};

        Cursor cursor = mDatabase.query(
                UsersContract.User.TABLE_NAME,
                projection,
                where,
                whereArgs,
                null,null,null
        );

        if(cursor != null){
            cursor.moveToFirst();
            id = cursor.getLong(cursor.getColumnIndexOrThrow(UsersContract.User._ID));
            cursor.close();
        }

        return id;
    }

    /**
     * Method to specify conditional columns
     * @param filter
     * @return Column name
     */
    @Contract(pure = true)
    private String getSelectionString(int filter){
        String selection = "";
        switch(filter){
            case 100:
                selection = UsersContract.User.COLUMN_NAME_EMAIL + " = ?";
                break;
            case 101:
                selection = UsersContract.User.COLUMN_NAME_USER_NAME + " = ?";
                break;
            default:
                break;
        }
        return selection;
    }

    /**
     * Method to get password MD5 hash
     *
     * @param pass
     * @return MD5 hash in String data type
     * @throws NoSuchAlgorithmException
     */
    @NonNull
    private String getMD5(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(pass.getBytes());
        byte[] digest = messageDigest.digest();
        char[] hash = Hex.encodeHex(digest);
        return String.valueOf(hash);
    }
}