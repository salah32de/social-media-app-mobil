package com.example.socialmedia.Database.LocalDatabase.Dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.socialmedia.Database.LocalDatabase.Entity.ChatEntity;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatEntity chat);

    @Query("SELECT * FROM "+ChatEntity.TABLE_NAME+" WHERE idUser1 = :userId OR idUser2 = :userId")
    List<ChatEntity> getAllChatsByIdUser(String userId);

    @Query("SELECT * FROM "+ChatEntity.TABLE_NAME+" WHERE id = :chatId")
    ChatEntity getChatById(String chatId);

    @Query("DELETE FROM "+ChatEntity.TABLE_NAME+" WHERE id = :chatId")
    void deleteChatById(String chatId);
    @Query("DELETE FROM "+ChatEntity.TABLE_NAME+" WHERE idUser1 = :idUser or idUser2 = :idUser")
    void deleteAllChatByIdUser(String idUser);

}
