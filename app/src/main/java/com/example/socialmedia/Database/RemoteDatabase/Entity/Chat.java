package com.example.socialmedia.Database.RemoteDatabase.Entity;

import java.io.Serializable;

public class Chat implements Serializable {
    public static final String CHAT_KEY="chatKey";
    private String idChat;
    private String idUser1;
    private String idUser2;
    private String lastMessage;
    private User userReceiver;
     public Chat() {
    }

    public Chat(String idUser1, String idUser2 ,String lastMessage) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.lastMessage=lastMessage;
     }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }


    
}
