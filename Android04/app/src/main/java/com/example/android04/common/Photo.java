package com.example.android04.common;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java class representing a photo.
 * <p>Has a date.
 * Has a list of tags(can be empty).
 * Has a caption(can be empty).
 *
 * @author Jason Huang
 */
public class Photo implements Serializable {
    /**
     * List of Tags for a Photo
     */
    private ArrayList<Tag> tagList;

    /**
     * Uri for the photo as a String
     */
    private String photoUriString;

    /**
     * Creates a new Photo object that holds the image
     *
     * @param uri Uri - location of the photo as a Uri
     */
    public Photo(Uri uri) {
        tagList = new ArrayList<>();
        this.photoUriString = uri.toString();
    }

    /**
     * Gets the list of tags associated with the photo.
     *
     * @return ArrayList of {@link Tag} objects representing the tags for the photo.
     */
    public ArrayList<Tag> getTags() {
        return tagList;
    }

    /**
     * Adds a tag to the photo
     *
     * @param tag - Reference to tag object to add to this photo
     */
    public void addTag(Tag tag) {
        tagList.add(tag);
    }

    /**
     * Removes a tag from a photo
     *
     * @param tag - Reference to tag object to remove from this photo
     */
    public void removeTag(Tag tag) {
        tagList.removeIf(t -> t.equals(tag));
    }

    /**
     * Checks to see if photo has the specified tag
     *
     * @param tag - reference to tag object to search for
     * @return boolean if it has the tag
     */
    public boolean hasTag(Tag tag) {
        for (Tag t : tagList) {
            if (t.equals(tag)) return true;
        }
        return false;
    }

    /**
     * Get Uri for the photo as a String
     *
     * @return String - Uri as a string
     */
    public String getPhotoUriString() {
        return photoUriString;
    }


    /**
     * Get Uri for the photo
     *
     * @return Uri - Uri for the photo
     */
    public Uri getPhotoUri() {
        return Uri.parse(photoUriString);
    }

    /**
     * Set Uri for the photo
     *
     * @param uri Uri - Uri for the photo
     */
    public void setPhotoUri(Uri uri) {
        photoUriString = uri.toString();
    }
}
