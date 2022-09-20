package com.calmperson.simplep2pvoip.dto;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String text;
    private final String author;
    private final String date;


    public Message(String text, String author, String date) {
        this.text = text;
        this.author = author;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }
}
