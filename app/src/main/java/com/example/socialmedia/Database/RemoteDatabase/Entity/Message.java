package com.example.socialmedia.Database.RemoteDatabase.Entity;

public class Message {
    public static final String TEXT_TYPE = "textType";
    public static final String IMAGE_TYPE = "imageType";

    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;
    private String chatId;
    private String type;

    public Message(String chatId, String senderId, String receiverId, String content, String timestamp, String type) {

        this.chatId = chatId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Message() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
