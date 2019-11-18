package com.atharvainfo.nilkamal.model;

public class UserMessage {
    String message,sender,nickname,profileUrl;
    long createdAt;


    class Message {
        String message;
        User sender;
        long createdAt;
    }

    class User {
        String nickname;
        String profileUrl;
    }

    public UserMessage(String message, String sender, String nickname, String profileUrl){
        this.message = message;
        this.sender = sender;
        this.nickname = nickname;
        this.profileUrl = profileUrl;


    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
