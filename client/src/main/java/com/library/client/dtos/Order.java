package com.library.client.dtos;

public class Order {
    private Integer id;
    private User user;
    private Book book;
    private Boolean isReady;
    private Boolean beenIssued;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }

    public Boolean getBeenIssued() {
        return beenIssued;
    }

    public void setBeenIssued(Boolean beenIssued) {
        this.beenIssued = beenIssued;
    }
}
