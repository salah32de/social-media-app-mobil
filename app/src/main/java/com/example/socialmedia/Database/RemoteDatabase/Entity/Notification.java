package com.example.socialmedia.Database.RemoteDatabase.Entity;

public class Notification {
    public static final int LIKE_POST = 1;
    public static final int COMMENT_POST = 2;
    public static final int LIKE_COMMENT = 4;
    public static final int FRIEND_REQUEST = 5;
    public static final int ACCEPT_FRIEND_REQUEST = 6;
    public static final int REJECT_FRIEND_REQUEST = 7;
    public static final int SENT_MESSAGE = 8;

    public static final String LIKE_POST_TEXT = "liked your post";
    public static final String COMMENT_POST_TEXT = "commented on your post";
    public static final String LIKE_COMMENT_TEXT = "liked your comment";
    public static final String FRIEND_REQUEST_TEXT = "sent you a friend request";
    public static final String FRIEND_ACCEPTED_TEXT = "accepted your friend request";
    public static final String FRIEND_REJECTED_TEXT = "rejected your friend request";
    public static final String SENT_MESSAGE_TEXT = "sent you a message";


    private String idNotification;
    private String idNotify;//Something that interacted with it
    private String idUserNotify;
    private int typeNotification;
    private String textNotification = null;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification() {
    }

    public Notification(String idUserNotify, String idNotify, int typeNotification) {
        this.idUserNotify = idUserNotify;
        this.idNotify = idNotify;
        this.typeNotification = typeNotification;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public String getIdNotify() {
        return idNotify;
    }

    public void setIdNotify(String idNotify) {
        this.idNotify = idNotify;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public String getIdUserNotify() {
        return idUserNotify;
    }

    public void setIdUserNotify(String idUserNotify) {
        this.idUserNotify = idUserNotify;
    }

    public int getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(int typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getTextTypeNotification() {
        String text = "";
        switch (typeNotification) {
            case LIKE_POST:
                text = LIKE_POST_TEXT;
                break;
            case COMMENT_POST:
                text = COMMENT_POST_TEXT;
                break;
            case LIKE_COMMENT:
                text = LIKE_COMMENT_TEXT;
                break;
            case FRIEND_REQUEST:
                text = FRIEND_REQUEST_TEXT;
                break;
            case ACCEPT_FRIEND_REQUEST:
                text = FRIEND_ACCEPTED_TEXT;
                break;
            case REJECT_FRIEND_REQUEST:
                text = FRIEND_REJECTED_TEXT;
                break;
            case SENT_MESSAGE:
                text = SENT_MESSAGE_TEXT;
                break;
        }
        return text;
    }
}
