package com.example.android04.common;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java class that represents an Album.
 * <p>Has a name/title.
 * Has a number of photos.
 * Has references to photos.
 *
 * @author Jason Huang
 */
public class Album implements Serializable {
    /**
     * List of photos in album
     */
    private ArrayList<Photo> photoList;

    /**
     * Number of photos in album
     */
    private int numPhotos = 0;

    /**
     * Name of album
     */
    private String albumName;

    /**
     * Album constructor
     * @param albumName String name of album
     */
    public Album(String albumName){
        this.albumName = albumName;
        photoList = new ArrayList<Photo>();
        numPhotos = photoList.size();
    }

    /**
     * Number of Photos getter
     * @return int - number of photos in album
     */
    public int getNumPhotos(){
        return photoList.size();
    }

    /**
     * Album name getter
     * @return name of the album
     */
    public String getName(){
        return albumName;
    }

    /**
     * Sets the name of the album
     * @param newName String to set the name of this album
     */
    public void setName(String newName){
        this.albumName = newName;
    }

    /**
     * Adds photo to the album
     * @param photo Photo object to add
     */
    public void addPhoto(Photo photo){
        photoList.add(photo);
    }

    /**
     * Removes photo from the album
     * @param photo Reference to photo object in photoList
     */
    public void removePhoto(Photo photo){
        photoList.remove(photo);
    }

    /**
     * Gets the photos of the album
     * @return The photos in the album
     */
    public ArrayList<Photo> getPhotos(){
        return photoList;
    }

    public boolean hasPhoto(Uri photoUri){
        String reg = "\\/ORIGINAL\\/NONE\\/image\\/.*\\/[0-9]*";
        String normalized_uri = photoUri.getPath().replaceAll(reg, "");
        return photoList
                .stream()
                .anyMatch(p -> {
                    String normalized_test = p.getPhotoUri().getPath().replaceAll(reg, "");
                    return normalized_uri.equals(normalized_test);
                });
    }

    /**
     * Gives string with album name
     * @return album name as string
     */
    @Override
    public String toString(){
        return albumName;
    }
}
