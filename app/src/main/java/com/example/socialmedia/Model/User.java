package com.example.socialmedia.Model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

//we should the Serializable because we use intent.putExtra("user",user) and gson.toJson(user)
//  @SerializedName("id") is the name id in the json file
public class User implements Serializable        {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("photoProfile")
    private String photoProfile="";
    @SerializedName("bio")
    private String bio="";

    @SerializedName("number of notification unread")
    private int numNotificationUnread=0;
    @SerializedName("name index")
    private List<String> listNameIndex;//list of name index for search
    @SerializedName("isOnline")
    private boolean isOnline=false;
    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("isAdmin")
    private boolean isAdmin;

    public User(){}

    public User(String bio,  String email,  String name, String photo) {
        this.bio = bio;
        this.email = email;
        this.name = name;
        this.photoProfile = photo;
        this.isActive=true;
        this.isAdmin=false;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<String> getListNameIndex() {
        return listNameIndex;
    }

    public void setListNameIndex(List<String> listNameIndex) {
        this.listNameIndex = listNameIndex;
    }



    public int getNumNotificationUnread() {
        return numNotificationUnread;
    }

    public void setNumNotificationUnread(int numNotificationUnread) {
        this.numNotificationUnread = numNotificationUnread;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null)return false;
        if(obj instanceof User){
            User user = (User) obj;
            if(this.getId().equals(user.getId()))return true;
        }
        return false;
    }
}

