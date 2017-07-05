package com.repositoryworks.datarepository.models;

/**
 * Created by ajay3 on 7/5/2017.
 */

public class HomeModel {

    private String Title;
    private String Credits;
    private int Release;
    private String YoutubeID;

    public HomeModel(String title, String credits, int release, String youtubeid){
        this.Title = title;
        this.Credits = credits;
        this.Release = release;
        this.YoutubeID = youtubeid;
    }

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

    public int getRelease() {
        return Release;
    }

    public void setRelease(int release) {
        Release = release;
    }

    public String getYoutubeID() {
        return YoutubeID;
    }

    public void setYoutubeID(String youtubeID) {
        YoutubeID = youtubeID;
    }
}