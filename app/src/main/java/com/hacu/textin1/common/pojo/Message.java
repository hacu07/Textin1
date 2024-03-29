package com.hacu.textin1.common.pojo;

import com.google.firebase.database.Exclude;

public class Message {
    private String msg;
    private String sender; //quien lo envio
    private String photoUrl;

    //de uso local, no se mapean al obtener de firebase
    @Exclude
    private boolean sentByMe;
    @Exclude
    private String uid;
    @Exclude
    private boolean loaded; // si existe o no la carga de la imagen

    public Message() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Exclude
    public boolean isSentByMe() {
        return sentByMe;
    }

    @Exclude
    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public boolean isLoaded() {
        return loaded;
    }

    @Exclude
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return uid != null ? uid.equals(message.uid) : message.uid == null;
    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
