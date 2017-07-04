package com.repositoryworks.datarepository.models;

/**
 * Created by ajay3 on 7/5/2017.
 */

public class HomeModel {

    private String Title;
    private String Credits;
    private String Release;
    private String YoutubeID;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCredits() {
        return Credits;
    }

    public void setCredits(String credits) {
        Credits = credits;
    }

    public String getRelease() {
        return Release;
    }

    public void setRelease(String release) {
        Release = release;
    }

    public String getYoutubeID() {
        return YoutubeID;
    }

    public void setYoutubeID(String youtubeID) {
        YoutubeID = youtubeID;
    }
}