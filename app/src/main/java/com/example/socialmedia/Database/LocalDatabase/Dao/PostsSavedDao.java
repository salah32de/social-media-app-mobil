package com.example.socialmedia.Database.LocalDatabase.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity;

import java.util.List;

@Dao
public interface PostsSavedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PostsSavedEntity savesPostsEntity);

    @Query("SELECT * FROM " + PostsSavedEntity.TABLE_NAME + " WHERE idUserSavedPost = :idUserSavedPost ORDER BY date DESC")
    List<PostsSavedEntity> getPost(String idUserSavedPost);

    @Query("DELETE FROM " + PostsSavedEntity.TABLE_NAME + " WHERE postId = :postId")
    void delete(String postId);

    @Query("DELETE FROM " + PostsSavedEntity.TABLE_NAME + " WHERE idUserSavedPost = :idUserSavedPost")
    void deleteAll(String idUserSavedPost);
}
