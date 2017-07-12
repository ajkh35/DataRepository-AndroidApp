package com.repositoryworks.datarepository.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ajay3 on 7/6/2017.
 */

public class UserModel implements Parcelable{
    private String FirstName;
    private String LastName;
    private String UserName;
    private String Email;
    private String Password;
    private String ProfilePic;
    private Bitmap ImageBitmap;

    public UserModel(String firstName,String lastName,String userName,String email,String password,
                     String profilePic){
        this.FirstName = firstName;
        this.LastName = lastName;
        this.UserName = userName;
        this.Email = email;
        this.Password = password;
        this.ProfilePic = profilePic;
    }

    protected UserModel(Parcel in) {
        FirstName = in.readString();
        LastName = in.readString();
        UserName = in.readString();
        Email = in.readString();
        Password = in.readString();
        ProfilePic = in.readString();
        ImageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

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

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public Bitmap getImageBitmap() {
        return ImageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        ImageBitmap = imageBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(UserName);
        dest.writeString(Email);
        dest.writeString(Password);
        dest.writeString(ProfilePic);
        dest.writeParcelable(ImageBitmap, flags);
    }
}