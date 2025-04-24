package com.example.socialmedia.Database.LocalDatabase.Entity;

import static com.example.socialmedia.Database.LocalDatabase.Entity.UserEntity.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = TABLE_NAME)
public class UserEntity {
    final static public String TABLE_NAME = "users";
    @PrimaryKey
    @NonNull
    private String id;
    private String name;

    public UserEntity(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}