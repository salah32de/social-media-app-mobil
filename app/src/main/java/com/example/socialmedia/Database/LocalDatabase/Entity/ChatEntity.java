package com.example.socialmedia.Database.LocalDatabase.Entity;


import static com.example.socialmedia.Database.LocalDatabase.Entity.ChatEntity.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = TABLE_NAME, foreignKeys = {
        @androidx.room.ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "idUser1",
                onDelete = androidx.room.ForeignKey.CASCADE),
        @androidx.room.ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "idUser2",
                onDelete = androidx.room.ForeignKey.CASCADE
        )
})
public class ChatEntity {
    final static public String TABLE_NAME = "chat";

    @PrimaryKey
    @NonNull
    private String id;

    private String idUser1;
    private String idUser2;
    private String lastMessage;

    public ChatEntity(@NonNull String id, String idUser1, String idUser2,  String lastMessage) {
        this.id = id;
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.lastMessage = lastMessage;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUser1() { return idUser1; }
    public void setIdUser1(String idUser1) { this.idUser1 = idUser1; }

    public String getIdUser2() { return idUser2; }
    public void setIdUser2(String idUser2) { this.idUser2 = idUser2; }



    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}
