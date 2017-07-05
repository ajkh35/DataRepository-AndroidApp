package com.repositoryworks.datarepository.models;

/**
 * Created by ajay3 on 7/5/2017.
 */

public class MusicModel {

    private String Title;
    private String Artist;
    private String Album;
    private int Year;
    private String YoutubeID;

    public MusicModel(String title,String artist,String album,int year,String youtubeID){
        this.Title = title;
        this.Artist = artist;
        this.Album = album;
        this.Year = year;
        this.YoutubeID = youtubeID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public String getYoutubeID() {
        return YoutubeID;
    }

    public void setYoutubeID(String youtubeID) {
        YoutubeID = youtubeID;
    }
}