package com.repositoryworks.datarepository.utils.dbaccess;

import android.provider.BaseColumns;

/**
 * Created by ajay3 on 7/6/2017.
 */

class UsersContract {

    private UsersContract() {

    }

    static class User implements BaseColumns{
        static final String TABLE_NAME = "user";
        static final String COLUMN_NAME_FIRST_NAME = "first_name";
        static final String COLUMN_NAME_LAST_NAME = "last_name";
        static final String COLUMN_NAME_USER_NAME = "user_name";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_PASSWORD = "password";
        static final String COLUMN_NAME_IMAGE = "image";
    }
}