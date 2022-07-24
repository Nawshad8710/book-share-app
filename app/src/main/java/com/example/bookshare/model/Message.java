package com.example.bookshare.model;

public class Message {
    public int fromId;
    public int toId;
    public String dateAdded;
    public String timeAdded;
    public String message;
    public String senderName;

    public Message() {
    }

    public Message(int fromId, int toId, String dateAdded, String timeAdded, String message, String senderName) {
        this.fromId = fromId;
        this.toId = toId;
        this.dateAdded = dateAdded;
        this.timeAdded = timeAdded;
        this.message = message;
        this.senderName = senderName;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }
}
