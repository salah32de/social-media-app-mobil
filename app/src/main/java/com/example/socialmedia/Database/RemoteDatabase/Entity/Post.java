package com.example.socialmedia.Database.RemoteDatabase.Entity;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class Post implements Serializable {
    private String idPost;//primary key
    private String idUser;//the user who posted
    private long date;
    private String text;
    private String link;//link the photo or video if any
    private long likes=0;
    private long numComments =0;

    private User userCreatePost;
    public Post() {}

    public Post(@NonNull long date, @NonNull String idUser,String link, String text) {
        this.date = date;
        this.idUser = idUser;
        this.link = link;
        this.text = text;
    }




    public long getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public User getUserCreatePost() {return userCreatePost;}

    public void setUserCreatePost(User userCreatePost) {
        this.userCreatePost = userCreatePost;
    }

}
