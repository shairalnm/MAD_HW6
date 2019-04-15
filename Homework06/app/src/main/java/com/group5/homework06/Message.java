package com.group5.homework06;

import java.io.Serializable;
import java.util.Date;



public class Message implements Serializable {
   public String messageText, imageUrl, firstName, lastName;
   public Date dateTime;

    public Message() {
    }

    public Message(String messageText, String imageUrl, String firstName, String lastName, Date dateTime) {
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
    }
}
