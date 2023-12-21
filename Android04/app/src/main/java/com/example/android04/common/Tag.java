package com.example.android04.common;


import java.io.Serializable;

/**
 * Java class that represents a tag for a photo.
 * <p>A tag is a type-value pair. A tag can be singular. Singular tags can only be given that tag type to a photo once.
 *
 * @author Jason Huang
 */
public class Tag implements Serializable {
    /**
     * General category of tag
     */
    private String tagType;

    /**
     * Specific value under the category
     */
    private String tagValue;

    /**
     * Signal if a photo should only have a single type of this tag
     */
    private boolean singular;

    /**
     * Tag Constructor
     * @param tagType - String value: the tag type, i.e Location, Contributor, Source, Person, etc.
     * @param tagValue - String value: the tag value, i.e NYC, Person's Name, URL, etc.
     * @param singular - boolean value: indicates whether a tag is singular. A singular tag can only have its tag type assigned to a photo once,
     * i.e a photo can only be taken at one location, thus a location should be a singular tag.
     */
    public Tag(String tagType, String tagValue, boolean singular){
        this.tagType = tagType;
        this.tagValue = tagValue;
        this.singular = singular;
    }

    /**
     * Getter for tag type
     * @return String - type of the tag
     */
    public String getTagType(){
        return tagType;
    }

    /**
     * Getter for tag value
     * @return String - value of the tag type
     */
    public String getTagValue(){
        return tagValue;
    }

    /**
     * Getter for tag singularity
     * @return boolean - whether tag is Singular. A singular tag can only have one tagType of that type.
     */
    public boolean getSingularity(){
        return singular;
    }

    /**
     * Equals method for Tag
     * @param o any object but should be of type Tag
     */
    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag) o;
        return this.tagType.equals(tag.getTagType()) && this.tagValue.equalsIgnoreCase(tag.getTagValue());
    }

    /**
     * Override of string method.
     * @return String - in the format "Type: tagType --- Value: tagValue"
     */
    public String toString(){
        return tagType + " : " + tagValue;
    }
}