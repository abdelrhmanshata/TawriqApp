package com.example.tawriqapp.Model;

import java.io.Serializable;

public class Book  extends Item implements Serializable {

    private String BookAuthor;

    public Book() {
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor = bookAuthor;
    }
}
