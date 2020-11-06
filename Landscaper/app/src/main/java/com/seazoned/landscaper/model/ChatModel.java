package com.seazoned.landscaper.model;

/**
 * Created by root on 26/4/18.
 */

public class ChatModel {
    String senderid;
    String receiverid;
    String text;
    //String source;
    String date;
    String senderName;

    public ChatModel() {
    }

    public ChatModel(String senderid, String receiverid, String text,  String date,String senderName) {
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.text = text;
        //this.source = source;
        this.date = date;
        this.senderName = senderName;
    }

    public String getSenderid() {
        return senderid;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public String getText() {
        return text;
    }

    /*public String getSource() {
        return source;
    }*/

    public String getDate() {
        return date;
    }

    public String getSenderName() {
        return senderName;
    }
}
