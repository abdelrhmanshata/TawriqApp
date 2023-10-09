package com.example.tawriqapp.Model;

public class MessageRoom {

    private String SenderID, ReceiverID, lastMessage, dateTime;

    public MessageRoom() {
    }

    public MessageRoom(String senderID, String receiverID, String lastMessage, String dateTime) {

        SenderID = senderID;
        ReceiverID = receiverID;
        this.lastMessage = lastMessage;
        this.dateTime = dateTime;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
