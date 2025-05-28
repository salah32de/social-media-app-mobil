package com.example.socialmedia.Database.RemoteDatabase.Entity;

public class Comment {

    private String idComment;//primary key
    private String idUser;//user create comment
    private User userCreateComment;
    private String idPost;
    private int nmbLike;
    private long date;
    private String text;
    private String link;//link the photo if any
    private String idParent = "";//the comment is a reply to

    private String nameUserReply;
    private Long nmbReply= 0L;
    
    public Comment() {
    }

    public Comment(String idUser, String idPost, long date, String text, String idParent) {
        this.idUser = idUser;
        this.idPost = idPost;
        this.date = date;
        this.text = text;
        this.idParent = idParent;
    }

    public Long getNmbReply() {
        return nmbReply;
    }

    public void setNmbReply(Long nmbReply) {
        this.nmbReply = nmbReply;
    }

    public int getNmbLike() {
        return nmbLike;
    }

    public void setNmbLike(int nmbLike) {
        this.nmbLike = nmbLike;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public long getDate() {
        return date;
    }

    public User getUserCreateComment() {
        return userCreateComment;
    }

    public void setUserCreateComment(User userCreateComment) {
        this.userCreateComment = userCreateComment;
    }


    public String getNameUserReply() {
        return nameUserReply;
    }

    public void setNameUserReply(String nameUserReply) {
        this.nameUserReply = nameUserReply;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }


}
