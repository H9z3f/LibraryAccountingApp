package com.library.server.bodies.responses;

import com.library.server.entities.Book;

public class BookResponseBody {
    private boolean success;
    private String message;
    private Book book;

    public BookResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BookResponseBody(boolean success, Book book) {
        this.success = success;
        this.book = book;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
