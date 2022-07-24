package com.example.bookshare.model;

import java.io.Serializable;

public class Conversation implements Serializable {

    public String chatImageText;
    public int messageCount;
    public String userFullName;
    public String userName;
    public int userId;

    public Conversation() {
    }

    public Conversation(String chatImageText, int messageCount, String userFullName, String userName, int userId) {
        this.chatImageText = chatImageText;
        this.messageCount = messageCount;
        this.userFullName = userFullName;
        this.userName = userName;
        this.userId = userId;
    }

    public String getChatImageText() {
        return chatImageText;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }
}
