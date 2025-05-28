package com.example.socialmedia.Database.LocalDatabase.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);
    @Query("SELECT * FROM "+UserEntity.TABLE_NAME+" WHERE id = :id")
    UserEntity getUserById(String id);

    @Query("DELETE FROM "+UserEntity.TABLE_NAME+" WHERE id = :id")
    void deleteUserById(String id);
    @Query("DELETE FROM "+UserEntity.TABLE_NAME)
    void deleteAllUsers();
}
