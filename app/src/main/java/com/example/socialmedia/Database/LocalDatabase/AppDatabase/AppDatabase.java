package com.example.socialmedia.Database.LocalDatabase.AppDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.socialmedia.Database.LocalDatabase.Dao.ChatDao;
import com.example.socialmedia.Database.LocalDatabase.Dao.PostsSavedDao;
import com.example.socialmedia.Database.LocalDatabase.Dao.UserDao;
import com.example.socialmedia.Database.LocalDatabase.Entity.ChatEntity;
import com.example.socialmedia.Database.LocalDatabase.Entity.PostsSavedEntity;
import com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity;

@Database(entities = {UserEntity.class, ChatEntity.class, PostsSavedEntity.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "local_database";

    public abstract UserDao userDao();
    public abstract ChatDao chatDao();
    public abstract PostsSavedDao savesPostsDao();

     private static AppDatabase INSTANCE;

     public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
