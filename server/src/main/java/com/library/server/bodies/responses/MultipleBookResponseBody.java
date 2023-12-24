package com.library.server.bodies.responses;

import com.library.server.entities.Book;

import java.util.List;

public class MultipleBookResponseBody {
    private boolean success;
    private String message;
    private List<Book> books;

    public MultipleBookResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public MultipleBookResponseBody(boolean success, List<Book> books) {
        this.success = success;
        this.books = books;
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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
