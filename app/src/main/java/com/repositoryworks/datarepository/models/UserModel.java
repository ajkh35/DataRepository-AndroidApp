package com.repositoryworks.datarepository.models;

/**
 * Created by ajay3 on 7/6/2017.
 */

public class UserModel {
    private String FirstName;
    private String LastName;
    private String UserName;
    private String Email;
    private String Password;

    public UserModel(String firstName,String lastName,String userName,String email,String password){
        this.FirstName = firstName;
        this.LastName = lastName;
        this.UserName = userName;
        this.Email = email;
        this.Password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
