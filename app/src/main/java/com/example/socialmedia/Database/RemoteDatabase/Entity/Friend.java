package com.example.socialmedia.Database.RemoteDatabase.Entity;

public class Friend {
    private String id;
    private String idUser1;
    private String idUser2;
    private long status;

    public static final long REQUEST = 1;
    public static final long ACCEPT = 2;

    public Friend(String idUser1, String idUser2, long status) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.status = status;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Friend() {
    }

    public String getId() {
        return id;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setId(String id) {
        this.id = id;
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
