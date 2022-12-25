package org.example.app.exceptions;

public class BookNotFoundException extends ItemNotFoundException {

    public BookNotFoundException(String s) {
        super(s);
    }
}
