package com.example.socialmedia.Database.LocalDatabase.Entity;


import static com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = TABLE_NAME , foreignKeys = {
        @androidx.room.ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "idUserSavedPost",
                onDelete = androidx.room.ForeignKey.CASCADE)
})
public class PostsSavedEntity {
    final static public String TABLE_NAME = "PostsSavedEntity";
    @PrimaryKey
    @NonNull
    private String postId;
    private String idUserCreatePost;//the user who posted
    private String idUserSavedPost;//the user who saved the post
    private long date;
    private String text;
    private String link;//link the photo or video if any
    private long likes;
    private long numComments ;


    public PostsSavedEntity(@NonNull String postId, @NonNull long date, @NonNull String idUserCreatePost, String idUserSavedPost, String link, String text,long likes,long numComments) {
        this.postId=postId;
        this.date = date;
        this.idUserSavedPost = idUserSavedPost;
        this.idUserCreatePost = idUserCreatePost;
        this.link = link;
        this.text = text;
        this.likes=likes;
        this.numComments=numComments;
    }



    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    public String getIdUserSavedPost() {
        return idUserSavedPost;
    }

    public void setIdUserSavedPost(String idUserSavedPost) {
        this.idUserSavedPost = idUserSavedPost;
    }

    public void setLikes(long likes) {
        this.likes = likes;
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

    public String getIdUserCreatePost() {
        return idUserCreatePost;
    }

    public void setIdUserCreatePost(String idUserCreatePost) {
        this.idUserCreatePost = idUserCreatePost;
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

}
