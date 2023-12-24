package com.library.server.bodies.responses;

import com.library.server.entities.Book;
import com.library.server.entities.User;

import java.util.List;

public class UserResponseBody {
    private boolean success;
    private String message;
    private String jwt;
    private User user;
    private List<Book> books;

    public UserResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public UserResponseBody(boolean success, String jwt, User user, List<Book> books) {
        this.success = success;
        this.jwt = jwt;
        this.user = user;
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

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
