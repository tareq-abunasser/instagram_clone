package com.example.myapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Photo implements Parcelable {

    private String caption;
    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;
    private String tags;
    private List<Like> likes;
    private ArrayList<Comment> comments;

    public Photo() {

    }

//
//    public Photo(String caption, String date_created, String image_path, String photo_id,
//                 String user_id, String tags, List<Like> likes) {
//        this.caption = caption;
//        this.date_created = date_created;
//        this.image_path = image_path;
//        this.photo_id = photo_id;
//        this.user_id = user_id;
//        this.tags = tags;
//        this.likes = likes;
//    }

    public Photo(String caption, String date_created, String image_path, String photo_id,
                 String user_id, String tags, List<Like> likes,ArrayList<Comment> comments) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.comments=comments;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    protected Photo(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(caption);
        parcel.writeString(date_created);
        parcel.writeString(image_path);
        parcel.writeString(photo_id);
        parcel.writeString(user_id);
        parcel.writeString(tags);
    }
}