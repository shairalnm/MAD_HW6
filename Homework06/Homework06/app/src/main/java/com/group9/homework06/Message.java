package com.group9.homework06;

import java.io.Serializable;
import java.util.Date;

//Homework06
//Group 9
//Rockford Stoller
//Ryan Swaim

public class Message implements Serializable {
    String messageText, imageUrl, firstName, lastName;
    Date dateTime;

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
